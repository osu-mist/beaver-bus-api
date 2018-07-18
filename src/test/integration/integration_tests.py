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
        valid_vehicles = test_get(self, "vehicles", "vehicle", True)

        # Test routeID parameter
        validate_filter_param(self, valid_vehicles, "vehicles", "routeID")

    # Test arrivals
    def test_arrivals(self):
        valid_arrivals = test_get(self, "arrivals", "arrival", False)

        # Test routeID parameter
        validate_filter_param(self, valid_arrivals, "arrivals", "routeID")

        # Test stopID parameter
        validate_filter_param(self, valid_arrivals, "arrivals", "stopID")


def validate_filter_param(self, res, type, param):
    try:
        valid_id = res.json()["data"][0]["attributes"][param]
        filtered_objects = utils.get(type, {param: valid_id})
        validate_response(self, filtered_objects, 200)
        self.assertIn(res.json()["data"][0], filtered_objects.json()["data"])
    except IndexError:
        warn("Can't test {param} parameter in /{type}. No {type} found".format(
            param=param, type=type
        ))


def test_get(self, type, res_type, has_id):
    # Test GET /type
    valid_types = utils.get(type)
    validate_response(self, valid_types, 200)
    check_null_fields(self, valid_types.json())

    # Test GET /type/{id}
    if has_id:
        try:
            valid_id = valid_types.json()["data"][0]["id"]
            valid_type = utils.get_by_id(type, valid_id)
            validate_response(self, valid_type, 200, res_type)
        except IndexError:
            warn("Can't test GET /{0}/{{id}} with valid ID. "
                 "No {0} found".format(type))
    return valid_types


# Checks that all fields of an object are not null
def check_null_fields(self, object):
    if isinstance(object, dict):
        for key, value in object.items():
            check_null_fields(self, value)
    elif isinstance(object, list):
        for value in object:
            check_null_fields(self, value)
    else:
        self.assertIsNotNone(object)


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
