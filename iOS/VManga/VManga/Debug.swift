//
//  Debug.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/10/17.
//  Copyright © 2017 mac. All rights reserved.
//

func debug() {

    SearchManager.search(name: "One piece").then { books -> Void in
        for book in books {
            print("\(book.id) \(book.title) \(book.thumbnail)")
        }
    }
    
}
