import json
import logging
import re
from http.server import BaseHTTPRequestHandler, HTTPServer
from jsonschema import validate

import make_classification

class LocalData(object):
    records = {}


json_shema={
    "$schema": "http://json-schema.org/draft-06/schema#",
    "type": "array",
    "items": {
        "$ref": "#/definitions/Welcome5Element"
    },
    "definitions": {
        "Welcome5Element": {
            "type": "object",
            "additionalProperties": False,
            "properties": {
                "id": {
                    "type": "integer"
                },
                "comment": {
                    "type": "string"
                }
            },
            "required": [
                "comment",
                "id"
            ],
            "title": "Welcome5Element"
        }
    }
}

class HTTPRequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if re.search('/api/post/*', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')
            try:
                date_to_process = json.loads(data, strict=False)
                validate(instance=date_to_process, schema=json_shema)
                classes = make_classification.process_data(date_to_process)
                self.send_response(200)
                self.send_header('Content-Type', 'application/json')
                self.end_headers()
                self.wfile.write(classes.encode(encoding='utf_8'))
            except Exception as e:
                logging.info(e)
                self.send_response(400)
        else:
            self.send_response(403)
        self.end_headers()

    def do_GET(self):
        if re.search('/api/get/*', self.path):
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()

        
if __name__ == '__main__':
    server = HTTPServer(('0.0.0.0', 6543), HTTPRequestHandler)
    logging.basicConfig(level=logging.INFO)
    logging.info('Starting httpd...\n')
    print('Starting httpd...\n')
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    server.server_close()
    logging.info('Stopping httpd...\n')