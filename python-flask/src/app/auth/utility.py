import copy
from errors import constants as errors

from flask import make_response, json
from flask_httpauth import HTTPBasicAuth

auth = HTTPBasicAuth()


def find_user(request_arguments, users):
    response = make_response()
    response.headers["Content-Type"] = "application/json"
    response.status = 200
    if "login" in request_arguments:
        response.data = json.dumps(
            [found_user.to_json() for found_user in users if
             found_user.login == request_arguments["login"]])
    else:
        response.data = json.dumps({errors.error: errors.unrecognized_param})
        response.status = 400

    return response


def validate_request(data, users):
    if len([existing for existing in users if existing.login == data["login"]]) > 0:
        return json.dumps({errors.error: errors.user_exists_error})

    if "login" not in data:
        return json.dumps({errors.error: errors.missing_login})


def find_user_by_name_password(username, password, users):
    for searched_user in users:
        if searched_user.check_password_and_login(password_to_check=password, username_to_check=username) is True:
            return searched_user
    return None
