//
//  Book.swift
//  VManga
//
//  Created by mac on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

import SwiftyJSON

struct Book {
    static let share = Book()
    
    var _id : String!
    var manga_id: Int!
    var thumbnail : String!
    var title : String!
    var description : String!
    var category : [String]!
    var numberOfChapters: Int = 0
    
    init() {}
    init(id: Int, thumbnail: String, title: String) {
        self.manga_id = id
        self.thumbnail = thumbnail
        self.title = title
    }
    init(json: JSON) {
        _id = json["_id"].stringValue
        manga_id = json["manga_id"].intValue
        thumbnail = json["thumbnail"].stringValue
        title = json["title"].stringValue
        description = json["content"].stringValue
        category = json["category"].arrayValue.map({$0.stringValue})
        numberOfChapters = json["chapters"].arrayValue.count
    }
}
