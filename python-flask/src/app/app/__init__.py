from flask import Flask


def create_app():
    app = Flask(__name__)

    from errors import bp as errors_bp
    app.register_blueprint(errors_bp, url_prefix="/")

    from ops import ops_bp
    app.register_blueprint(ops_bp, url_prefix="/")

    from auth import auth_bp
    app.register_blueprint(auth_bp, url_prefix="/")

    return app
