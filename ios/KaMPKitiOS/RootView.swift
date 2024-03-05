//
//  RootView.swift
//  KaMPKitiOS
//
//  Created by Leonid Gomberg on 05.03.2024.
//  Copyright © 2024 Touchlab. All rights reserved.
//

import SwiftUI

@available(iOS 16.0, *)
struct RootView: View {
    @StateObject private var coordinator = Coordinator()

    var body: some View {
        NavigationStack(path: $coordinator.path) {
            VStack {
                BreedListScreen()
            }
            .navigationDestination(for: String.self) { routeId in
                if routeId == String(describing: BreedListScreen.self) {
                    BreedListScreen()
                } else if routeId == String(describing: DetailsScreen.self) {
                    let arg0 = coordinator.routeParams[0]
                    (arg0 as? String).map {
                        DetailsScreen(breed: $0)
                    }
                }
            }
        }
        .environmentObject(coordinator)
    }
}
