//
//  SearchManager.swift
//  VManga
//
//  Created by mac on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

import Alamofire
import SwiftyJSON
import PromiseKit
import Foundation


struct SearchManager {
    static func search(name: String!) -> Promise<[Book]> {
        return Promise { resolve, reject in
            if name == nil {
                resolve([])
            }
            
            var books = [Book]()
            guard let name = name.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) else {
                reject(NetworkError.RequestURLError)
                return
            }
            
            Alamofire.request("http://wannashare.info/api/v1/list/search?name=\(name)").responseJSON { response in
                if response.request == nil {
                    reject(NetworkError.RequestURLError)
                }
                if response.value == nil {
                   reject(NetworkError.UnableToParseJSON)
                }
                
                let json = JSON(response.value!)
                for(_, json):(String, JSON) in json["data"] {
                    let book = Book(id: json["manga_id"].intValue, thumbnail: json["thumbnail"].stringValue, title: json["title"].stringValue)
                    books.append(book)
                }
                resolve(books)
            }
        }
    }
}
