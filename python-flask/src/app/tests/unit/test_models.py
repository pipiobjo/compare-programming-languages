import json
import uuid

from app.models import User


def test_new_user():
    user = new_user_factory()

    assert user.firstname == "testFirstName"
    assert user.lastname == "testLastName"
    assert user.login == "testLogin"


def test_user_pw_login_check():
    user = new_user_factory("123", "123", "123", "123")
    assert user.check_password_and_login("123", "123")


def test_user_greet():
    user = new_user_factory("123", "123", "123", "123")
    assert user.greet() == json.dumps({"msg": "Hello {} {}!".format("123", "123"),
                                       "firstname": "123", "lastname": "123"})

def new_user_factory(first_name="testFirstName", last_name="testLastName", login="testLogin", password="testPassword"):
    user_id = uuid.uuid4()
    return User(firstname=first_name, lastname=last_name, login=login, password=password,
                userId=user_id)
