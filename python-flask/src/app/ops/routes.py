from ops import ops_bp


@ops_bp.route('/ops/ready')
def ready():
    return 'ok'


@ops_bp.route('/ops/start')
def start():
    return 'ok'


@ops_bp.route('/ops/live', methods=["GET"])
def live():
    return 'ok'
