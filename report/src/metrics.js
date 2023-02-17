import Chart from 'chart.js/auto';
import {Grid} from 'ag-grid-community';
import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-alpine.css';

import reportFiles from './loadtest-results.json';

const loadTestData = {};
const requestCountData = {};
const requestCountTableData = [];
const buildDurationData = {};
const startupDurationData = {};
const imageSizeData = {};
const cpuDataSet = {};
const memDataSet = {};


function formatBytes(bytes, decimals = 2) {
    if (!+bytes) return 0;
    const k = 1024;
    const dm = 2;
    const result = parseFloat((bytes / Math.pow(k, 2)).toFixed(dm))
    return result;

}

function prepareContainerImageSizesData(prefix, containerImageData) {
    const imageSize = containerImageData["image-size"];

    imageSizeData

    if (!imageSizeData.labels) {
        imageSizeData.labels = [];
    }

    if (!imageSizeData.datasets) {
        imageSizeData.datasets = [];
    }
    imageSizeData.labels.push(prefix);
    imageSizeData.datasets.push({
        "label": prefix,
        "data": [{
            "y": formatBytes(imageSize),
            "x": prefix
        }]
    });
}

function prepareStartupDurationData(prefix, data) {

    if (!startupDurationData.labels) {
        startupDurationData.labels = [];
    }

    if (!startupDurationData.datasets) {
        startupDurationData.datasets = [{
            "label": "startupTime",
            "data": [],
        }];
    }
    startupDurationData.labels.push(prefix);
    startupDurationData.datasets[0].data.push(data.startup_time_in_seconds);


}

function prepareBuildDurationData(prefix, buildDuration) {

    if (!buildDurationData.labels) {
        buildDurationData.labels = [];
    }

    if (!buildDurationData.datasets) {
        buildDurationData.datasets = [{
            "label": "buildDuration",
            "data": [],
        }];
    }
    buildDurationData.labels.push(prefix);
    buildDurationData.datasets[0].data.push(buildDuration.buildDuration);

}

function prepareLoadTestData(prefix, loadTestResults) {
    console.log(loadTestResults)
    const httpRequestDurationAvg = loadTestResults.metrics["http_req_duration"].values.avg;
    const httpRequestDurationMax = loadTestResults.metrics["http_req_duration"].values.max;
    const httpRequestDurationMed = loadTestResults.metrics["http_req_duration"].values.med;
    const httpRequestDurationMin = loadTestResults.metrics["http_req_duration"].values.min;
    const httpRequestDurationP90 = loadTestResults.metrics["http_req_duration"].values["p(90)"];
    const httpRequestDurationP95 = loadTestResults.metrics["http_req_duration"].values["p(95)"];
    const totalRequests = loadTestResults.metrics["http_reqs"].values.count;
    const totalErrors = loadTestResults.metrics["http_req_failed"].values.passes;


    // prepare request count data
    if (!requestCountData.labels) {
        requestCountData.labels = [];
    }
    requestCountData.labels.push(prefix);

    if (!requestCountData.datasets) {
        requestCountData.datasets = [{
            "label": "totalRequests",
            "data": [],
        }, {
            "label": "failedRequests",
            "data": [],
        }];
    }

    requestCountData.datasets[0].data.push(totalRequests);
    requestCountData.datasets[1].data.push(totalErrors);

    requestCountTableData.push({
        "name": prefix,
        "totalRequests": totalRequests,
        "failedRequests": totalErrors,
        httpRequestDurationAvg,
        httpRequestDurationMed,
        httpRequestDurationMax,
        httpRequestDurationMin,
        httpRequestDurationP90,
        httpRequestDurationP95
    });


    // prepare duration data
    if (!loadTestData.labels) {
        loadTestData.labels = ["average", "median", "max", "min", "p90", "p95"];
    }

    if (!loadTestData.datasets) {
        loadTestData.datasets = [];
    }
    loadTestData.datasets[loadTestData.datasets.length] = {
        label: prefix,
        data: [httpRequestDurationAvg, httpRequestDurationMed, httpRequestDurationMax, httpRequestDurationMin, httpRequestDurationP90, httpRequestDurationP95]
    };


}

function unifyValues(values) {
    const result = [];
    var min = Math.min(...values.map(item => item[0]));
    values.map(item => {
        result.push({
            x: item[0] - min,
            y: item[1],
        })
    });
    return result;


}


function unifyValuesMemory(values) {
    const result = [];
    var min = Math.min(...values.map(item => item[0]));
    values.map(item => {
        result.push({
            x: item[0] - min,
            y: formatBytes(item[1]),
        })
    });
    return result;


}

