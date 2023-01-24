import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";
import { describe, expect } from 'https://jslib.k6.io/k6chaijs/4.3.4.2/index.js';
import http from 'k6/http';
import { sleep, check } from 'k6';
import { SharedArray } from 'k6/data';

// define constants
const protocol = "http://";
const host = "localhost:48080";

const userEndpointPath = "/api/user";

const TEST_USERS = JSON.parse(open('./test-data/users.json'));


export function buildUrl(userName, password, path){
    let basicAuth = ""
    if (userName && password){
        basicAuth = `${userName}:${password}@`
    }
    const baseURL = `http://${basicAuth}localhost:48080${__ENV.MY_CONTEXT_PATH}${path}`;
    return baseURL;
}

// activate console and html results
export function handleSummary(data) {
    const reportOutput = `${__ENV.REPORT_FILE}`;
    const reportOutputJSON = `${__ENV.REPORT_FILE_JSON}`;
    return {
        [reportOutput]: htmlReport(data),
        [reportOutputJSON]: JSON.stringify(data),
        stdout: textSummary(data, { indent: " ", enableColors: true }),
    };
}

// read central configuration file
const sharedServiceConfig = new SharedArray('some name', function () {
    // All heavy work (opening and processing big files for example) should be done inside here.
    // This way it will happen only once and the result will be shared between all VUs, saving time and memory.
    const f = JSON.parse(open('./shared-service-config.json'));
    return f; // f must be an array
});


export const options = {
    stages: [
        // { duration: '2s', vus: 1, target: 0 },
        // { duration: '30s', vus: 5, target: 10 },
        { duration: '1m', vus: 5,  target: 10 },
    ],

}

function createUser(data){
    const postData = {
        "login": data.username,
        "password": data.password,
        "firstname": data.first_name,
        "lastname": data.last_name
    }
    const userURL = buildUrl(null, null, userEndpointPath);

    const res = http.post(userURL, JSON.stringify(postData), {
        headers: { 'Content-type': 'application/json' },
    });
    check(res, { 'status 200 or 400 if user already exists': (r) => r.status === 200 | r.status === 400 });

}

function createUsers() {
    console.log("start creating users");
    TEST_USERS.forEach((element, index, array) => {
        createUser(element);
    });
}

function listUsers() {
    const listUserURL = buildUrl(null, null, userEndpointPath);
    const res = http.get(listUserURL);
    expect(res.status, 'response status').to.equal(200);

    expect(res).to.have.validJsonBody();
    const users = res.json().length;

    console.log("existing users: ", users);

    return users;
}

export function setup() {
    // setup code called once before all virtual user test methods
    console.log("setup");
    const users = listUsers();

    if(users == 0){
        createUsers();
    }

    listUsers();

}


export function teardown(data) {
    // teardown code called once after all tests are executed
    console.log("teardown");

}

export function testUserGreetings(userObj){
    const greetingURL = buildUrl(userObj.username, userObj.password, "/api/greeting");

    const res = http.get(greetingURL);
    expect(res.status, 'response status').to.equal(200);

    expect(res).to.have.validJsonBody();
    check(res.json(), {
        'firstname is set': (r) => r.firstname === userObj["first_name"],
        'lastname is set': (r) => r.lastname === userObj["last_name"],
        'msg contains Hello firstname lastname!': (r) => r.msg === "Hello " + userObj["first_name"] + " " + userObj["last_name"] + "!"
    });

}



export default function () {
    TEST_USERS.forEach((element, index, array) => {
        testUserGreetings(element);
    });

}
