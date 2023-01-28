import copy

from flask import make_response, json


def clean_user(user):
    user.pop("id")
    user.pop("password")
    return user


def find_user(request_arguments, users):
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


def validate_request(data, users):
    if len([existing for existing in users if existing["login"] == data["login"]]) > 0:
        return json.dumps({'error': 'user already exists'})

    if "login" not in data:
        return json.dumps({'error': 'missing login data'})



def find_user_by_name_password(username, password, users):
    for searched_user in users:
        if searched_user["password"] == password and searched_user["login"] == username:
            return searched_user
    return None
