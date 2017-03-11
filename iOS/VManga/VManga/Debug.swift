//
//  Debug.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

func debug() {
    API.getChapter(manga_id: 11909, chapterId: 0)
        .then { pages -> Void in
            print(pages)
        }.catch { e in
            print(e)
    }
}
