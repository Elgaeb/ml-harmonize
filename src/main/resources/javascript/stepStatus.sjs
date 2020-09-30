var jobId, stepNumber, page, pageSize;

const start = (page - 1) * pageSize;

const jobDoc = fn.head(cts.search(cts.andQuery([
    cts.collectionQuery(["Job"]),
    cts.jsonPropertyRangeQuery("jobId", "=", jobId),
]))).toObject();

const stepResponse = jobDoc.job.stepResponses[stepNumber];

const query = cts.andQuery([
    cts.collectionQuery(["Batch"]),
    cts.jsonPropertyRangeQuery("jobId", "=", jobId),
    cts.jsonPropertyRangeQuery("stepNumber", "=", stepNumber)
]);

const batches = fn.subsequence(cts.search(query, [
    "unfiltered",
    cts.indexOrder(cts.jsonPropertyReference("timeStarted"), ["descending"])
]), start + 1, pageSize).toArray().map(doc => {
    doc = doc.toObject();
    return doc.batch;
    return  {
        batchId: doc.batch.batchId,
        timeStarted: doc.batch.timeStarted,
        timeEnded: doc.batch.timeEnded,
        batchStatus: doc.batch.batchStatus,
    };
});

const total = cts.estimate(query);

const out = {
    stepResponse,
    start,
    pageSize,
    total,
    batches,
};

out