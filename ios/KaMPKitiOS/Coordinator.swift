//
//  Coordinator.swift
//  KaMPKitiOS
//
//  Created by Leonid Gomberg on 05.03.2024.
//  Copyright © 2024 Touchlab. All rights reserved.
//

import Foundation
import SwiftUI

@available(iOS 16.0, *)
class Coordinator: ObservableObject {
    @Published var path = NavigationPath()
    var routeParams = [Any](arrayLiteral: "", "", "")

    func show<V>(_ viewType: V.Type) where V: View {
        path.append(String(describing: viewType.self))
    }

    func popToRoot() {
        path.removeLast(path.count)
    }
}
