import express from 'express'
import {v4} from 'uuid';

const app = express();
app.use(express.json());

const users = [];

const isAuth = (req, res, next) => {
    var encoded = req.headers.authorization.split(' ')[1];
    // decode it using base64
    var decoded = new Buffer(encoded, 'base64').toString();
    var login = decoded.split(':')[0];
    var password = decoded.split(':')[1];
    const foundUser = users.find(user => user.login === login);
    if (foundUser && foundUser.password === password) {
        req.loggedInUser = foundUser;
        next();
    } else {
        res.status(401);
        res.send('Access denied');
    }
}
const port = 8080;

app.all('/api/user', (req, res) => {
    if (req.method === 'POST') {
        const body = req.body
        const login = body['login'];
        if (users.find(user => user.login === login)) {
            res.status(400)
            res.send("user already exists")
        } else {
            const password = body['password'];
            const firstname = body['firstname'];
            const lastname = body['lastname'];
            const id = v4();
            users.push({lastname, firstname, password, login, id})
            res.json({id: id});
        }
    } else if (req.method === 'GET') {
        if (!req.query?.login) {
            res.send(users);
        } else {
            res.send(users.find(user => user.login === req.query.login) ?? [])
        }
    }
});

app.get('/api/greeting', isAuth, (req, res) => {
    res.send({
        msg: `Hello ${req.loggedInUser.firstname} ${req.loggedInUser.lastname}!`,
        firstname: req.loggedInUser.firstname,
        lastname: req.loggedInUser.lastname
    })
})

app.get('/ops/ready', (req, res) => {
    req.send('ok');
})


app.get('/ops/start', (req, res) => {
    req.send('ok');
})

app.get('/ops/live', (req, res) => {
    req.send('ok');
})


app.listen(port, () => {
    console.log(`Listening on port ${port}`);
});