function prepareMemData(prefix, perfData) {
    const values = perfData.data.result[0].values;
    let unifiedVal = unifyValuesMemory(values);

    if (!memDataSet.labels) {
        memDataSet.labels = [];
    }

    if (!memDataSet.datasets) {
        memDataSet.datasets = [];
    }

    memDataSet.labels.push(prefix);
    memDataSet.datasets.push(
        {
            label: prefix,
            data: unifiedVal,
        },
    );

}

function prepareCpuData(prefix, perfData) {
    const values = perfData.data.result[0].values;
    let unifiedVal = unifyValues(values);

    if (!cpuDataSet.labels) {
        cpuDataSet.labels = [];
    }

    if (!cpuDataSet.datasets) {
        cpuDataSet.datasets = [];
    }

    cpuDataSet.labels.push(prefix);
    cpuDataSet.datasets.push(
        {
            label: prefix,
            data: unifiedVal,
        });
}

const buildTable = async () => {

    const tableRows = []

    const tableColumns = [
        {headerName: 'Service', field: 'service'},
        {headerName: '# Successful Requests', field: 'successfulRequests', type: 'numberColumn'},
        {headerName: '# Failed Requests', field: 'failedRequests', type: 'numberColumn'},
    ]

    requestCountTableData.forEach(countItem => (
        tableRows.push({
            service: countItem.name,
            successfulRequests: countItem.totalRequests,
            failedRequests: countItem.failedRequests,
        })
    ));

    const defaultColDef = {
        width: 100,
        sortable: true,
        filter: 'agTextColumnFilter',
        resizable: true,
        wrapHeaderText: true,
        autoHeaderHeight: true,
    }

    const defaultColGroupDef = {
        marryChildren: true,
    }

    const columnTypes = {
        numberColumn: {
            width: 100,
            filter: 'agNumberColumnFilter',
            filterParams: {
                comparator: (filterValue, cellValue) => {
                    if (cellValue < filterValue) {
                        return -1;
                    }
                }
            }
        }
    }

    let gridOptions = {
        columnDefs: tableColumns,
        rowData: tableRows,
        defaultColDef,
        defaultColGroupDef,
        columnTypes
    }

    let gridDiv = document.querySelector('#myGrid');
    new Grid(gridDiv, gridOptions);
}

async function loadServiceData(prefix, serviceReports) {
    const loadTestResultsPath = serviceReports["loadtest-results"];
    const containerImageSizePath = serviceReports["container-image-size"];
    const perfCPUPath = serviceReports["perf-cpu"];
    const perfMemPath = serviceReports["perf-mem"];
    const buildDurationPath = serviceReports["build-duration"];
    const startupDurationPath = serviceReports["startup-time"];

    const responsesJSON = await Promise.all([
        fetch(loadTestResultsPath),
        fetch(containerImageSizePath),
        fetch(perfCPUPath),
        fetch(perfMemPath),
        fetch(buildDurationPath),
        fetch(startupDurationPath),
    ]);
    const results = await Promise.allSettled(responsesJSON.map(r => r.json()));

    const [ loadTestResults, containerImageSize, perfCPU, perfMem, buildDuration, startupDuration] = results.filter(result => !(result.status === 'rejected'))

    loadTestResults && prepareLoadTestData(prefix, loadTestResults.value);

    containerImageSize && prepareContainerImageSizesData(prefix, containerImageSize.value);
    perfCPU && prepareCpuData(prefix, perfCPU.value);
    perfMem && prepareMemData(prefix, perfMem.value);
    buildDuration && prepareBuildDurationData(prefix, buildDuration.value);
    startupDuration && prepareStartupDurationData(prefix, startupDuration.value);

}

async function prepareChartData() {
    for (const key in reportFiles) {

        const serviceReports = reportFiles[key];
        await loadServiceData(key, serviceReports)
    }

    await buildTable();
}

var requestDurationChart;

