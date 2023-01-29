import copy
import uuid
from flask import request, make_response, jsonify

from app.models import User
from auth import auth_bp
from auth.constants import application_json
from auth.utility import find_user, validate_request, find_user_by_name_password

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
            users.append(User(login=data["login"], password=data["password"], firstname=data["firstname"],
                              lastname=data["lastname"], userId=user_id))
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
def auth_greet():
    username = request.authorization["username"]
    password = request.authorization["password"]
    found_user = find_user_by_name_password(username, password, users)
    if found_user is not None:
        return make_response(
            found_user.greet(),
            200
        )
    else:
        return make_response(
            {"error": "user not found or password incorrect"},
            400
        )
