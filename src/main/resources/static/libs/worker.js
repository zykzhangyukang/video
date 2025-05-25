importScripts('/libs/spark-md5.min.js');

let totalChunks = 0;
let chunkHashes = [];
let receivedChunks = 0;

self.onmessage = function (e) {
    const data = e.data;

    if (data.type === 'init') {
        totalChunks = data.total;
        chunkHashes = new Array(totalChunks);
        receivedChunks = 0;
    }

    if (data.type === 'chunk') {
        const { index, buffer } = data;

        const spark = new SparkMD5.ArrayBuffer();
        spark.append(buffer);
        chunkHashes[index] = spark.end();
        receivedChunks++;

        self.postMessage({ type: 'progress', index });

        if (receivedChunks === totalChunks) {
            const fullStr = chunkHashes.join('');
            const finalHash = SparkMD5.hash(fullStr);
            self.postMessage({ type: 'final', finalHash });
        }
    }
};
