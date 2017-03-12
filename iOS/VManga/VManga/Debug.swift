//
//  Debug.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//
import SocketIO

func debug() {
//    API.getMangaInfo(manga_id: 13821).then { book in
//        print(book)
//    }
    let socket = SocketIOClient(socketURL: URL(string: "http://wannashare.info:3030")!)
    
    socket.on("connect") {data, ack in
        print("socket connected")
    }
    
    socket.on("api/v1/realtime created") {data, ack in
        if let cur = data[0] as? Double {
            socket.emitWithAck("canUpdate", cur).timingOut(after: 0) {data in
            }
        }
        print(data)
    }
    
    socket.connect()
}
