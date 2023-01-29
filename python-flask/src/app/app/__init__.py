from flask import Flask

from config import Config


def create_app(config_class=Config):
    app = Flask(__name__)
    app.config.from_object(config_class)

    from errors import bp as errors_bp
    app.register_blueprint(errors_bp, url_prefix="/")

    from ops import ops_bp
    app.register_blueprint(ops_bp, url_prefix="/")

    from auth import auth_bp
    app.register_blueprint(auth_bp, url_prefix="/")

    return app
