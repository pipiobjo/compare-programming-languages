import json
from flask import Flask, request, jsonify, make_response
import uuid
import copy

app = Flask(__name__)

users = []


@app.route('/api/user', methods=["GET", "POST"])
def user():
    if request.method == "GET":
        request_args = request.args
        if len(request_args) == 0:
            response = make_response([clean_user(cleaned_user) for cleaned_user in copy.deepcopy(users)], 200)
            response.headers["Content-Type"] = "application/json"
            return response
        if len(request_args) > 0:
            return find_user(request_args)
    if request.method == "POST":
        data = request.get_json()
        errors = validate_request(data)
        if errors is None:
            user_id = uuid.uuid4()
            response = make_response(
                jsonify({"id": user_id}),
                200
            )
            data["id"] = user_id
            response.headers["Content-Type"] = "application/json"
            users.append(data)
            return response
        else:
            response = make_response()
            response.data = errors
            response.status = 400
            response.headers["Content-Type"] = "application/json"
            return response


def clean_user(user):
    user.pop("id")
    user.pop("password")
    return user


def find_user(request_arguments):
    response = make_response()
    response.headers["Content-Type"] = "application/json"
    response.status = 200
    if "login" in request_arguments:
        response.data = json.dumps(
            [clean_user(found_user) for found_user in copy.deepcopy(users) if
             found_user["login"] == request_arguments["login"]])
    elif "firstname" in request_arguments:
        response.data = json.dumps([clean_user(found_user) for found_user in copy.deepcopy(users) if
                                    found_user["firstname"] == request_arguments["firstname"]])
    elif "lastname" in request_arguments:
        response.data = json.dumps(
            [clean_user(found_user) for found_user in copy.deepcopy(users) if
             found_user["lastname"] == request_arguments["lastname"]])
    else:
        response.data = json.dumps({"Error": "Unrecognized query parameter"})
        response.status = 400

    return response


def validate_request(data):
    if len([existing for existing in users if existing["login"] == data["login"]]) > 0:
        return json.dumps({'error': 'user already exists'})

    if "login" not in data:
        return json.dumps({'error': 'missing login data'})


@app.route('/api/greeting')
def auth_greet():
    username = request.authorization["username"]
    password = request.authorization["password"]
    found_user = find_user_by_name_password(username, password)
    if found_user is not None:
        return make_response(
            {"msg": "Hello {} {}!".format(found_user["firstname"], found_user["lastname"]),
             "firstname": found_user["firstname"], "lastname": found_user["lastname"]},
            200
        )
    else:
        return make_response(
            {"error": "user not found or password incorrect"},
            400
        )


def find_user_by_name_password(username, password):
    for searched_user in users:
        if searched_user["password"] == password and searched_user["login"] == username:
            return searched_user
    return None


@app.route('/ops/ready')
def ready():
    return 'ok'


@app.route('/ops/start')
def start():
    return 'ok'


@app.route('/ops/live')
def live():
    return 'ok'


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8080, debug=False)
