//
//  HHAccountViewController.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit
import FacebookLogin
import FacebookCore
import SocketIO

class HHAccountViewController: UIViewController, UITableViewDelegate {
    @IBOutlet weak var activeUsersTableView: UITableView!
    var users = [User]()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = colors.background
        activeUsersTableView.delegate = self
        activeUsersTableView.dataSource = self
        addLoginButton()
        createSocket()
    }

}

extension HHAccountViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//        return book.numberOfChapters
        return users.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = activeUsersTableView.dequeueReusableCell(withIdentifier: "UserTableViewCell", for: indexPath) as! HHActiveUserTableViewCell
        let user = users[indexPath.row]
        if let mangaName = user.reading {
            cell.statusLabel.text = "\(user.name!) is reading \(mangaName)"
        } else {
            cell.statusLabel.text = "\(user.name) isn't reading any manga"
        }
        
        return cell
    }
}

extension HHAccountViewController {
    fileprivate func renderTable() {
        API.getActiveUsers().then { users -> Void in
            self.users = users
            self.activeUsersTableView.reloadData()
        }.catch { e in print(e) }
    }
    
    fileprivate func createSocket() {
        let socket = SocketIOClient(socketURL: URL(string: "http://wannashare.info:3030")!)
        
        socket.on("connect") {data, ack in
            self.renderTable()
        }
        
        socket.on("api/v1/realtime created") {data, ack in
            if let cur = data[0] as? Double {
                socket.emitWithAck("canUpdate", cur).timingOut(after: 0) {data in
                }
            }
            print("get socket emit")
            self.renderTable()
        }
        socket.connect()
    }
    
    fileprivate func addLoginButton() {
        let loginButton = LoginButton(readPermissions: [ .publicProfile, .email, .userFriends ])
        loginButton.center = CGPoint(x: view.center.x, y: view.frame.height - 100)
        loginButton.delegate = self
        view.addSubview(loginButton)
        
        User.setCurrentUser()
    }
}

extension HHAccountViewController: LoginButtonDelegate {
    func loginButtonDidCompleteLogin(_ loginButton: LoginButton, result: LoginResult) {
        User.setCurrentUser()
    }
    
    func loginButtonDidLogOut(_ loginButton: LoginButton) {
        
    }
}
