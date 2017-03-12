//
//  HHReadingViewController.swift
//  VManga
//
//  Created by Nguyen Le Vu Long on 3/11/17.
//  Copyright © 2017 mac. All rights reserved.
//

import UIKit
import SKPhotoBrowser
import SDWebImage
import PromiseKit

class HHReadingViewController: UIViewController  {
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    var images = [SKPhotoProtocol]()

    private func getPages(manga_id: Int, chapterId: Int) -> Promise<[SKPhotoProtocol]> {
        return Promise { resolve, reject in
            API.getChapter(manga_id: manga_id, chapterId: chapterId).then { pages -> Void in
                let skPages = pages.map({SKPhoto.photoWithImageURL($0)})
                resolve(skPages)
                }.catch { e in reject(e) }
        }
    }
    
    func loadChapter(manga_id: Int, chapterId: Int) {
        SKPhotoBrowserOptions.displayAction = false
        SKPhotoBrowserOptions.backgroundColor = colors.background
        
        getPages(manga_id: manga_id, chapterId: chapterId).then { pages -> Void in
            self.activityIndicator.stopAnimating()
            
            let browser = SKPhotoBrowser(photos: pages)
            browser.initializePageIndex(0)
            browser.delegate = self
            self.present(browser, animated: true, completion: nil)
        }.catch { e in print(e) }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = colors.background
        SKCache.sharedCache.imageCache = CustomImageCache()
    }
}

// MARK: - SKPhotoBrowserDelegate

extension HHReadingViewController: SKPhotoBrowserDelegate {
    func didDismissAtPageIndex(_ index: Int) {
        self.dismiss(animated: true, completion: nil)
    }
    
    func didDismissActionSheetWithButtonIndex(_ buttonIndex: Int, photoIndex: Int) {
    }
    
    func removePhoto(index: Int, reload: (() -> Void)) {
        SKCache.sharedCache.removeImageForKey("somekey")
        reload()
    }
}

class CustomImageCache: SKImageCacheable {
    var cache: SDImageCache
    
    init() {
        let cache = SDImageCache(namespace: "com.long.custom.cache")
        self.cache = cache
    }
    
    func imageForKey(_ key: String) -> UIImage? {
        guard let image = cache.imageFromDiskCache(forKey: key) else { return nil }
        
        return image
    }
    
    func setImage(_ image: UIImage, forKey key: String) {
        cache.store(image, forKey: key)
    }
    
    func removeImageForKey(_ key: String) {
    }
}
