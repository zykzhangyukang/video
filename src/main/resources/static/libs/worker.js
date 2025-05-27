importScripts('spark-md5.min.js');

self.onmessage = async (e) => {
    const {
        file,
        CHUNK_SIZE,
        startChunkIndex: start,
        endChunkIndex: end,
    } = e.data;
    const proms = []
    for (let i = start; i < end; i++) {
        proms.push(createChunk(file, i, CHUNK_SIZE))
    }
    const chunks =  await Promise.all(proms)
    self.postMessage(chunks)
}

function createChunk(file, index, chunkSize) {
    return new Promise((resolve, reject) => {
        const start = index * chunkSize;
        const end = start + chunkSize;
        const spark = new SparkMD5.ArrayBuffer();
        const fileReader = new FileReader();
        const blob = file.slice(start, end)
        fileReader.onload = e => {
            spark.append(e.target.result)
            resolve({
                start,
                end,
                index,
                hash: spark.end(),
                blob
            })
        }
        fileReader.readAsArrayBuffer(blob)
    })
}
