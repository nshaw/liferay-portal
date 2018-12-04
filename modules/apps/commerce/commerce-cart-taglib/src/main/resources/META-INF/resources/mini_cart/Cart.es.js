import 'clay-icon';

import debounce from 'debounce';
import Component from 'metal-component';
import Soy, {Config} from 'metal-soy';
import debounce from 'metal-debounce';

import template from './Cart.soy';

import './CartProduct.es';
import './Summary.es'; x;

class Cart extends Component {

	created() {
		fetch(this.cartAPI + '/' + this.cartId, {
			method: 'GET'
		})
			.then(response => response.json())
			.then(cartState => {
				this.cartId = cartState.cartId;
				this.products = cartState.products;
				this.summary = cartState.summary;
			});
	}

	toggleCart() {
		return (this.isOpen = !this.isOpen);
	}

	attached() {
		window.Liferay.on('addProductsToCart', (evt) => {
			const newProducts = evt.details[0];
			this.products = [
				...this.products,
				...newProducts
			];
			this.productsCount = this.productsCount + newProducts.length;
		});
		return (this.productsCount = this.products.length);
	}

	syncPendingOperations(pendingOperations) {
		this.isLoading = !!pendingOperations.length;
	}

	normalizeProducts(rawProducts) {
		const normalizedProducts = rawProducts.map(productData => {
			return Object.assign(
				{
					sendUpdateRequest: debounce(
						() => this.sendUpdateRequest(productData.id),
						500
					),
					sendDeleteRequest: debounce(
						() => this.sendDeleteRequest(productData.id),
						500
					)
				},
				productStateSchema,
				productData
			);
		});
		return normalizedProducts;
	}

	updateProductQuantity(productId, quantity) {
		this.addPendingOperation(productId);
		this.setProductProperties(productId, {
			isDeleteDisabled: true,
			quantity: quantity,
			isUpdating: true
		});
		this.getProductProperty(productId, 'sendUpdateRequest')();
	}

	handleSubmitQuantity(productId, quantity) {
		this.setProductProperties(productId, {inputChanged: false});
		this.updateProductQuantity(productId, quantity);
	}

	deleteProduct(productId) {
		this.productsCount = this.productsCount - 1;
	}

	setProductProperties(productId, newProperties) {
		this.products = this.products.map(product =>
			product.id === productId ?
				Object.assign({}, product, newProperties) :
				product
		);
	}

	getProductProperty(productId, key) {
		return this.products.reduce((property, product) => {
			if (property) {
				return property;
			}
			if (product.id === productId) {
				try {
					return product[key];
				}
				catch (error) {
					console.warn(`Property ${key} not found!`);
					return undefined;
				}
			}
		}, false);
	}

	subtractProducts(orArray, subArray) {
		const result = subArray.reduce((arrayToBeFiltered, elToRemove) => {
			return arrayToBeFiltered.filter((elToCheck) => elToCheck.id !== elToRemove.id);
		}, orArray);
	}

	handleDeleteItem(productId) {
		const isDeleteDisabled = this.getProductProperty(
			productId,
			'isDeleteDisabled'
		);

		if (isDeleteDisabled) {
			return false;
		};

		this.setProductProperties(
			productId, {
				isDeleteDisabled: true,
				isDeleting: true
			}
		);

		setTimeout(() => {
			const isDeleting = this.getProductProperty(productId, 'isDeleting');
			if (isDeleting) {
				this.setProductProperties(
					productId, 
					{
						isCollapsed: true
					}
				);
				this.getProductProperty(productId, 'sendDeleteRequest')();
			}
		}, 2000);
	}

	handleCancelItemDeletion(productId) {
		this.setProductProperties(productId, {
			isDeleting: false,
			isDeleteDisabled: false
		});
		this.removePendingOperation();
	}

	addPendingOperation(productId) {
		if (!this.pendingOperations.includes(productId)) {
			return (this.pendingOperations = [...this.pendingOperations, productId]);
		}
		return false;
	}

	removePendingOperation(productId) {
		return (this.pendingOperations = this.pendingOperations.filter(
			el => el !== productId
		));
	}

	sendUpdateRequest(productId) {
		return fetch(this.cartAPI + '/' + productId, {
			body: JSON.stringify({
				quantity: this.getProductProperty(productId, 'quantity')
			}),
			headers: new Headers({'Content-Type': 'application/json'}),
			method: 'POST'
		})
			.then(response => response.json())
			.then(updatedCartState => {
				const updatedPrice = updatedCartState.products.reduce(
					(acc, el) => (el.id === productId ? el.price : acc),
					null
				);
				this.removePendingOperation(productId);
				this.setProductProperties(productId, {
					isDeleteDisabled: false,
					isUpdating: false,
					price: updatedPrice
				});
				this.updateSummary(updatedCartState.summary);
			});
	}

	getProducts() {
		return fetch(this.cartAPI + '/' + this.cartId + '/' + productId, {
			body: JSON.stringify({
				quantity: this.getProductProperty(productId, 'quantity')
			}),
			headers: new Headers({'Content-Type': 'application/json'}),
			method: 'POST'
		})
			.then(response => response.json())
			.then(updatedCartState => {
				const updatedPrice = updatedCartState.products.reduce(
					(acc, el) => (el.id === productId ? el.price : acc),
					null
				);
				this.removePendingOperation(productId);
				this.setProductProperties(productId, {
					isDeleteDisabled: false,
					isUpdating: false,
					price: updatedPrice
				});
				this.updateSummary(updatedCartState.summary);
			});
	}

	sendDeleteRequest(productId) {
		this.addPendingOperation(productId);
		
		return fetch(
			this.cartAPI + '/' + productId, 
			{
				method: 'DELETE'
			}
		)
		.then(response => response.json())
		.then(
			updatedCartState => {
				this.removePendingOperation(productId);
				this.setProductProperties(
					productId, 
					{
						isDeleteDisabled: false
					}
				);

				this.updateSummary(updatedCartState.summary);

				const remainingProducts = this.subtractProducts(this.products, updatedCartState.products);

				this.deleteProduct(productId);
			}
		);
	}

	updateSummary(summary) {
		this.summary = summary;
	}
}

Soy.register(Cart, template);

const productStateSchema = {
	inputChanged: false,
	isDeleting: false,
	isDeleteDisabled: false,
	isCollapsed: false,
	isUpdating: false
};

Cart.STATE = {
	spritemap: {
		value: ''
	},
	cartAPI: {
		value: 'http://localhost:8080/o/commerce-cart'
	},
	isOpen: {
		value: true
	},
	isDisabled: {
		value: false
	},
	cartId: {
		value: null
	},
	products: {
		setter: 'normalizeProducts',
		value: null
	},
	productsCount: {
		value: 0
	},
	summary: {
		value: {
			checkoutUrl: '',
			subtotal: '',
			grandTotal: '',
			discount: '',
			totalUnits: 0
		}
	},
	isLoading: {
		value: false
	},
	pendingOperations: {
		value: []
	},
	detailsUrl: {
		value: ''
	}
};

export {Cart};
export default Cart;