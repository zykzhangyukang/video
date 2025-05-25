importScripts('spark-md5.min.js');

let chunkHashes = [];
let total = 0;
let received = 0;

onmessage = function (e) {
    const { type } = e.data;

    if (type === 'init') {
        total = e.data.total;
        received = 0;
        chunkHashes = new Array(total);
    }

    if (type === 'chunk') {
        const { index, buffer } = e.data;

        const spark = new SparkMD5.ArrayBuffer();
        spark.append(buffer);
        const hash = spark.end();

        chunkHashes[index] = hash;
        received++;

        postMessage({ type: 'progress', index });

        if (received === total) {
            const finalSpark = new SparkMD5();
            finalSpark.append(chunkHashes.join(''));
            const finalHash = finalSpark.end();

            postMessage({ type: 'final', finalHash });
        }
    }
};