(async function () {

    await prepareChartData();

    console.log("start drawing charts");
    // container sizes
    console.log("image sizes", imageSizeData)
    if (!imageSizeData) {
        console.log("imageSizeData missing or error")
    }
    new Chart(
        document.getElementById('container_image_size'),
        {
            type: 'bar',
            options: {
                plugins: {
                    legend: true,
                    title: {
                        display: true,
                        text: "Container Image Size"
                    },
                },
                scales: {
                    x: {
                        display: true,
                        type: 'category',
                        title: {
                            display: true,
                            text: 'Services',
                        },
                    },
                    y: {
                        display: true,
                        type: 'linear',
                        title: {
                            display: true,
                            text: 'Size in MB',
                        },
                    }
                },
            },
            data: imageSizeData,
        }
    );

    // startup duration
    if (!startupDurationData) {
        console.log("startupDurationData missing or error")
    }
    console.log("startup-duration-data", startupDurationData)
    new Chart(
        document.getElementById('startup_duration_chart'),
        {
            type: 'bar',
            data: startupDurationData,
            options: {
                barValueSpacing: 2,
                plugins: {
                    title: {
                        display: true,
                        text: "Startup Duration"
                    },
                    legend: {
                        display: true,
                        position: "right",
                        fullWidth: true,

                    }
                },
                scales: {
                    x: {
                        display: true,
                        type: 'category',
                        title: {
                            display: true,
                            text: 'Services',
                        },
                    },
                    y: {
                        display: true,
                        type: 'linear',
                        title: {
                            display: true,
                            text: 'Startup duration in seconds',
                        },
                    }
                }
            }
        });

    //build duration
    console.log("build-duration-data", buildDurationData)
    if (!buildDurationData) {
        console.log("buildDurationData missing or error")
    }
    new Chart(
        document.getElementById('build_duration_chart'),
        {
            type: 'bar',
            data: buildDurationData,
            options: {
                barValueSpacing: 2,
                plugins: {
                    title: {
                        display: true,
                        text: "Build Duration"
                    },
                    legend: {
                        display: true,
                        position: "top",
                        fullWidth: true,

                    }
                },
                scales: {
                    x: {
                        display: true,
                        type: 'category',
                        title: {
                            display: true,
                            text: 'Services',
                        },
                    },
                    y: {
                        display: true,
                        type: 'linear',
                        title: {
                            display: true,
                            text: 'Build duration in seconds',
                        },
                    }
                }
            }
        });

    // request count chart
    if (!requestCountData) {
        console.log("requestCounTdata missing or error")
    }
    console.log("request_count-data", requestCountData)
    new Chart(
        document.getElementById('request_count_chart'),
        {
            type: 'bar',
            data: requestCountData,
            options: {
                responsive: true,
                barValueSpacing: 2,
                plugins: {
                    title: {
                        display: true,
                        text: "Http Request Counts - log scale"
                    },
                    legend: {
                        display: true,
                        position: "top",
                        fullWidth: true,

                    }
                },
                scales: {
                    x: {
                        display: true,
                        type: 'category',
                        title: {
                            display: true,
                            text: 'Services',
                        },
                    },

                    y: {
                        display: true,
                        type: 'linear',
                        title: {
                            display: true,
                            text: 'Amount of Requests',
                        },
                    }
                }
            }
        });

    // request duration charts
    console.log("request_duration-data", loadTestData)
    if (!loadTestData) {
        console.log("loadTestData missing or error")
    }
    requestDurationChart = new Chart(
        document.getElementById('http_req_duration'),
        {
            type: 'bar',
            options: {
                responsive: true,
                categoryPercentage: 0.8,
                barPercentage: 0.8,
                plugins: {
                    beforeInit: function (chart, options) {
                        chart.legend.afterFit = function () {
                            this.height = this.height + 50;
                        };
                    },
                    legend: {
                        display: true,
                        position: "right",
                        fullWidth: true,

                    },
                    title: {
                        display: true,
                        text: "Http Request Duration Avg (ms) - log scale"
                    },
                },
                scales: {
                    x: {
                        type: 'category',
                        labels: loadTestData.labels,
                        title: {
                            display: true,
                            text: 'Durations',
                        },
                    },
                    y: {
                        display: true,
                        type: 'logarithmic',
                        title: {
                            display: true,
                            text: 'Time in ms',
                        },
                    }
                },
            },
            data: loadTestData,

        }
    );


    // resource data
    // cpu - loadtest results
    if (!cpuDataSet) {
        console.log("cpuDataSet missing or error")
    }
    console.log("cpu_data", cpuDataSet);
    new Chart(
        document.getElementById('perf_cpu'),
        {
            type: 'line',
            options: {
                parsing: {
                    xAxisKey: 'x',
                    yAxisKey: 'y'
                },
                plugins: {
                    legend: true,
                    title: {
                        display: true,
                        text: "CPU Usage"
                    },
                },
                scales: {
                    x: {
                        type: 'linear',
                        title: {
                            display: true,
                            text: 'Time in seconds',
                        },
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'CPU Usage',
                        },
                    }
                },
            },
            data: cpuDataSet,
        }
    );


    console.log("memDataSet", memDataSet);
    if (!memDataSet) {
        console.log("memDataSet missing or error")
    } else {
        new Chart(
            document.getElementById('perf_mem'),
            {
                type: 'line',
                options: {
                    parsing: {
                        xAxisKey: 'x',
                        yAxisKey: 'y'
                    },
                    plugins: {
                        legend: true,
                        title: {
                            display: true,
                            text: "Memory Usage"
                        },
                    },
                    scales: {
                        x: {
                            type: 'linear',
                            title: {
                                display: true,
                                text: 'Time in seconds',
                            },
                        },
                        y: {
                            title: {
                                display: true,
                                text: 'Memory Usage in MB',
                            },
                        }
                    },
                },
                data: memDataSet,
            }
        );
    }

})();
