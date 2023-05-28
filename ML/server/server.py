import json
import logging
import re
from http.server import BaseHTTPRequestHandler, HTTPServer

import make_classification

class LocalData(object):
    records = {}



class HTTPRequestHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        if re.search('/api/post/*', self.path):
            length = int(self.headers.get('content-length'))
            data = self.rfile.read(length).decode('utf8')
            classes = make_classification.process(json.loads(data))

            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(classes.encode(encoding='utf_8'))
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
    logging.info('Starting httpd...\n')
    print('Starting httpd...\n')
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    server.server_close()
    logging.info('Stopping httpd...\n')