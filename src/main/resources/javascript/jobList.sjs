var page, pageSize;

function listJobs(page, pageSize) {
    const start = (page - 1) * pageSize;
    const query = cts.collectionQuery(["Job"]);
    const total = cts.estimate(query);
    const jobs = fn.subsequence(cts.search(query, [
            "unfiltered",
            cts.indexOrder(cts.jsonPropertyReference("timeStarted"), ["descending"])
        ]), start + 1, pageSize).toArray()
        .map(doc => doc.toObject().job)
        .map(job => ({
            timeStarted: job.timeStarted,
            jobId: job.jobId,
            flow: job.flow,
            jobStatus: job.jobStatus,
        }));
 
    return {
        total,
        page,
        pageSize,
        jobs
    };
}

listJobs(page, pageSize);

/*
cts.valueTuples([
    cts.jsonPropertyReference("timeStarted"),
    cts.jsonPropertyReference("jobId"),
    cts.jsonPropertyReference("flow"),
    cts.jsonPropertyReference("jobStatus"),
], [ "timezone=Z", "item-order", "descending", "limit=50" ], cts.collectionQuery(["Job"])).toArray();
*/