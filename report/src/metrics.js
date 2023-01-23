import Chart from 'chart.js/auto';
import reportFiles from './loadtest-results.json';

const loadTestData = [];
const imageSizeData = [];
const cpuDataSet = {};
const memDataSet = {};


function formatBytes(bytes, decimals = 2) {
    if (!+bytes) return 0;
    const k = 1024;
    const dm = 2;
    const result = parseFloat((bytes / Math.pow(k, 2)).toFixed(dm))
    return result;

}


function prepareContainerImageSizesData(prefix, containerImageData){
    const imageSize = containerImageData["image-size"];
    console.log("containerImageSize " + prefix, imageSize);
    imageSizeData.push({
        "name": prefix,
        "value": formatBytes(imageSize)
    });
}



function prepareLoadTestData(prefix, loadTestResults){
    const httpRequestDurationAvg = loadTestResults.metrics["http_req_duration"].values.avg;
    const httpRequestDurationMax = loadTestResults.metrics["http_req_duration"].values.max;
    const httpRequestDurationMed = loadTestResults.metrics["http_req_duration"].values.med;
    const httpRequestDurationMin = loadTestResults.metrics["http_req_duration"].values.min;
    const httpRequestDurationP90 = loadTestResults.metrics["http_req_duration"].values["p(90)"];
    const httpRequestDurationP95 = loadTestResults.metrics["http_req_duration"].values["p(95)"];

    loadTestData.push({
        "name": prefix,
        "value-avg": httpRequestDurationAvg,
        "name-max": prefix + "-avg",
        "value-max": httpRequestDurationMax,
        "name-med": prefix + "-med",
        "value-med": httpRequestDurationMed,
        "name-min": prefix + "-min",
        "value-min": httpRequestDurationMin,

        "name-p90": prefix + "-p90",
        "value-p90": httpRequestDurationP90,
        "name-p95": prefix + "-p95",
        "value-p95": httpRequestDurationP95,
    });
}

function unifyValues(values){
    console.log("values", values);
    const result = [];
    var min = Math.min(...values.map(item => item[0]));
    console.log("min:", min);
    values.map(item => {
        result.push({
            x: item[0] - min,
            y: item[1],
        })
    });
    return result;


}

function prepareMemData(prefix, perfData){
    console.log("mem-perfData", perfData)
    const values = perfData.data.result[0].values;
    let unifiedVal = unifyValues(values);
    console.log("unified values", unifiedVal);

    if(!memDataSet.labels){
        memDataSet.labels=[];
    }

    memDataSet.labels.push(prefix);
    memDataSet.datasets = [
        {
            label: prefix,
            data: unifiedVal,
        },
    ];

}

function prepareCpuData(prefix, perfData){
    console.log("perfData", perfData)
    const values = perfData.data.result[0].values;
    let unifiedVal = unifyValues(values);
    console.log("unified values", unifiedVal);

    if(!cpuDataSet.labels){
        cpuDataSet.labels=[];
    }

    cpuDataSet.labels.push(prefix);
    cpuDataSet.datasets = [
        {
            label: prefix,
            data: unifiedVal,
        },
    ];
}

async function loadServiceData(prefix, serviceReports) {

    const loadTestResultsPath = serviceReports["loadtest-results"];
    const containerImageSizePath = serviceReports["container-image-size"];
    const perfCPUPath = serviceReports["perf-cpu"];
    const perfMemPath = serviceReports["perf-mem"];

    console.log(loadTestResultsPath, "loadTestResultsPath");
    console.log(containerImageSizePath, "containerImageSizePath");
    console.log(perfCPUPath, "perfCPUPath");
    console.log(perfMemPath, "perfMemPath");

    const responsesJSON = await Promise.all([
        fetch(loadTestResultsPath),
        fetch(containerImageSizePath),
        fetch(perfCPUPath),
        fetch(perfMemPath)
    ]);
    const [loadTestResults, containerImageSize, perfCPU, perfMem] = await Promise.all(responsesJSON.map(r => r.json()));
    console.log("loadtest", loadTestResults);
    console.log("containerImages", containerImageSize);
    console.log('cpu', perfCPU);
    console.log('mem', perfMem);


    prepareLoadTestData(prefix, loadTestResults);
    prepareContainerImageSizesData(prefix, containerImageSize);
    prepareCpuData(prefix,perfCPU);
    prepareMemData(prefix,perfMem);



}






(async function() {



    for (const key in reportFiles) {

        console.log(key, reportFiles[key]);
        const serviceReports = reportFiles[key];

        await loadServiceData(key, serviceReports)

    }


    // container sizes
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
                        type: 'category',
                        // title: {
                        //     display: true,
                        //     text: 'Services',
                        // },
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
            data: {
                labels: imageSizeData.map(row => row.name),
                datasets: [
                    {
                        label: imageSizeData.map(row => row["name"]),
                        data: imageSizeData.map(row => row["value"])
                    },
                    // {
                    //     label: 'Http Request Duration Max',
                    //     data: loadTestData.map(row => row["value-max"])
                    // },
                ]
            }
        }
    );


    // loadtest results
    new Chart(
        document.getElementById('http_req_duration_avg'),
        {
            type: 'bar',
            options: {

              plugins: {
                  legend: true,
                  title: {
                      display: true,
                      text: "Http Request Duration Avg"
                  },
              },
              scales: {
                  x: {
                      type: 'category',
                      // labels: ['java-pure']
                  }
              },
            },
            data: {
                labels: loadTestData.map(row => row.name),
                datasets: [
                    {
                        label: loadTestData.map(row => row["name"]),
                        data: loadTestData.map(row => row["value-avg"])
                    },
                    // {
                    //     label: 'Http Request Duration Max',
                    //     data: loadTestData.map(row => row["value-max"])
                    // },
                ]
            }
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
                            text: 'Memory Usage',
                        },
                    }
                },
            },
            data: memDataSet,
        }
    );


})();
