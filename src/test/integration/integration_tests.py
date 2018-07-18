import json
import sys
import unittest
import utils
from logging import warn


class TestStringMethods(unittest.TestCase):

    # Test routes
    def test_routes(self):
        test_get(self, "routes", "route", True)

    # Test vehicles
    def test_vehicles(self):
        test_get(self, "vehicles", "vehicle", True)

    # Test arrivals
    def test_arrivals(self):
        test_get(self, "arrivals", "arrival", False)


def test_get(self, type, res_type, has_id):
    # Test GET /type
    valid_types = utils.get(type)
    validate_response(self, valid_types, 200)

    # Test GET /type/{id}
    if has_id:
        try:
            valid_id = valid_types.json()["data"][0]["id"]
            valid_type = utils.get_by_id(type, valid_id)
            validate_response(self, valid_type, 200, res_type)
        except IndexError:
            warn("Can't test GET /{0}/{{id}} with valid ID. "
                 "No {0} found".format(type))


def validate_response(self, res, code=None, res_type=None, message=None):
    if code:
        self.assertEqual(res.status_code, code)
    if res_type:
        self.assertEqual(res.json()["data"]["type"], res_type)
    if message:
        self.assertIn(message, res.json()[0]["developerMessage"])


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.input_file))
    utils.set_local_vars(config)
    sys.argv = args
    unittest.main()
