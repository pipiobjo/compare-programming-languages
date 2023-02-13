import Chart from 'chart.js/auto';
import reportFiles from './loadtest-results.json';

const loadTestData = {};
const requestCountData = {};
const buildDurationData = {};
const imageSizeData = {};
const cpuDataSet = {};
const memDataSet = {};
const buttonArrray = [];
const errors = {};


function formatBytes(bytes, decimals = 2) {
    if (!+bytes) return 0;
    const k = 1024;
    const dm = 2;
    const result = parseFloat((bytes / Math.pow(k, 2)).toFixed(dm))
    return result;

}


function prepareContainerImageSizesData(prefix, containerImageData){
    const imageSize = containerImageData["image-size"];

    imageSizeData

    if(!imageSizeData.labels){
        imageSizeData.labels=[];
    }

    if(!imageSizeData.datasets){
        imageSizeData.datasets=[];
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

function prepareBuildDurationData(prefix, buildDuration) {

    if(!buildDurationData.labels){
        buildDurationData.labels=[];
    }

    if(!buildDurationData.datasets){
        buildDurationData.datasets=[{
            "label": "buildDuration",
            "data": [],
        }];
    }
    buildDurationData.labels.push(prefix);
    buildDurationData.datasets[0].data.push(buildDuration.buildDuration);

}

function prepareLoadTestData(prefix, loadTestResults){
    const httpRequestDurationAvg = loadTestResults.metrics["http_req_duration"].values.avg;
    const httpRequestDurationMax = loadTestResults.metrics["http_req_duration"].values.max;
    const httpRequestDurationMed = loadTestResults.metrics["http_req_duration"].values.med;
    const httpRequestDurationMin = loadTestResults.metrics["http_req_duration"].values.min;
    const httpRequestDurationP90 = loadTestResults.metrics["http_req_duration"].values["p(90)"];
    const httpRequestDurationP95 = loadTestResults.metrics["http_req_duration"].values["p(95)"];
    const totalRequests = loadTestResults.metrics["http_reqs"].values.count;
    const totalErrors = loadTestResults.metrics["http_req_failed"].values.passes;


    // prepare request count data
    if(!requestCountData.labels){
        requestCountData.labels=[];
    }
    requestCountData.labels.push(prefix);

    if(!requestCountData.datasets){
        requestCountData.datasets=[{
            "label": "totalRequests",
            "data": [],
        }, {
            "label": "failedRequests",
            "data": [],
        }];
    }

    requestCountData.datasets[0].data.push(totalRequests);
    requestCountData.datasets[1].data.push(totalErrors);



    // prepare duration data
    if(!loadTestData.labels){
        loadTestData.labels=["average", "median", "max", "min", "p90", "p95"];
    }

    if(!loadTestData.datasets){
        loadTestData.datasets=[];
    }
    loadTestData.datasets[loadTestData.datasets.length]= {
        label: prefix,
        data: [httpRequestDurationAvg, httpRequestDurationMed, httpRequestDurationMax, httpRequestDurationMin, httpRequestDurationP90, httpRequestDurationP95]
    };



}

function unifyValues(values){
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


function unifyValuesMemory(values){
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

function prepareMemData(prefix, perfData){
    const values = perfData.data.result[0].values;
    let unifiedVal = unifyValuesMemory(values);

    if(!memDataSet.labels){
        memDataSet.labels=[];
    }

    if(!memDataSet.datasets){
        memDataSet.datasets=[];
    }

    memDataSet.labels.push(prefix);
    memDataSet.datasets.push(
        {
            label: prefix,
            data: unifiedVal,
        },
    );

}

function prepareCpuData(prefix, perfData){
    const values = perfData.data.result[0].values;
    let unifiedVal = unifyValues(values);

    if(!cpuDataSet.labels){
        cpuDataSet.labels=[];
    }

    if(!cpuDataSet.datasets){
        cpuDataSet.datasets=[];
    }

    cpuDataSet.labels.push(prefix);
    cpuDataSet.datasets.push(
        {
            label: prefix,
            data: unifiedVal,
        });
}

async function loadServiceData(prefix, serviceReports) {
    const loadTestResultsPath = serviceReports["loadtest-results"];
    const containerImageSizePath = serviceReports["container-image-size"];
    const perfCPUPath = serviceReports["perf-cpu"];
    const perfMemPath = serviceReports["perf-mem"];
    const buildDurationPath = serviceReports["build-duration"];
    const loadTestHtml = serviceReports["loadtest-results.html"]

    const responsesJSON = await Promise.all([
        fetch(loadTestResultsPath),
        fetch(containerImageSizePath),
        fetch(perfCPUPath),
        fetch(perfMemPath),
        fetch(buildDurationPath),
    ]);
    // const loadTestHtmlFetched = await fetch(loadTestHtml)
    //     .then(res => res.url)
    const [loadTestResults, containerImageSize, perfCPU, perfMem, buildDuration] = await Promise.all(responsesJSON.map(r => r.json()));
    // console.log("--------------------------------------------------------------------------------")
    // console.log("loadtest", loadTestResults);
    // console.log("containerImages", containerImageSize);
    // console.log('cpu', perfCPU);
    // console.log('mem', perfMem);
    // console.log('buildDuration', buildDuration);
    // console.log("--------------------------------------------------------------------------------")
    // await getFailedRequestsWithCallback(loadTestHtml, loadTestHtmlFetched, prefix, addHtmlFilesAndButtons)

    prepareLoadTestData(prefix, loadTestResults);
    prepareContainerImageSizesData(prefix, containerImageSize);
    prepareCpuData(prefix,perfCPU);
    prepareMemData(prefix,perfMem);
    prepareBuildDurationData(prefix, buildDuration);



}



async function prepareChartData(){
    for (const key in reportFiles) {

        const serviceReports = reportFiles[key];
        await loadServiceData(key, serviceReports)

    }
}

var requestDurationChart;

(async function() {

    await prepareChartData();

    console.log("start drawing charts");
    // container sizes
    console.log("image sizes", imageSizeData)
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

    //build duration
    console.log("build-duration-data", buildDurationData)
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
    new Chart(
        document.getElementById('request_count_chart'),
        {
            type: 'bar',
            data: requestCountData,
            options: {
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
                        type: 'logarithmic',
                        title: {
                            display: true,
                            text: 'Amount of Requests',
                        },
                    }
                }
            }
        });

    // request duration charts
    console.log("request duration data", loadTestData)
    requestDurationChart = new Chart(
        document.getElementById('http_req_duration'),
        {
            type: 'bar',
            options: {
              responsive: true,
              categoryPercentage: 0.8,
              barPercentage: 0.8,
              plugins: {
                  beforeInit: function(chart, options) {
                      chart.legend.afterFit = function() {
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

})();
