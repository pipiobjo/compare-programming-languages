import pytest

from app import create_app
from tests.TestConfig import TestConfig


@pytest.fixture(scope="module")
def test_client():
    flask_app = create_app(TestConfig)

    with flask_app.test_client() as client:
        yield client
