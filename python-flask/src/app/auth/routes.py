import uuid
from flask import request, make_response, jsonify
import numpy

import array as arr
from app.models import User
from auth import auth_bp
from auth.constants import application_json
from auth.utility import find_user, validate_request
from flask_httpauth import HTTPBasicAuth

auth = HTTPBasicAuth()
users = []


@auth_bp.route('/api/user', methods=["GET", "POST"])
def api_user():
    if request.method == "GET":
        request_args = request.args
        if len(request_args) == 0:
            response = make_response([user.to_json() for user in users], 200)
            response.headers["Content-Type"] = application_json
            return response
        if len(request_args) > 0:
            return find_user(request_args, users)
    if request.method == "POST":
        data = request.get_json()
        errors = validate_request(data, users)
        if errors is None:
            user_id = uuid.uuid4()
            users.append(User(login=data.get("login"), password=data.get("password"), firstname=data.get("firstname"),
                              lastname=data.get("lastname"), userId=user_id))
            response = make_response(
                jsonify({"id": user_id}),
                200
            )
            response.headers["Content-Type"] = application_json
            return response
        else:
            response = make_response()
            response.data = errors
            response.status = 400
            response.headers["Content-Type"] = application_json
            return response


@auth_bp.route('/api/greeting')
@auth.login_required
def auth_greet():
    return make_response(auth.current_user().greet(), 200)


@auth.verify_password
def verify_password(username, password):
    for user in users:
        if user.login == username and user.password == password:
            return user
    return False
