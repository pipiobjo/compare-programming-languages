import json
from ctypes import Union
from typing import List

import jsons


class User:
    def __init__(self, login, password, firstname, lastname, userId):
        self.firstname = firstname
        self.lastname = lastname
        self.login = login
        self.__password = password
        self.__userId = userId

    def __repr__(self):
        return repr("login: {0}, firstname: {1}, lastname: {2}".format(self.login, self.firstname, self.lastname))

    def to_json(self):
        return jsons.dump(self, strip_privates=True)

    def check_password_and_login(self, password_to_check, username_to_check) -> bool:
        return self.__password == password_to_check and self.login == username_to_check

    def greet(self):
        return json.dumps({"msg": "Hello {} {}!".format(self.firstname, self.lastname),
                           "firstname": self.firstname, "lastname": self.lastname})
