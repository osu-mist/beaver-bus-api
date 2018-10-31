import json
import sys
import unittest
import utils
from logging import warning


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

    @classmethod
    def tearDownClass(self):
        for warn in warnings:
            warning(warn)


def validate_filter_param(self, res, type, param):
    try:
        valid_id = res.json()["data"][0]["attributes"][param]
        filtered_objects = utils.get(type, {param: valid_id})
        validate_response(self, filtered_objects, 200)
        for object in filtered_objects.json()["data"]:
            if object["attributes"][param] != valid_id:
                self.fail("{} object with {} of {} found. Should be {}".format(
                    type, param, object["attributes"][param], valid_id
                ))
    except IndexError:
        warnings.append(
            "Could not test {0} parameter in /{1}. No {1} found".format(
                param, type
            ))


def test_get(self, type, res_type, has_id):
    # Test GET /type
    valid_types = utils.get(type)
    validate_response(self, valid_types, 200)
    check_null_fields(self, valid_types.json())

    # Test GET /type/{id}
    if has_id:
        # Valid ID
        try:
            valid_id = valid_types.json()["data"][0]["id"]
            valid_type = utils.get_by_id(type, valid_id)
            validate_response(self, valid_type, 200, res_type)
        except IndexError:
            warnings.append("Could not test GET /{0}/{{id}} with valid ID. "
                            "No {0} found".format(type))
        # Invalid ID
        invalid_id = utils.get_by_id(type, utils.invalid_id)
        validate_response(self, invalid_id, 404, message="Not Found")
    return valid_types


# Checks that all fields of an object are not null
def check_null_fields(self, object):
    if isinstance(object, dict):
        for key, value in object.items():
            if key == "eta":
                # eta is the only field that can be null
                continue

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
        self.assertIn(message, res.json()["developerMessage"])


if __name__ == "__main__":
    namespace, args = utils.parse_args()
    config = json.load(open(namespace.input_file))
    utils.set_local_vars(config)
    sys.argv = args
    global warnings
    warnings = []
    unittest.main()
