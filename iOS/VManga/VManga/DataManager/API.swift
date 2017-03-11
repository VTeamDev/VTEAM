//
//  API.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

import Alamofire
import SwiftyJSON
import PromiseKit

enum NetworkError: Error {
    case UnableToParseJSON
}

struct API {
    static func getMangaInfo(manga_id: Int) -> Promise<Book> {
        return Promise { resolve, reject in
            Alamofire.request("http://wannashare.info/api/v1/manga/\(manga_id)").responseJSON { response in
                guard let data = response.value else {
                    reject(NetworkError.UnableToParseJSON)
                    return
                }
                let json = JSON(data)
                let book = Book(id: json["manga_id"].intValue, thumbnail: json["thumbnail"].stringValue, title: json["title"].stringValue)
                resolve(book)
            }
        }
    }
    
    static func getChapter(manga_id: Int, chapterId: Int) -> Promise<[String]> {
        return Promise { resolve, reject in
            var pages = [String]()
            Alamofire.request("http://wannashare.info/api/v1/manga/\(manga_id)").responseJSON { response in
                guard let data = response.value else {
                    reject(NetworkError.UnableToParseJSON)
                    return
                }
                var json = JSON(data)
                json = json["chapters"][chapterId]
                var chapter: JSON!
                for (_, subJson):(String, JSON) in json {
                    chapter = subJson
                    break
                }
                pages = chapter["content"].arrayValue.map({$0.stringValue})
                resolve(pages)
            }
        }
    }
    
    static func getTop() -> Promise<[Book]> {
        return Promise { resolve, reject in
            var books = [Book]()
            
            Alamofire.request("http://wannashare.info/api/v1/list/top").responseJSON { response in
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
    
    static func getLatest() -> Promise<[Book]> {
        return Promise { resolve, reject in
            var books = [Book]()
            
            Alamofire.request("http://wannashare.info/api/v1/list/latest").responseJSON { response in
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
    
    static func getRecommend() -> Promise<[Book]> {
        return Promise { resolve, reject in
            var books = [Book]()
            
            Alamofire.request("http://wannashare.info/api/v1/list/recommend").responseJSON { response in
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
