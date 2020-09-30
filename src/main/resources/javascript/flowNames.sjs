const flows = cts.search(cts.collectionQuery(["http://marklogic.com/data-hub/flow"]))
    .toArray()
    .map(flow => flow.toObject())
    .map(flow => flow.name)
    .sort()
;

flows;