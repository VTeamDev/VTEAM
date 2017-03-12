//
//  User.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/12/17.
//  Copyright © 2017 mac. All rights reserved.
//

import SwiftyJSON

struct User {
    static var current: User?
    
    var _id: String!
    var facebookId: String!
    var name: String!
    var email: String!
    var avatar: String!
    
    init() {}
    init(json: JSON) {
        _id = json["_id"].stringValue
        facebookId = json["facebookId"].stringValue
        name = json["name"].stringValue
        email = json["email"].stringValue
        avatar = json["avatars"].stringValue
    }
}
