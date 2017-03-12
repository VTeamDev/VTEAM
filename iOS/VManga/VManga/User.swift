//
//  User.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/12/17.
//  Copyright © 2017 mac. All rights reserved.
//

import SwiftyJSON
import FacebookCore

struct User {
    static var current: User?
    
    var _id: String!
    var facebookId: String!
    var name: String!
    var email: String!
    var avatar: String!
    var reading: String?
    
    init() {}
    init(json: JSON) {
        _id = json["_id"].stringValue
        facebookId = json["facebookId"].stringValue
        name = json["name"].stringValue
        email = json["email"].stringValue
        avatar = json["avatars"].stringValue
        reading = nil
    }
    
    static func setCurrentUser() {
        if let accessToken = AccessToken.current {
            API.getUserWithFbToken(token: accessToken.authenticationToken).then { user -> Void in
                User.current = user
                }.catch { e in }
        }
    }
}
