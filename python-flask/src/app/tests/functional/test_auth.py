import json
from uuid import UUID

mockUser = {
    "login": "testUser",
    "password": "testPassword",
    "firstname": "testFirstName",
    "lastname": "testLastName"
}


def test_empty_user_with_fixture(test_client):
    response = test_client.get("/api/user")
    assert response.data == b'[]\n'
    assert response.status_code == 200


def test_new_user_with_fixture(test_client):
    response = test_client.post("/api/user", data=json.dumps(mockUser), headers={"Content-Type": "application/json"})

    assert response.status_code == 200

    assert validate_uuid(response.json["id"])


def validate_uuid(uuid_string):
    try:
        val = UUID(uuid_string, version=4)
    except ValueError:
        return False

    return val.hex == uuid_string.replace("-", "")


def test_user_already_exists_with_fixture(test_client):
    test_client.post("/api/user", data=json.dumps(mockUser), headers={"Content-Type": "application/json"})
    response_two = test_client.post("/api/user", data=json.dumps(mockUser), headers={"Content-Type": "application/json"})

    assert response_two.status_code == 400
    assert response_two.data == b'{"Error": "user already exists"}'

