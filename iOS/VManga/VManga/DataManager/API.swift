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
import FacebookCore

enum NetworkError: Error {
    case UnableToParseJSON, RequestURLError
}

struct API {
    static func postReadingManga(_id_manga: String) {
        guard let user = User.current else {
            return
        }
        
        let parameters: Parameters = [
            "user": user._id,
            "manga": _id_manga
        ]
        Alamofire.request("http://wannashare.info/api/v1/realtime", method: .post, parameters: parameters).responseJSON { response in
//            print(response.request)
//            print(response.value)
//            print(parameters)
            
        }
    }
    
    static func getActiveUsers() -> Promise<[User]> {
        return Promise { resolve, reject in
            Alamofire.request("http://wannashare.info/api/v1/realtime").responseJSON { response in
                if response.value == nil {
                    reject(NetworkError.UnableToParseJSON)
                    return
                }
                let json = JSON(response.value!)["data"]
                let users = json.arrayValue.map { json -> User in
                    var user = User(json: json["user"])
                    user.reading = json["manga"]["title"].stringValue
                    return user
                }
                resolve(users)
            }
        }
    }
    
    static func getUserWithFbToken(token: String) -> Promise<User> {
        return Promise { resolve, reject in
            Alamofire.request("http://wannashare.info/auth/facebook/token?access_token=\(token)").responseJSON { response in
                if response.value == nil {
                    reject(NetworkError.UnableToParseJSON)
                    return
                }
                let json = JSON(response.value!)
                resolve(User(json: json))
            }
        }
        
    }
    
    static func getMangaInfo(manga_id: Int) -> Promise<Book> {
        return Promise { resolve, reject in
            Alamofire.request("http://wannashare.info/api/v1/manga/\(manga_id)").responseJSON { response in
                if response.value == nil {
                    reject(NetworkError.UnableToParseJSON)
                    return
                }
                let json = JSON(response.value!)
                resolve(Book(json: json))
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
                let chapter = json["chapters"][chapterId]
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
                    return
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
                    return
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
                    return
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
