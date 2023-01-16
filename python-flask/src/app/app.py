from flask import Flask
app = Flask(__name__)

@app.route('/')
def index():
    return 'Server Works!'

@app.route('/greet')
def say_hello():
    return 'Hello from Server'

@app.route('/ops/ready')
def ready():
    return 'ok'
@app.route('/ops/start')
def start():
    return 'ok'
@app.route('/ops/live')
def live():
    return 'ok'

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8080, debug=True)
