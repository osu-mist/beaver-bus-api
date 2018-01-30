package edu.oregonstate.mist.api.jsonapi

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode
class ResourceObject {
    String id
    String type
    def attributes
    def links
}