/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import React from 'react';

import App from './App';
import PageStructureSidebar from './components/PageStructureSidebar';

/**
 * Entry-point for "Page Structure" (sidebar pane) functionality.
 */
export default class PageStructure {
	static name = 'PageStructure';

	constructor({app, panel}) {
		this.Actions = app.Actions;
		this.dispatch = app.dispatch;
		this.title = panel.label;

		App.init(app);
	}

	activate() {
		const reducer = (state, action) => {
			const nextState = state;

			switch (action.type) {
				default:
					break;
			}

			return nextState;
		};

		this.dispatch(this.Actions.loadReducer(reducer, PageStructure.name));
	}

	deactivate() {
		this.dispatch(this.Actions.unloadReducer(PageStructure.name));
	}

	renderSidebar() {
		return <PageStructureSidebar title={this.title} />;
	}
}
