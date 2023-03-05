from flask import Blueprint

ops_bp = Blueprint('ops_bp', __name__)

from ops import routes
