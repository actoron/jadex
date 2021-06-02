/* Riot v4.3.5, @license MIT */
(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
  typeof define === 'function' && define.amd ? define(factory) :
  (global = global || self, global.riot = factory());
}(this, function () { 'use strict';

  const COMPONENTS_IMPLEMENTATION_MAP = new Map(),
        DOM_COMPONENT_INSTANCE_PROPERTY = Symbol('riot-component'),
        PLUGINS_SET = new Set(),
        IS_DIRECTIVE = 'is',
        VALUE_ATTRIBUTE = 'value',
        ATTRIBUTES_KEY_SYMBOL = Symbol('attributes'),
        TEMPLATE_KEY_SYMBOL = Symbol('template');

  var globals = /*#__PURE__*/Object.freeze({
    COMPONENTS_IMPLEMENTATION_MAP: COMPONENTS_IMPLEMENTATION_MAP,
    DOM_COMPONENT_INSTANCE_PROPERTY: DOM_COMPONENT_INSTANCE_PROPERTY,
    PLUGINS_SET: PLUGINS_SET,
    IS_DIRECTIVE: IS_DIRECTIVE,
    VALUE_ATTRIBUTE: VALUE_ATTRIBUTE,
    ATTRIBUTES_KEY_SYMBOL: ATTRIBUTES_KEY_SYMBOL,
    TEMPLATE_KEY_SYMBOL: TEMPLATE_KEY_SYMBOL
  });

  /**
   * Remove the child nodes from any DOM node
   * @param   {HTMLElement} node - target node
   * @returns {undefined}
   */
  function cleanNode(node) {
    clearChildren(node, node.childNodes);
  }
  /**
   * Clear multiple children in a node
   * @param   {HTMLElement} parent - parent node where the children will be removed
   * @param   {HTMLElement[]} children - direct children nodes
   * @returns {undefined}
   */


  function clearChildren(parent, children) {
    Array.from(children).forEach(n => parent.removeChild(n));
  }

  const EACH = 0;
  const IF = 1;
  const SIMPLE = 2;
  const TAG = 3;
  const SLOT = 4;
  var bindingTypes = {
    EACH,
    IF,
    SIMPLE,
    TAG,
    SLOT
  };
  /**
   * Create the template meta object in case of <template> fragments
   * @param   {TemplateChunk} componentTemplate - template chunk object
   * @returns {Object} the meta property that will be passed to the mount function of the TemplateChunk
   */

  function createTemplateMeta(componentTemplate) {
    const fragment = componentTemplate.dom.cloneNode(true);
    return {
      avoidDOMInjection: true,
      fragment,
      children: Array.from(fragment.childNodes)
    };
  }
  /* get rid of the @ungap/essential-map polyfill */


  const append = (get, parent, children, start, end, before) => {
    if (end - start < 2) parent.insertBefore(get(children[start], 1), before);else {
      const fragment = parent.ownerDocument.createDocumentFragment();

      while (start < end) fragment.appendChild(get(children[start++], 1));

      parent.insertBefore(fragment, before);
    }
  };

  const eqeq = (a, b) => a == b;

  const identity = O => O;

  const indexOf = (moreNodes, moreStart, moreEnd, lessNodes, lessStart, lessEnd, compare) => {
    const length = lessEnd - lessStart;
    /* istanbul ignore if */

    if (length < 1) return -1;

    while (moreEnd - moreStart >= length) {
      let m = moreStart;
      let l = lessStart;

      while (m < moreEnd && l < lessEnd && compare(moreNodes[m], lessNodes[l])) {
        m++;
        l++;
      }

      if (l === lessEnd) return moreStart;
      moreStart = m + 1;
    }

    return -1;
  };

  const isReversed = (futureNodes, futureEnd, currentNodes, currentStart, currentEnd, compare) => {
    while (currentStart < currentEnd && compare(currentNodes[currentStart], futureNodes[futureEnd - 1])) {
      currentStart++;
      futureEnd--;
    }

    return futureEnd === 0;
  };

  const next = (get, list, i, length, before) => i < length ? get(list[i], 0) : 0 < i ? get(list[i - 1], -0).nextSibling : before;

  const remove = (get, parent, children, start, end) => {
    if (end - start < 2) parent.removeChild(get(children[start], -1));else {
      const range = parent.ownerDocument.createRange();
      range.setStartBefore(get(children[start], -1));
      range.setEndAfter(get(children[end - 1], -1));
      range.deleteContents();
    }
  }; // - - - - - - - - - - - - - - - - - - -
  // diff related constants and utilities
  // - - - - - - - - - - - - - - - - - - -


  const DELETION = -1;
  const INSERTION = 1;
  const SKIP = 0;
  const SKIP_OND = 50;

  const HS = (futureNodes, futureStart, futureEnd, futureChanges, currentNodes, currentStart, currentEnd, currentChanges) => {
    let k = 0;
    /* istanbul ignore next */

    let minLen = futureChanges < currentChanges ? futureChanges : currentChanges;
    const link = Array(minLen++);
    const tresh = Array(minLen);
    tresh[0] = -1;

    for (let i = 1; i < minLen; i++) tresh[i] = currentEnd;

    const keymap = new Map();

    for (let i = currentStart; i < currentEnd; i++) keymap.set(currentNodes[i], i);

    for (let i = futureStart; i < futureEnd; i++) {
      const idxInOld = keymap.get(futureNodes[i]);

      if (idxInOld != null) {
        k = findK(tresh, minLen, idxInOld);
        /* istanbul ignore else */

        if (-1 < k) {
          tresh[k] = idxInOld;
          link[k] = {
            newi: i,
            oldi: idxInOld,
            prev: link[k - 1]
          };
        }
      }
    }

    k = --minLen;
    --currentEnd;

    while (tresh[k] > currentEnd) --k;

    minLen = currentChanges + futureChanges - k;
    const diff = Array(minLen);
    let ptr = link[k];
    --futureEnd;

    while (ptr) {
      const {
        newi,
        oldi
      } = ptr;

      while (futureEnd > newi) {
        diff[--minLen] = INSERTION;
        --futureEnd;
      }

      while (currentEnd > oldi) {
        diff[--minLen] = DELETION;
        --currentEnd;
      }

      diff[--minLen] = SKIP;
      --futureEnd;
      --currentEnd;
      ptr = ptr.prev;
    }

    while (futureEnd >= futureStart) {
      diff[--minLen] = INSERTION;
      --futureEnd;
    }

    while (currentEnd >= currentStart) {
      diff[--minLen] = DELETION;
      --currentEnd;
    }

    return diff;
  }; // this is pretty much the same petit-dom code without the delete map part
  // https://github.com/yelouafi/petit-dom/blob/bd6f5c919b5ae5297be01612c524c40be45f14a7/src/vdom.js#L556-L561


  const OND = (futureNodes, futureStart, rows, currentNodes, currentStart, cols, compare) => {
    const length = rows + cols;
    const v = [];
    let d, k, r, c, pv, cv, pd;

    outer: for (d = 0; d <= length; d++) {
      /* istanbul ignore if */
      if (d > SKIP_OND) return null;
      pd = d - 1;
      /* istanbul ignore next */

      pv = d ? v[d - 1] : [0, 0];
      cv = v[d] = [];

      for (k = -d; k <= d; k += 2) {
        if (k === -d || k !== d && pv[pd + k - 1] < pv[pd + k + 1]) {
          c = pv[pd + k + 1];
        } else {
          c = pv[pd + k - 1] + 1;
        }

        r = c - k;

        while (c < cols && r < rows && compare(currentNodes[currentStart + c], futureNodes[futureStart + r])) {
          c++;
          r++;
        }

        if (c === cols && r === rows) {
          break outer;
        }

        cv[d + k] = c;
      }
    }

    const diff = Array(d / 2 + length / 2);
    let diffIdx = diff.length - 1;

    for (d = v.length - 1; d >= 0; d--) {
      while (c > 0 && r > 0 && compare(currentNodes[currentStart + c - 1], futureNodes[futureStart + r - 1])) {
        // diagonal edge = equality
        diff[diffIdx--] = SKIP;
        c--;
        r--;
      }

      if (!d) break;
      pd = d - 1;
      /* istanbul ignore next */

      pv = d ? v[d - 1] : [0, 0];
      k = c - r;

      if (k === -d || k !== d && pv[pd + k - 1] < pv[pd + k + 1]) {
        // vertical edge = insertion
        r--;
        diff[diffIdx--] = INSERTION;
      } else {
        // horizontal edge = deletion
        c--;
        diff[diffIdx--] = DELETION;
      }
    }

    return diff;
  };

  const applyDiff = (diff, get, parentNode, futureNodes, futureStart, currentNodes, currentStart, currentLength, before) => {
    const live = new Map();
    const length = diff.length;
    let currentIndex = currentStart;
    let i = 0;

    while (i < length) {
      switch (diff[i++]) {
        case SKIP:
          futureStart++;
          currentIndex++;
          break;

        case INSERTION:
          // TODO: bulk appends for sequential nodes
          live.set(futureNodes[futureStart], 1);
          append(get, parentNode, futureNodes, futureStart++, futureStart, currentIndex < currentLength ? get(currentNodes[currentIndex], 0) : before);
          break;

        case DELETION:
          currentIndex++;
          break;
      }
    }

    i = 0;

    while (i < length) {
      switch (diff[i++]) {
        case SKIP:
          currentStart++;
          break;

        case DELETION:
          // TODO: bulk removes for sequential nodes
          if (live.has(currentNodes[currentStart])) currentStart++;else remove(get, parentNode, currentNodes, currentStart++, currentStart);
          break;
      }
    }
  };

  const findK = (ktr, length, j) => {
    let lo = 1;
    let hi = length;

    while (lo < hi) {
      const mid = (lo + hi) / 2 >>> 0;
      if (j < ktr[mid]) hi = mid;else lo = mid + 1;
    }

    return lo;
  };

  const smartDiff = (get, parentNode, futureNodes, futureStart, futureEnd, futureChanges, currentNodes, currentStart, currentEnd, currentChanges, currentLength, compare, before) => {
    applyDiff(OND(futureNodes, futureStart, futureChanges, currentNodes, currentStart, currentChanges, compare) || HS(futureNodes, futureStart, futureEnd, futureChanges, currentNodes, currentStart, currentEnd, currentChanges), get, parentNode, futureNodes, futureStart, currentNodes, currentStart, currentLength, before);
  };
  /*! (c) 2018 Andrea Giammarchi (ISC) */


  const domdiff = (parentNode, // where changes happen
  currentNodes, // Array of current items/nodes
  futureNodes, // Array of future items/nodes
  options // optional object with one of the following properties
  //  before: domNode
  //  compare(generic, generic) => true if same generic
  //  node(generic) => Node
  ) => {
    if (!options) options = {};
    const compare = options.compare || eqeq;
    const get = options.node || identity;
    const before = options.before == null ? null : get(options.before, 0);
    const currentLength = currentNodes.length;
    let currentEnd = currentLength;
    let currentStart = 0;
    let futureEnd = futureNodes.length;
    let futureStart = 0; // common prefix

    while (currentStart < currentEnd && futureStart < futureEnd && compare(currentNodes[currentStart], futureNodes[futureStart])) {
      currentStart++;
      futureStart++;
    } // common suffix


    while (currentStart < currentEnd && futureStart < futureEnd && compare(currentNodes[currentEnd - 1], futureNodes[futureEnd - 1])) {
      currentEnd--;
      futureEnd--;
    }

    const currentSame = currentStart === currentEnd;
    const futureSame = futureStart === futureEnd; // same list

    if (currentSame && futureSame) return futureNodes; // only stuff to add

    if (currentSame && futureStart < futureEnd) {
      append(get, parentNode, futureNodes, futureStart, futureEnd, next(get, currentNodes, currentStart, currentLength, before));
      return futureNodes;
    } // only stuff to remove


    if (futureSame && currentStart < currentEnd) {
      remove(get, parentNode, currentNodes, currentStart, currentEnd);
      return futureNodes;
    }

    const currentChanges = currentEnd - currentStart;
    const futureChanges = futureEnd - futureStart;
    let i = -1; // 2 simple indels: the shortest sequence is a subsequence of the longest

    if (currentChanges < futureChanges) {
      i = indexOf(futureNodes, futureStart, futureEnd, currentNodes, currentStart, currentEnd, compare); // inner diff

      if (-1 < i) {
        append(get, parentNode, futureNodes, futureStart, i, get(currentNodes[currentStart], 0));
        append(get, parentNode, futureNodes, i + currentChanges, futureEnd, next(get, currentNodes, currentEnd, currentLength, before));
        return futureNodes;
      }
    }
    /* istanbul ignore else */
    else if (futureChanges < currentChanges) {
        i = indexOf(currentNodes, currentStart, currentEnd, futureNodes, futureStart, futureEnd, compare); // outer diff

        if (-1 < i) {
          remove(get, parentNode, currentNodes, currentStart, i);
          remove(get, parentNode, currentNodes, i + futureChanges, currentEnd);
          return futureNodes;
        }
      } // common case with one replacement for many nodes
    // or many nodes replaced for a single one

    /* istanbul ignore else */


    if (currentChanges < 2 || futureChanges < 2) {
      append(get, parentNode, futureNodes, futureStart, futureEnd, get(currentNodes[currentStart], 0));
      remove(get, parentNode, currentNodes, currentStart, currentEnd);
      return futureNodes;
    } // the half match diff part has been skipped in petit-dom
    // https://github.com/yelouafi/petit-dom/blob/bd6f5c919b5ae5297be01612c524c40be45f14a7/src/vdom.js#L391-L397
    // accordingly, I think it's safe to skip in here too
    // if one day it'll come out like the speediest thing ever to do
    // then I might add it in here too
    // Extra: before going too fancy, what about reversed lists ?
    //        This should bail out pretty quickly if that's not the case.


    if (currentChanges === futureChanges && isReversed(futureNodes, futureEnd, currentNodes, currentStart, currentEnd, compare)) {
      append(get, parentNode, futureNodes, futureStart, futureEnd, next(get, currentNodes, currentEnd, currentLength, before));
      return futureNodes;
    } // last resort through a smart diff


    smartDiff(get, parentNode, futureNodes, futureStart, futureEnd, futureChanges, currentNodes, currentStart, currentEnd, currentChanges, currentLength, compare, before);
    return futureNodes;
  };
  /**
   * Check if a value is null or undefined
   * @param   {*}  value - anything
   * @returns {boolean} true only for the 'undefined' and 'null' types
   */


  function isNil(value) {
    return value == null;
  }
  /**
   * Check if an element is a template tag
   * @param   {HTMLElement}  el - element to check
   * @returns {boolean} true if it's a <template>
   */


  function isTemplate(el) {
    return !isNil(el.content);
  }

  const EachBinding = Object.seal({
    // dynamic binding properties
    childrenMap: null,
    node: null,
    root: null,
    condition: null,
    evaluate: null,
    template: null,
    isTemplateTag: false,
    nodes: [],
    getKey: null,
    indexName: null,
    itemName: null,
    afterPlaceholder: null,
    placeholder: null,

    // API methods
    mount(scope, parentScope) {
      return this.update(scope, parentScope);
    },

    update(scope, parentScope) {
      const {
        placeholder
      } = this;
      const collection = this.evaluate(scope);
      const items = collection ? Array.from(collection) : [];
      const parent = placeholder.parentNode; // prepare the diffing

      const {
        newChildrenMap,
        batches,
        futureNodes
      } = createPatch(items, scope, parentScope, this); // patch the DOM only if there are new nodes

      if (futureNodes.length) {
        domdiff(parent, this.nodes, futureNodes, {
          before: placeholder,
          node: patch(Array.from(this.childrenMap.values()), parentScope)
        });
      } else {
        // remove all redundant templates
        unmountRedundant(this.childrenMap);
      } // trigger the mounts and the updates


      batches.forEach(fn => fn()); // update the children map

      this.childrenMap = newChildrenMap;
      this.nodes = futureNodes;
      return this;
    },

    unmount(scope, parentScope) {
      unmountRedundant(this.childrenMap, parentScope);
      this.childrenMap = new Map();
      this.nodes = [];
      return this;
    }

  });
  /**
   * Patch the DOM while diffing
   * @param   {TemplateChunk[]} redundant - redundant tepmplate chunks
   * @param   {*} parentScope - scope of the parent template
   * @returns {Function} patch function used by domdiff
   */

  function patch(redundant, parentScope) {
    return (item, info) => {
      if (info < 0) {
        const {
          template,
          context
        } = redundant.pop(); // notice that we pass null as last argument because
        // the root node and its children will be removed by domdiff

        template.unmount(context, parentScope, null);
      }

      return item;
    };
  }
  /**
   * Unmount the remaining template instances
   * @param   {Map} childrenMap - map containing the children template to unmount
   * @param   {*} parentScope - scope of the parent template
   * @returns {TemplateChunk[]} collection containing the template chunks unmounted
   */


  function unmountRedundant(childrenMap, parentScope) {
    return Array.from(childrenMap.values()).map((_ref) => {
      let {
        template,
        context
      } = _ref;
      return template.unmount(context, parentScope, true);
    });
  }
  /**
   * Check whether a template must be filtered from a loop
   * @param   {Function} condition - filter function
   * @param   {Object} context - argument passed to the filter function
   * @returns {boolean} true if this item should be skipped
   */


  function mustFilterItem(condition, context) {
    return condition ? Boolean(condition(context)) === false : false;
  }
  /**
   * Extend the scope of the looped template
   * @param   {Object} scope - current template scope
   * @param   {string} options.itemName - key to identify the looped item in the new context
   * @param   {string} options.indexName - key to identify the index of the looped item
   * @param   {number} options.index - current index
   * @param   {*} options.item - collection item looped
   * @returns {Object} enhanced scope object
   */


  function extendScope(scope, _ref2) {
    let {
      itemName,
      indexName,
      index,
      item
    } = _ref2;
    scope[itemName] = item;
    if (indexName) scope[indexName] = index;
    return scope;
  }
  /**
   * Loop the current template items
   * @param   {Array} items - expression collection value
   * @param   {*} scope - template scope
   * @param   {*} parentScope - scope of the parent template
   * @param   {EeachBinding} binding - each binding object instance
   * @returns {Object} data
   * @returns {Map} data.newChildrenMap - a Map containing the new children template structure
   * @returns {Array} data.batches - array containing the template lifecycle functions to trigger
   * @returns {Array} data.futureNodes - array containing the nodes we need to diff
   */


  function createPatch(items, scope, parentScope, binding) {
    const {
      condition,
      template,
      childrenMap,
      itemName,
      getKey,
      indexName,
      root,
      isTemplateTag
    } = binding;
    const newChildrenMap = new Map();
    const batches = [];
    const futureNodes = [];
    items.forEach((item, index) => {
      const context = extendScope(Object.create(scope), {
        itemName,
        indexName,
        index,
        item
      });
      const key = getKey ? getKey(context) : index;
      const oldItem = childrenMap.get(key);

      if (mustFilterItem(condition, context)) {
        return;
      }

      const componentTemplate = oldItem ? oldItem.template : template.clone();
      const el = oldItem ? componentTemplate.el : root.cloneNode();
      const mustMount = !oldItem;
      const meta = isTemplateTag && mustMount ? createTemplateMeta(componentTemplate) : {};

      if (mustMount) {
        batches.push(() => componentTemplate.mount(el, context, parentScope, meta));
      } else {
        componentTemplate.update(context, parentScope);
      } // create the collection of nodes to update or to add
      // in case of template tags we need to add all its children nodes


      if (isTemplateTag) {
        futureNodes.push(...(meta.children || componentTemplate.children));
      } else {
        futureNodes.push(el);
      } // delete the old item from the children map


      childrenMap.delete(key); // update the children map

      newChildrenMap.set(key, {
        template: componentTemplate,
        context,
        index
      });
    });
    return {
      newChildrenMap,
      batches,
      futureNodes
    };
  }

  function create(node, _ref3) {
    let {
      evaluate,
      condition,
      itemName,
      indexName,
      getKey,
      template
    } = _ref3;
    const placeholder = document.createTextNode('');
    const parent = node.parentNode;
    const root = node.cloneNode();
    parent.insertBefore(placeholder, node);
    parent.removeChild(node);
    return Object.assign({}, EachBinding, {
      childrenMap: new Map(),
      node,
      root,
      condition,
      evaluate,
      isTemplateTag: isTemplate(root),
      template: template.createDOM(node),
      getKey,
      indexName,
      itemName,
      placeholder
    });
  }
  /**
   * Binding responsible for the `if` directive
   */


  const IfBinding = Object.seal({
    // dynamic binding properties
    node: null,
    evaluate: null,
    parent: null,
    isTemplateTag: false,
    placeholder: null,
    template: null,

    // API methods
    mount(scope, parentScope) {
      this.parent.insertBefore(this.placeholder, this.node);
      this.parent.removeChild(this.node);
      return this.update(scope, parentScope);
    },

    update(scope, parentScope) {
      const value = !!this.evaluate(scope);
      const mustMount = !this.value && value;
      const mustUnmount = this.value && !value;

      switch (true) {
        case mustMount:
          this.parent.insertBefore(this.node, this.placeholder);
          this.template = this.template.clone();
          this.template.mount(this.node, scope, parentScope);
          break;

        case mustUnmount:
          this.unmount(scope);
          break;

        default:
          if (value) this.template.update(scope, parentScope);
      }

      this.value = value;
      return this;
    },

    unmount(scope, parentScope) {
      this.template.unmount(scope, parentScope);
      return this;
    }

  });

  function create$1(node, _ref4) {
    let {
      evaluate,
      template
    } = _ref4;
    return Object.assign({}, IfBinding, {
      node,
      evaluate,
      parent: node.parentNode,
      placeholder: document.createTextNode(''),
      template: template.createDOM(node)
    });
  }

  const ATTRIBUTE = 0;
  const EVENT = 1;
  const TEXT = 2;
  const VALUE = 3;
  var expressionTypes = {
    ATTRIBUTE,
    EVENT,
    TEXT,
    VALUE
  };
  const REMOVE_ATTRIBUTE = 'removeAttribute';
  const SET_ATTIBUTE = 'setAttribute';
  /**
   * Add all the attributes provided
   * @param   {HTMLElement} node - target node
   * @param   {Object} attributes - object containing the attributes names and values
   * @returns {undefined} sorry it's a void function :(
   */

  function setAllAttributes(node, attributes) {
    Object.entries(attributes).forEach((_ref5) => {
      let [name, value] = _ref5;
      return attributeExpression(node, {
        name
      }, value);
    });
  }
  /**
   * Remove all the attributes provided
   * @param   {HTMLElement} node - target node
   * @param   {Object} attributes - object containing all the attribute names
   * @returns {undefined} sorry it's a void function :(
   */


  function removeAllAttributes(node, attributes) {
    Object.keys(attributes).forEach(attribute => node.removeAttribute(attribute));
  }
  /**
   * This methods handles the DOM attributes updates
   * @param   {HTMLElement} node - target node
   * @param   {Object} expression - expression object
   * @param   {string} expression.name - attribute name
   * @param   {*} value - new expression value
   * @param   {*} oldValue - the old expression cached value
   * @returns {undefined}
   */


  function attributeExpression(node, _ref6, value, oldValue) {
    let {
      name
    } = _ref6;

    // is it a spread operator? {...attributes}
    if (!name) {
      // is the value still truthy?
      if (value) {
        setAllAttributes(node, value);
      } else if (oldValue) {
        // otherwise remove all the old attributes
        removeAllAttributes(node, oldValue);
      }

      return;
    } // handle boolean attributes


    if (typeof value === 'boolean') {
      node[name] = value;
    }

    node[getMethod(value)](name, normalizeValue(name, value));
  }
  /**
   * Get the attribute modifier method
   * @param   {*} value - if truthy we return `setAttribute` othewise `removeAttribute`
   * @returns {string} the node attribute modifier method name
   */


  function getMethod(value) {
    return isNil(value) || value === false || value === '' || typeof value === 'object' ? REMOVE_ATTRIBUTE : SET_ATTIBUTE;
  }
  /**
   * Get the value as string
   * @param   {string} name - attribute name
   * @param   {*} value - user input value
   * @returns {string} input value as string
   */


  function normalizeValue(name, value) {
    // be sure that expressions like selected={ true } will be always rendered as selected='selected'
    if (value === true) return name;
    return value;
  }
  /**
   * Set a new event listener
   * @param   {HTMLElement} node - target node
   * @param   {Object} expression - expression object
   * @param   {string} expression.name - event name
   * @param   {*} value - new expression value
   * @returns {undefined}
   */


  function eventExpression(node, _ref7, value) {
    let {
      name
    } = _ref7;
    node[name] = value;
  }
  /**
   * This methods handles a simple text expression update
   * @param   {HTMLElement} node - target node
   * @param   {Object} expression - expression object
   * @param   {number} expression.childNodeIndex - index to find the text node to update
   * @param   {*} value - new expression value
   * @returns {undefined}
   */


  function textExpression(node, _ref8, value) {
    let {
      childNodeIndex
    } = _ref8;
    const target = node.childNodes[childNodeIndex];
    const val = normalizeValue$1(value); // replace the target if it's a placeholder comment

    if (target.nodeType === Node.COMMENT_NODE) {
      const textNode = document.createTextNode(val);
      node.replaceChild(textNode, target);
    } else {
      target.data = normalizeValue$1(val);
    }
  }
  /**
   * Normalize the user value in order to render a empty string in case of falsy values
   * @param   {*} value - user input value
   * @returns {string} hopefully a string
   */


  function normalizeValue$1(value) {
    return value != null ? value : '';
  }
  /**
   * This methods handles the input fileds value updates
   * @param   {HTMLElement} node - target node
   * @param   {Object} expression - expression object
   * @param   {*} value - new expression value
   * @returns {undefined}
   */


  function valueExpression(node, expression, value) {
    node.value = value;
  }

  var expressions = {
    [ATTRIBUTE]: attributeExpression,
    [EVENT]: eventExpression,
    [TEXT]: textExpression,
    [VALUE]: valueExpression
  };
  const Expression = Object.seal({
    // Static props
    node: null,
    value: null,

    // API methods

    /**
     * Mount the expression evaluating its initial value
     * @param   {*} scope - argument passed to the expression to evaluate its current values
     * @returns {Expression} self
     */
    mount(scope) {
      // hopefully a pure function
      this.value = this.evaluate(scope); // IO() DOM updates

      apply(this, this.value);
      return this;
    },

    /**
     * Update the expression if its value changed
     * @param   {*} scope - argument passed to the expression to evaluate its current values
     * @returns {Expression} self
     */
    update(scope) {
      // pure function
      const value = this.evaluate(scope);

      if (this.value !== value) {
        // IO() DOM updates
        apply(this, value);
        this.value = value;
      }

      return this;
    },

    /**
     * Expression teardown method
     * @returns {Expression} self
     */
    unmount() {
      return this;
    }

  });
  /**
   * IO() function to handle the DOM updates
   * @param {Expression} expression - expression object
   * @param {*} value - current expression value
   * @returns {undefined}
   */

  function apply(expression, value) {
    return expressions[expression.type](expression.node, expression, value, expression.value);
  }

  function create$2(node, data) {
    return Object.assign({}, Expression, {}, data, {
      node
    });
  }
  /**
   * Create a flat object having as keys a list of methods that if dispatched will propagate
   * on the whole collection
   * @param   {Array} collection - collection to iterate
   * @param   {Array<string>} methods - methods to execute on each item of the collection
   * @param   {*} context - context returned by the new methods created
   * @returns {Object} a new object to simplify the the nested methods dispatching
   */


  function flattenCollectionMethods(collection, methods, context) {
    return methods.reduce((acc, method) => {
      return Object.assign({}, acc, {
        [method]: scope => {
          return collection.map(item => item[method](scope)) && context;
        }
      });
    }, {});
  }

  function create$3(node, _ref9) {
    let {
      expressions
    } = _ref9;
    return Object.assign({}, flattenCollectionMethods(expressions.map(expression => create$2(node, expression)), ['mount', 'update', 'unmount']));
  }

  const SlotBinding = Object.seal({
    // dynamic binding properties
    node: null,
    name: null,
    template: null,

    // API methods
    mount(scope, parentScope) {
      const templateData = scope.slots ? scope.slots.find((_ref10) => {
        let {
          id
        } = _ref10;
        return id === this.name;
      }) : false;
      const {
        parentNode
      } = this.node;
      this.template = templateData && create$6(templateData.html, templateData.bindings).createDOM(parentNode);

      if (this.template) {
        this.template.mount(this.node, parentScope);
        moveSlotInnerContent(this.node);
      }

      parentNode.removeChild(this.node);
      return this;
    },

    update(scope, parentScope) {
      if (this.template && parentScope) {
        this.template.update(parentScope);
      }

      return this;
    },

    unmount(scope, parentScope, mustRemoveRoot) {
      if (this.template) {
        this.template.unmount(parentScope, null, mustRemoveRoot);
      }

      return this;
    }

  });
  /**
   * Move the inner content of the slots outside of them
   * @param   {HTMLNode} slot - slot node
   * @returns {undefined} it's a void function
   */

  function moveSlotInnerContent(slot) {
    if (slot.firstChild) {
      slot.parentNode.insertBefore(slot.firstChild, slot);
      moveSlotInnerContent(slot);
    }
  }
  /**
   * Create a single slot binding
   * @param   {HTMLElement} node - slot node
   * @param   {string} options.name - slot id
   * @returns {Object} Slot binding object
   */


  function createSlot(node, _ref11) {
    let {
      name
    } = _ref11;
    return Object.assign({}, SlotBinding, {
      node,
      name
    });
  }
  /**
   * Create a new tag object if it was registered before, otherwise fallback to the simple
   * template chunk
   * @param   {Function} component - component factory function
   * @param   {Array<Object>} slots - array containing the slots markup
   * @param   {Array} attributes - dynamic attributes that will be received by the tag element
   * @returns {TagImplementation|TemplateChunk} a tag implementation or a template chunk as fallback
   */


  function getTag(component, slots, attributes) {
    if (slots === void 0) {
      slots = [];
    }

    if (attributes === void 0) {
      attributes = [];
    }

    // if this tag was registered before we will return its implementation
    if (component) {
      return component({
        slots,
        attributes
      });
    } // otherwise we return a template chunk


    return create$6(slotsToMarkup(slots), [...slotBindings(slots), {
      // the attributes should be registered as binding
      // if we fallback to a normal template chunk
      expressions: attributes.map(attr => {
        return Object.assign({
          type: ATTRIBUTE
        }, attr);
      })
    }]);
  }
  /**
   * Merge all the slots bindings into a single array
   * @param   {Array<Object>} slots - slots collection
   * @returns {Array<Bindings>} flatten bindings array
   */


  function slotBindings(slots) {
    return slots.reduce((acc, _ref12) => {
      let {
        bindings
      } = _ref12;
      return acc.concat(bindings);
    }, []);
  }
  /**
   * Merge all the slots together in a single markup string
   * @param   {Array<Object>} slots - slots collection
   * @returns {string} markup of all the slots in a single string
   */


  function slotsToMarkup(slots) {
    return slots.reduce((acc, slot) => {
      return acc + slot.html;
    }, '');
  }

  const TagBinding = Object.seal({
    // dynamic binding properties
    node: null,
    evaluate: null,
    name: null,
    slots: null,
    tag: null,
    attributes: null,
    getComponent: null,

    mount(scope) {
      return this.update(scope);
    },

    update(scope, parentScope) {
      const name = this.evaluate(scope); // simple update

      if (name === this.name) {
        this.tag.update(scope);
      } else {
        // unmount the old tag if it exists
        this.unmount(scope, parentScope, true); // mount the new tag

        this.name = name;
        this.tag = getTag(this.getComponent(name), this.slots, this.attributes);
        this.tag.mount(this.node, scope);
      }

      return this;
    },

    unmount(scope, parentScope, keepRootTag) {
      if (this.tag) {
        // keep the root tag
        this.tag.unmount(keepRootTag);
      }

      return this;
    }

  });

  function create$4(node, _ref13) {
    let {
      evaluate,
      getComponent,
      slots,
      attributes
    } = _ref13;
    return Object.assign({}, TagBinding, {
      node,
      evaluate,
      slots,
      attributes,
      getComponent
    });
  }

  var bindings = {
    [IF]: create$1,
    [SIMPLE]: create$3,
    [EACH]: create,
    [TAG]: create$4,
    [SLOT]: createSlot
  };
  /**
   * Bind a new expression object to a DOM node
   * @param   {HTMLElement} root - DOM node where to bind the expression
   * @param   {Object} binding - binding data
   * @returns {Expression} Expression object
   */

  function create$5(root, binding) {
    const {
      selector,
      type,
      redundantAttribute,
      expressions
    } = binding; // find the node to apply the bindings

    const node = selector ? root.querySelector(selector) : root; // remove eventually additional attributes created only to select this node

    if (redundantAttribute) node.removeAttribute(redundantAttribute); // init the binding

    return (bindings[type] || bindings[SIMPLE])(node, Object.assign({}, binding, {
      expressions: expressions || []
    }));
  }
  /**
   * Check if an element is part of an svg
   * @param   {HTMLElement}  el - element to check
   * @returns {boolean} true if we are in an svg context
   */


  function isSvg(el) {
    const owner = el.ownerSVGElement;
    return !!owner || owner === null;
  } // in this case a simple innerHTML is enough


  function createHTMLTree(html, root) {
    const template = isTemplate(root) ? root : document.createElement('template');
    template.innerHTML = html;
    return template.content;
  } // for svg nodes we need a bit more work


  function creteSVGTree(html, container) {
    // create the SVGNode
    const svgNode = container.ownerDocument.importNode(new window.DOMParser().parseFromString(`<svg xmlns="http://www.w3.org/2000/svg">${html}</svg>`, 'application/xml').documentElement, true);
    return svgNode;
  }
  /**
   * Create the DOM that will be injected
   * @param {Object} root - DOM node to find out the context where the fragment will be created
   * @param   {string} html - DOM to create as string
   * @returns {HTMLDocumentFragment|HTMLElement} a new html fragment
   */


  function createDOMTree(root, html) {
    if (isSvg(root)) return creteSVGTree(html, root);
    return createHTMLTree(html, root);
  }
  /**
   * Move all the child nodes from a source tag to another
   * @param   {HTMLElement} source - source node
   * @param   {HTMLElement} target - target node
   * @returns {undefined} it's a void method ¯\_(ツ)_/¯
   */
  // Ignore this helper because it's needed only for svg tags

  /* istanbul ignore next */


  function moveChildren(source, target) {
    if (source.firstChild) {
      target.appendChild(source.firstChild);
      moveChildren(source, target);
    }
  }
  /**
   * Inject the DOM tree into a target node
   * @param   {HTMLElement} el - target element
   * @param   {HTMLFragment|SVGElement} dom - dom tree to inject
   * @returns {undefined}
   */


  function injectDOM(el, dom) {
    switch (true) {
      case isSvg(el):
        moveChildren(dom, el);
        break;

      case isTemplate(el):
        el.parentNode.replaceChild(dom, el);
        break;

      default:
        el.appendChild(dom);
    }
  }
  /**
   * Create the Template DOM skeleton
   * @param   {HTMLElement} el - root node where the DOM will be injected
   * @param   {string} html - markup that will be injected into the root node
   * @returns {HTMLFragment} fragment that will be injected into the root node
   */


  function createTemplateDOM(el, html) {
    return html && (typeof html === 'string' ? createDOMTree(el, html) : html);
  }
  /**
   * Template Chunk model
   * @type {Object}
   */


  const TemplateChunk = Object.freeze({
    // Static props
    bindings: null,
    bindingsData: null,
    html: null,
    isTemplateTag: false,
    fragment: null,
    children: null,
    dom: null,
    el: null,

    /**
     * Create the template DOM structure that will be cloned on each mount
     * @param   {HTMLElement} el - the root node
     * @returns {TemplateChunk} self
     */
    createDOM(el) {
      // make sure that the DOM gets created before cloning the template
      this.dom = this.dom || createTemplateDOM(el, this.html);
      return this;
    },

    // API methods

    /**
     * Attach the template to a DOM node
     * @param   {HTMLElement} el - target DOM node
     * @param   {*} scope - template data
     * @param   {*} parentScope - scope of the parent template tag
     * @param   {Object} meta - meta properties needed to handle the <template> tags in loops
     * @returns {TemplateChunk} self
     */
    mount(el, scope, parentScope, meta) {
      if (meta === void 0) {
        meta = {};
      }

      if (!el) throw new Error('Please provide DOM node to mount properly your template');
      if (this.el) this.unmount(scope); // <template> tags require a bit more work
      // the template fragment might be already created via meta outside of this call

      const {
        fragment,
        children,
        avoidDOMInjection
      } = meta; // <template> bindings of course can not have a root element
      // so we check the parent node to set the query selector bindings

      const {
        parentNode
      } = children ? children[0] : el;
      this.isTemplateTag = isTemplate(el); // create the DOM if it wasn't created before

      this.createDOM(el);

      if (this.dom) {
        // create the new template dom fragment if it want already passed in via meta
        this.fragment = fragment || this.dom.cloneNode(true);
      } // store root node
      // notice that for template tags the root note will be the parent tag


      this.el = this.isTemplateTag ? parentNode : el; // create the children array only for the <template> fragments

      this.children = this.isTemplateTag ? children || Array.from(this.fragment.childNodes) : null; // inject the DOM into the el only if a fragment is available

      if (!avoidDOMInjection && this.fragment) injectDOM(el, this.fragment); // create the bindings

      this.bindings = this.bindingsData.map(binding => create$5(this.el, binding));
      this.bindings.forEach(b => b.mount(scope, parentScope));
      return this;
    },

    /**
     * Update the template with fresh data
     * @param   {*} scope - template data
     * @param   {*} parentScope - scope of the parent template tag
     * @returns {TemplateChunk} self
     */
    update(scope, parentScope) {
      this.bindings.forEach(b => b.update(scope, parentScope));
      return this;
    },

    /**
     * Remove the template from the node where it was initially mounted
     * @param   {*} scope - template data
     * @param   {*} parentScope - scope of the parent template tag
     * @param   {boolean|null} mustRemoveRoot - if true remove the root element,
     * if false or undefined clean the root tag content, if null don't touch the DOM
     * @returns {TemplateChunk} self
     */
    unmount(scope, parentScope, mustRemoveRoot) {
      if (this.el) {
        this.bindings.forEach(b => b.unmount(scope, parentScope, mustRemoveRoot));

        if (mustRemoveRoot && this.el.parentNode) {
          this.el.parentNode.removeChild(this.el);
        } else if (mustRemoveRoot !== null) {
          if (this.children) {
            clearChildren(this.children[0].parentNode, this.children);
          } else {
            cleanNode(this.el);
          }
        }

        this.el = null;
      }

      return this;
    },

    /**
     * Clone the template chunk
     * @returns {TemplateChunk} a clone of this object resetting the this.el property
     */
    clone() {
      return Object.assign({}, this, {
        el: null
      });
    }

  });
  /**
   * Create a template chunk wiring also the bindings
   * @param   {string|HTMLElement} html - template string
   * @param   {Array} bindings - bindings collection
   * @returns {TemplateChunk} a new TemplateChunk copy
   */

  function create$6(html, bindings) {
    if (bindings === void 0) {
      bindings = [];
    }

    return Object.assign({}, TemplateChunk, {
      html,
      bindingsData: bindings
    });
  }

  /**
   * Quick type checking
   * @param   {*} element - anything
   * @param   {string} type - type definition
   * @returns {boolean} true if the type corresponds
   */
  function checkType(element, type) {
    return typeof element === type;
  }
  /**
   * Check that will be passed if its argument is a function
   * @param   {*} value - value to check
   * @returns {boolean} - true if the value is a function
   */

  function isFunction(value) {
    return checkType(value, 'function');
  }

  /* eslint-disable fp/no-mutating-methods */
  /**
   * Throw an error
   * @param {string} error - error message
   * @returns {undefined} it's a IO void function
   */

  function panic(error) {
    throw new Error(error);
  }
  /**
   * Call the first argument received only if it's a function otherwise return it as it is
   * @param   {*} source - anything
   * @returns {*} anything
   */

  function callOrAssign(source) {
    return isFunction(source) ? source.prototype && source.prototype.constructor ? new source() : source() : source;
  }
  /**
   * Convert a string from camel case to dash-case
   * @param   {string} string - probably a component tag name
   * @returns {string} component name normalized
   */

  function camelToDashCase(string) {
    return string.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
  }
  /**
   * Convert a string containing dashes to camel case
   * @param   {string} string - input string
   * @returns {string} my-string -> myString
   */

  function dashToCamelCase(string) {
    return string.replace(/-(\w)/g, (_, c) => c.toUpperCase());
  }
  /**
   * Define default properties if they don't exist on the source object
   * @param   {Object} source - object that will receive the default properties
   * @param   {Object} defaults - object containing additional optional keys
   * @returns {Object} the original object received enhanced
   */

  function defineDefaults(source, defaults) {
    Object.entries(defaults).forEach((_ref) => {
      let [key, value] = _ref;
      if (!source[key]) source[key] = value;
    });
    return source;
  } // doese simply nothing

  function noop() {
    return this;
  }
  /**
   * Autobind the methods of a source object to itself
   * @param   {Object} source - probably a riot tag instance
   * @param   {Array<string>} methods - list of the methods to autobind
   * @returns {Object} the original object received
   */

  function autobindMethods(source, methods) {
    methods.forEach(method => {
      source[method] = source[method].bind(source);
    });
    return source;
  }
  /**
   * Helper function to set an immutable property
   * @param   {Object} source - object where the new property will be set
   * @param   {string} key - object key where the new property will be stored
   * @param   {*} value - value of the new property
   * @param   {Object} options - set the propery overriding the default options
   * @returns {Object} - the original object modified
   */

  function defineProperty(source, key, value, options) {
    if (options === void 0) {
      options = {};
    }

    Object.defineProperty(source, key, Object.assign({
      value,
      enumerable: false,
      writable: false,
      configurable: true
    }, options));
    return source;
  }
  /**
   * Define multiple properties on a target object
   * @param   {Object} source - object where the new properties will be set
   * @param   {Object} properties - object containing as key pair the key + value properties
   * @param   {Object} options - set the propery overriding the default options
   * @returns {Object} the original object modified
   */

  function defineProperties(source, properties, options) {
    Object.entries(properties).forEach((_ref2) => {
      let [key, value] = _ref2;
      defineProperty(source, key, value, options);
    });
    return source;
  }
  /**
   * Evaluate a list of attribute expressions
   * @param   {Array} attributes - attribute expressions generated by the riot compiler
   * @returns {Object} key value pairs with the result of the computation
   */

  function evaluateAttributeExpressions(attributes) {
    return attributes.reduce((acc, attribute) => {
      const {
        value,
        type
      } = attribute;

      switch (true) {
        // spread attribute
        case !attribute.name && type === expressionTypes.ATTRIBUTE:
          return Object.assign({}, acc, {}, value);
        // value attribute

        case type === expressionTypes.VALUE:
          acc[VALUE_ATTRIBUTE] = attribute.value;
          break;
        // normal attributes

        default:
          acc[dashToCamelCase(attribute.name)] = attribute.value;
      }

      return acc;
    }, {});
  }

  /**
   * Converts any DOM node/s to a loopable array
   * @param   { HTMLElement|NodeList } els - single html element or a node list
   * @returns { Array } always a loopable object
   */
  function domToArray(els) {
    // can this object be already looped?
    if (!Array.isArray(els)) {
      // is it a node list?
      if (/^\[object (HTMLCollection|NodeList|Object)\]$/.test(Object.prototype.toString.call(els)) && typeof els.length === 'number') return Array.from(els);else // if it's a single node
        // it will be returned as "array" with one single entry
        return [els];
    } // this object could be looped out of the box


    return els;
  }

  /**
   * Normalize the return values, in case of a single value we avoid to return an array
   * @param   { Array } values - list of values we want to return
   * @returns { Array|string|boolean } either the whole list of values or the single one found
   * @private
   */

  const normalize = values => values.length === 1 ? values[0] : values;
  /**
   * Parse all the nodes received to get/remove/check their attributes
   * @param   { HTMLElement|NodeList|Array } els    - DOM node/s to parse
   * @param   { string|Array }               name   - name or list of attributes
   * @param   { string }                     method - method that will be used to parse the attributes
   * @returns { Array|string } result of the parsing in a list or a single value
   * @private
   */


  function parseNodes(els, name, method) {
    const names = typeof name === 'string' ? [name] : name;
    return normalize(domToArray(els).map(el => {
      return normalize(names.map(n => el[method](n)));
    }));
  }
  /**
   * Set any attribute on a single or a list of DOM nodes
   * @param   { HTMLElement|NodeList|Array } els   - DOM node/s to parse
   * @param   { string|Object }              name  - either the name of the attribute to set
   *                                                 or a list of properties as object key - value
   * @param   { string }                     value - the new value of the attribute (optional)
   * @returns { HTMLElement|NodeList|Array } the original array of elements passed to this function
   *
   * @example
   *
   * import { set } from 'bianco.attr'
   *
   * const img = document.createElement('img')
   *
   * set(img, 'width', 100)
   *
   * // or also
   * set(img, {
   *   width: 300,
   *   height: 300
   * })
   *
   */


  function set(els, name, value) {
    const attrs = typeof name === 'object' ? name : {
      [name]: value
    };
    const props = Object.keys(attrs);
    domToArray(els).forEach(el => {
      props.forEach(prop => el.setAttribute(prop, attrs[prop]));
    });
    return els;
  }
  /**
   * Get any attribute from a single or a list of DOM nodes
   * @param   { HTMLElement|NodeList|Array } els   - DOM node/s to parse
   * @param   { string|Array }               name  - name or list of attributes to get
   * @returns { Array|string } list of the attributes found
   *
   * @example
   *
   * import { get } from 'bianco.attr'
   *
   * const img = document.createElement('img')
   *
   * get(img, 'width') // => '200'
   *
   * // or also
   * get(img, ['width', 'height']) // => ['200', '300']
   *
   * // or also
   * get([img1, img2], ['width', 'height']) // => [['200', '300'], ['500', '200']]
   */

  function get(els, name) {
    return parseNodes(els, name, 'getAttribute');
  }

  /**
   * Get all the element attributes as object
   * @param   {HTMLElement} element - DOM node we want to parse
   * @returns {Object} all the attributes found as a key value pairs
   */

  function DOMattributesToObject(element) {
    return Array.from(element.attributes).reduce((acc, attribute) => {
      acc[dashToCamelCase(attribute.name)] = attribute.value;
      return acc;
    }, {});
  }
  /**
   * Get the tag name of any DOM node
   * @param   {HTMLElement} element - DOM node we want to inspect
   * @returns {string} name to identify this dom node in riot
   */

  function getName(element) {
    return get(element, IS_DIRECTIVE) || element.tagName.toLowerCase();
  }

  /**
   * Simple helper to find DOM nodes returning them as array like loopable object
   * @param   { string|DOMNodeList } selector - either the query or the DOM nodes to arraify
   * @param   { HTMLElement }        ctx      - context defining where the query will search for the DOM nodes
   * @returns { Array } DOM nodes found as array
   */

  function $(selector, ctx) {
    return domToArray(typeof selector === 'string' ? (ctx || document).querySelectorAll(selector) : selector);
  }

  const CSS_BY_NAME = new Map();
  const STYLE_NODE_SELECTOR = 'style[riot]'; // memoized curried function

  const getStyleNode = (style => {
    return () => {
      // lazy evaluation:
      // if this function was already called before
      // we return its cached result
      if (style) return style; // create a new style element or use an existing one
      // and cache it internally

      style = $(STYLE_NODE_SELECTOR)[0] || document.createElement('style');
      set(style, 'type', 'text/css');
      /* istanbul ignore next */

      if (!style.parentNode) document.head.appendChild(style);
      return style;
    };
  })();
  /**
   * Object that will be used to inject and manage the css of every tag instance
   */


  var cssManager = {
    CSS_BY_NAME,

    /**
     * Save a tag style to be later injected into DOM
     * @param { string } name - if it's passed we will map the css to a tagname
     * @param { string } css - css string
     * @returns {Object} self
     */
    add(name, css) {
      if (!CSS_BY_NAME.has(name)) {
        CSS_BY_NAME.set(name, css);
        this.inject();
      }

      return this;
    },

    /**
     * Inject all previously saved tag styles into DOM
     * innerHTML seems slow: http://jsperf.com/riot-insert-style
     * @returns {Object} self
     */
    inject() {
      getStyleNode().innerHTML = [...CSS_BY_NAME.values()].join('\n');
      return this;
    },

    /**
     * Remove a tag style from the DOM
     * @param {string} name a registered tagname
     * @returns {Object} self
     */
    remove(name) {
      if (CSS_BY_NAME.has(name)) {
        CSS_BY_NAME.delete(name);
        this.inject();
      }

      return this;
    }

  };

  /**
   * Function to curry any javascript method
   * @param   {Function}  fn - the target function we want to curry
   * @param   {...[args]} acc - initial arguments
   * @returns {Function|*} it will return a function until the target function
   *                       will receive all of its arguments
   */
  function curry(fn) {
    for (var _len = arguments.length, acc = new Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
      acc[_key - 1] = arguments[_key];
    }

    return function () {
      for (var _len2 = arguments.length, args = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
        args[_key2] = arguments[_key2];
      }

      args = [...acc, ...args];
      return args.length < fn.length ? curry(fn, ...args) : fn(...args);
    };
  }

  const COMPONENT_CORE_HELPERS = Object.freeze({
    // component helpers
    $(selector) {
      return $(selector, this.root)[0];
    },

    $$(selector) {
      return $(selector, this.root);
    }

  });
  const COMPONENT_LIFECYCLE_METHODS = Object.freeze({
    shouldUpdate: noop,
    onBeforeMount: noop,
    onMounted: noop,
    onBeforeUpdate: noop,
    onUpdated: noop,
    onBeforeUnmount: noop,
    onUnmounted: noop
  });
  const MOCKED_TEMPLATE_INTERFACE = {
    update: noop,
    mount: noop,
    unmount: noop,
    clone: noop,
    createDOM: noop
    /**
     * Factory function to create the component templates only once
     * @param   {Function} template - component template creation function
     * @param   {Object} components - object containing the nested components
     * @returns {TemplateChunk} template chunk object
     */

  };

  function componentTemplateFactory(template, components) {
    return template(create$6, expressionTypes, bindingTypes, name => {
      return components[name] || COMPONENTS_IMPLEMENTATION_MAP.get(name);
    });
  }
  /**
   * Create the component interface needed for the @riotjs/dom-bindings tag bindings
   * @param   {string} options.css - component css
   * @param   {Function} options.template - functon that will return the dom-bindings template function
   * @param   {Object} options.exports - component interface
   * @param   {string} options.name - component name
   * @returns {Object} component like interface
   */


  function createComponent(_ref) {
    let {
      css,
      template,
      exports,
      name
    } = _ref;
    const templateFn = template ? componentTemplateFactory(template, exports ? createSubcomponents(exports.components) : {}) : MOCKED_TEMPLATE_INTERFACE;
    return (_ref2) => {
      let {
        slots,
        attributes,
        props
      } = _ref2;
      const componentAPI = callOrAssign(exports) || {};
      const component = defineComponent({
        css,
        template: templateFn,
        componentAPI,
        name
      })({
        slots,
        attributes,
        props
      }); // notice that for the components create via tag binding
      // we need to invert the mount (state/parentScope) arguments
      // the template bindings will only forward the parentScope updates
      // and never deal with the component state

      return {
        mount(element, parentScope, state) {
          return component.mount(element, state, parentScope);
        },

        update(parentScope, state) {
          return component.update(state, parentScope);
        },

        unmount(preserveRoot) {
          return component.unmount(preserveRoot);
        }

      };
    };
  }
  /**
   * Component definition function
   * @param   {Object} implementation - the componen implementation will be generated via compiler
   * @param   {Object} component - the component initial properties
   * @returns {Object} a new component implementation object
   */

  function defineComponent(_ref3) {
    let {
      css,
      template,
      componentAPI,
      name
    } = _ref3;
    // add the component css into the DOM
    if (css && name) cssManager.add(name, css);
    return curry(enhanceComponentAPI)(defineProperties( // set the component defaults without overriding the original component API
    defineDefaults(componentAPI, Object.assign({}, COMPONENT_LIFECYCLE_METHODS, {
      state: {}
    })), Object.assign({
      // defined during the component creation
      slots: null,
      root: null
    }, COMPONENT_CORE_HELPERS, {
      name,
      css,
      template
    })));
  }
  /**
   * Evaluate the component properties either from its real attributes or from its attribute expressions
   * @param   {HTMLElement} element - component root
   * @param   {Array}  attributeExpressions - attribute values generated via createAttributeBindings
   * @returns {Object} attributes key value pairs
   */

  function evaluateProps(element, attributeExpressions) {
    if (attributeExpressions === void 0) {
      attributeExpressions = [];
    }

    return Object.assign({}, DOMattributesToObject(element), {}, evaluateAttributeExpressions(attributeExpressions));
  }
  /**
   * Create the bindings to update the component attributes
   * @param   {HTMLElement} node - node where we will bind the expressions
   * @param   {Array} attributes - list of attribute bindings
   * @returns {TemplateChunk} - template bindings object
   */


  function createAttributeBindings(node, attributes) {
    if (attributes === void 0) {
      attributes = [];
    }

    const expressions = attributes.map(a => create$2(node, a));
    const binding = {};

    const updateValues = method => scope => {
      expressions.forEach(e => e[method](scope));
      return binding;
    };

    return Object.assign(binding, {
      expressions,
      mount: updateValues('mount'),
      update: updateValues('update'),
      unmount: updateValues('unmount')
    });
  }
  /**
   * Create the subcomponents that can be included inside a tag in runtime
   * @param   {Object} components - components imported in runtime
   * @returns {Object} all the components transformed into Riot.Component factory functions
   */


  function createSubcomponents(components) {
    if (components === void 0) {
      components = {};
    }

    return Object.entries(callOrAssign(components)).reduce((acc, _ref4) => {
      let [key, value] = _ref4;
      acc[camelToDashCase(key)] = createComponent(value);
      return acc;
    }, {});
  }
  /**
   * Run the component instance through all the plugins set by the user
   * @param   {Object} component - component instance
   * @returns {Object} the component enhanced by the plugins
   */


  function runPlugins(component) {
    return [...PLUGINS_SET].reduce((c, fn) => fn(c) || c, component);
  }
  /**
   * Compute the component current state merging it with its previous state
   * @param   {Object} oldState - previous state object
   * @param   {Object} newState - new state givent to the `update` call
   * @returns {Object} new object state
   */


  function computeState(oldState, newState) {
    return Object.assign({}, oldState, {}, callOrAssign(newState));
  }
  /**
   * Add eventually the "is" attribute to link this DOM node to its css
   * @param {HTMLElement} element - target root node
   * @param {string} name - name of the component mounted
   * @returns {undefined} it's a void function
   */


  function addCssHook(element, name) {
    if (getName(element) !== name) {
      set(element, 'is', name);
    }
  }
  /**
   * Component creation factory function that will enhance the user provided API
   * @param   {Object} component - a component implementation previously defined
   * @param   {Array} options.slots - component slots generated via riot compiler
   * @param   {Array} options.attributes - attribute expressions generated via riot compiler
   * @returns {Riot.Component} a riot component instance
   */


  function enhanceComponentAPI(component, _ref5) {
    let {
      slots,
      attributes,
      props
    } = _ref5;
    const initialProps = callOrAssign(props);
    return autobindMethods(runPlugins(defineProperties(Object.create(component), {
      mount(element, state, parentScope) {
        if (state === void 0) {
          state = {};
        }

        this[ATTRIBUTES_KEY_SYMBOL] = createAttributeBindings(element, attributes).mount(parentScope);
        this.props = Object.freeze(Object.assign({}, initialProps, {}, evaluateProps(element, this[ATTRIBUTES_KEY_SYMBOL].expressions)));
        this.state = computeState(this.state, state);
        this[TEMPLATE_KEY_SYMBOL] = this.template.createDOM(element).clone(); // link this object to the DOM node

        element[DOM_COMPONENT_INSTANCE_PROPERTY] = this; // add eventually the 'is' attribute

        component.name && addCssHook(element, component.name); // define the root element

        defineProperty(this, 'root', element); // define the slots array

        defineProperty(this, 'slots', slots); // before mount lifecycle event

        this.onBeforeMount(this.props, this.state); // mount the template

        this[TEMPLATE_KEY_SYMBOL].mount(element, this, parentScope);
        this.onMounted(this.props, this.state);
        return this;
      },

      update(state, parentScope) {
        if (state === void 0) {
          state = {};
        }

        if (parentScope) {
          this[ATTRIBUTES_KEY_SYMBOL].update(parentScope);
        }

        const newProps = evaluateProps(this.root, this[ATTRIBUTES_KEY_SYMBOL].expressions);
        if (this.shouldUpdate(newProps, this.props) === false) return;
        this.props = Object.freeze(Object.assign({}, initialProps, {}, newProps));
        this.state = computeState(this.state, state);
        this.onBeforeUpdate(this.props, this.state);
        this[TEMPLATE_KEY_SYMBOL].update(this, parentScope);
        this.onUpdated(this.props, this.state);
        return this;
      },

      unmount(preserveRoot) {
        this.onBeforeUnmount(this.props, this.state);
        this[ATTRIBUTES_KEY_SYMBOL].unmount(); // if the preserveRoot is null the template html will be left untouched
        // in that case the DOM cleanup will happen differently from a parent node

        this[TEMPLATE_KEY_SYMBOL].unmount(this, {}, preserveRoot === null ? null : !preserveRoot);
        this.onUnmounted(this.props, this.state);
        return this;
      }

    })), Object.keys(component).filter(prop => isFunction(component[prop])));
  }
  /**
   * Component initialization function starting from a DOM node
   * @param   {HTMLElement} element - element to upgrade
   * @param   {Object} initialProps - initial component properties
   * @param   {string} componentName - component id
   * @returns {Object} a new component instance bound to a DOM node
   */

  function mountComponent(element, initialProps, componentName) {
    const name = componentName || getName(element);
    if (!COMPONENTS_IMPLEMENTATION_MAP.has(name)) panic(`The component named "${name}" was never registered`);
    const component = COMPONENTS_IMPLEMENTATION_MAP.get(name)({
      props: initialProps
    });
    return component.mount(element);
  }

  /**
   * Similar to compose but performs from left-to-right function composition.<br/>
   * {@link https://30secondsofcode.org/function#composeright see also}
   * @param   {...[function]} fns) - list of unary function
   * @returns {*} result of the computation
   */
  /**
   * Performs right-to-left function composition.<br/>
   * Use Array.prototype.reduce() to perform right-to-left function composition.<br/>
   * The last (rightmost) function can accept one or more arguments; the remaining functions must be unary.<br/>
   * {@link https://30secondsofcode.org/function#compose original source code}
   * @param   {...[function]} fns) - list of unary function
   * @returns {*} result of the computation
   */

  function compose() {
    for (var _len2 = arguments.length, fns = new Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
      fns[_key2] = arguments[_key2];
    }

    return fns.reduce((f, g) => function () {
      return f(g(...arguments));
    });
  }

  const {
    DOM_COMPONENT_INSTANCE_PROPERTY: DOM_COMPONENT_INSTANCE_PROPERTY$1,
    COMPONENTS_IMPLEMENTATION_MAP: COMPONENTS_IMPLEMENTATION_MAP$1,
    PLUGINS_SET: PLUGINS_SET$1
  } = globals;
  /**
   * Riot public api
   */

  /**
   * Register a custom tag by name
   * @param   {string} name - component name
   * @param   {Object} implementation - tag implementation
   * @returns {Map} map containing all the components implementations
   */

  function register(name, _ref) {
    let {
      css,
      template,
      exports
    } = _ref;
    if (COMPONENTS_IMPLEMENTATION_MAP$1.has(name)) panic(`The component "${name}" was already registered`);
    COMPONENTS_IMPLEMENTATION_MAP$1.set(name, createComponent({
      name,
      css,
      template,
      exports
    }));
    return COMPONENTS_IMPLEMENTATION_MAP$1;
  }
  /**
   * Unregister a riot web component
   * @param   {string} name - component name
   * @returns {Map} map containing all the components implementations
   */

  function unregister(name) {
    if (!COMPONENTS_IMPLEMENTATION_MAP$1.has(name)) panic(`The component "${name}" was never registered`);
    COMPONENTS_IMPLEMENTATION_MAP$1.delete(name);
    cssManager.remove(name);
    return COMPONENTS_IMPLEMENTATION_MAP$1;
  }
  /**
   * Mounting function that will work only for the components that were globally registered
   * @param   {string|HTMLElement} selector - query for the selection or a DOM element
   * @param   {Object} initialProps - the initial component properties
   * @param   {string} name - optional component name
   * @returns {Array} list of nodes upgraded
   */

  function mount(selector, initialProps, name) {
    return $(selector).map(element => mountComponent(element, initialProps, name));
  }
  /**
   * Sweet unmounting helper function for the DOM node mounted manually by the user
   * @param   {string|HTMLElement} selector - query for the selection or a DOM element
   * @param   {boolean|null} keepRootElement - if true keep the root element
   * @returns {Array} list of nodes unmounted
   */

  function unmount(selector, keepRootElement) {
    return $(selector).map(element => {
      if (element[DOM_COMPONENT_INSTANCE_PROPERTY$1]) {
        element[DOM_COMPONENT_INSTANCE_PROPERTY$1].unmount(keepRootElement);
      }

      return element;
    });
  }
  /**
   * Define a riot plugin
   * @param   {Function} plugin - function that will receive all the components created
   * @returns {Set} the set containing all the plugins installed
   */

  function install(plugin) {
    if (!isFunction(plugin)) panic('Plugins must be of type function');
    if (PLUGINS_SET$1.has(plugin)) panic('This plugin was already install');
    PLUGINS_SET$1.add(plugin);
    return PLUGINS_SET$1;
  }
  /**
   * Uninstall a riot plugin
   * @param   {Function} plugin - plugin previously installed
   * @returns {Set} the set containing all the plugins installed
   */

  function uninstall(plugin) {
    if (!PLUGINS_SET$1.has(plugin)) panic('This plugin was never installed');
    PLUGINS_SET$1.delete(plugin);
    return PLUGINS_SET$1;
  }
  /**
   * Helpter method to create component without relying on the registered ones
   * @param   {Object} implementation - component implementation
   * @returns {Function} function that will allow you to mount a riot component on a DOM node
   */

  function component(implementation) {
    return (el, props) => compose(c => c.mount(el), c => c({
      props
    }), createComponent)(implementation);
  }
  /** @type {string} current riot version */

  const version = 'v4.3.5'; // expose some internal stuff that might be used from external tools

  const __ = {
    cssManager,
    defineComponent,
    globals
  };

  var riot = /*#__PURE__*/Object.freeze({
    register: register,
    unregister: unregister,
    mount: mount,
    unmount: unmount,
    install: install,
    uninstall: uninstall,
    component: component,
    version: version,
    __: __
  });

  var commonjsGlobal = typeof globalThis !== 'undefined' ? globalThis : typeof window !== 'undefined' ? window : typeof global !== 'undefined' ? global : typeof self !== 'undefined' ? self : {};

  function unwrapExports (x) {
  	return x && x.__esModule && Object.prototype.hasOwnProperty.call(x, 'default') ? x['default'] : x;
  }

  function createCommonjsModule(fn, module) {
  	return module = { exports: {} }, fn(module, module.exports), module.exports;
  }

  function getCjsExportFromNamespace (n) {
  	return n && n['default'] || n;
  }

  var _empty_module = {};

  var _empty_module$1 = /*#__PURE__*/Object.freeze({
    'default': _empty_module
  });

  var require$$1 = getCjsExportFromNamespace(_empty_module$1);

  var compiler=createCommonjsModule(function(module,exports){/* Riot Compiler v4.3.5, @license MIT */(function(global,factory){factory(exports,require$$1,require$$1);})(commonjsGlobal,function(exports,fs,path$1){fs=fs&&fs.hasOwnProperty('default')?fs['default']:fs;path$1=path$1&&path$1.hasOwnProperty('default')?path$1['default']:path$1;const TAG_LOGIC_PROPERTY='exports';const TAG_CSS_PROPERTY='css';const TAG_TEMPLATE_PROPERTY='template';const TAG_NAME_PROPERTY='name';function unwrapExports(x){return x&&x.__esModule&&Object.prototype.hasOwnProperty.call(x,'default')?x['default']:x;}function createCommonjsModule(fn,module){return module={exports:{}},fn(module,module.exports),module.exports;}function getCjsExportFromNamespace(n){return n&&n['default']||n;}var types=createCommonjsModule(function(module,exports){var __extends=this&&this.__extends||function(){var _extendStatics=function extendStatics(d,b){_extendStatics=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(d,b){d.__proto__=b;}||function(d,b){for(var p in b)if(b.hasOwnProperty(p))d[p]=b[p];};return _extendStatics(d,b);};return function(d,b){_extendStatics(d,b);function __(){this.constructor=d;}d.prototype=b===null?Object.create(b):(__.prototype=b.prototype,new __());};}();Object.defineProperty(exports,"__esModule",{value:true});var Op=Object.prototype;var objToStr=Op.toString;var hasOwn=Op.hasOwnProperty;var BaseType=/** @class */function(){function BaseType(){}BaseType.prototype.assert=function(value,deep){if(!this.check(value,deep)){var str=shallowStringify(value);throw new Error(str+" does not match type "+this);}return true;};BaseType.prototype.arrayOf=function(){var elemType=this;return new ArrayType(elemType);};return BaseType;}();var ArrayType=/** @class */function(_super){__extends(ArrayType,_super);function ArrayType(elemType){var _this=_super.call(this)||this;_this.elemType=elemType;_this.kind="ArrayType";return _this;}ArrayType.prototype.toString=function(){return "["+this.elemType+"]";};ArrayType.prototype.check=function(value,deep){var _this=this;return Array.isArray(value)&&value.every(function(elem){return _this.elemType.check(elem,deep);});};return ArrayType;}(BaseType);var IdentityType=/** @class */function(_super){__extends(IdentityType,_super);function IdentityType(value){var _this=_super.call(this)||this;_this.value=value;_this.kind="IdentityType";return _this;}IdentityType.prototype.toString=function(){return String(this.value);};IdentityType.prototype.check=function(value,deep){var result=value===this.value;if(!result&&typeof deep==="function"){deep(this,value);}return result;};return IdentityType;}(BaseType);var ObjectType=/** @class */function(_super){__extends(ObjectType,_super);function ObjectType(fields){var _this=_super.call(this)||this;_this.fields=fields;_this.kind="ObjectType";return _this;}ObjectType.prototype.toString=function(){return "{ "+this.fields.join(", ")+" }";};ObjectType.prototype.check=function(value,deep){return objToStr.call(value)===objToStr.call({})&&this.fields.every(function(field){return field.type.check(value[field.name],deep);});};return ObjectType;}(BaseType);var OrType=/** @class */function(_super){__extends(OrType,_super);function OrType(types){var _this=_super.call(this)||this;_this.types=types;_this.kind="OrType";return _this;}OrType.prototype.toString=function(){return this.types.join(" | ");};OrType.prototype.check=function(value,deep){return this.types.some(function(type){return type.check(value,deep);});};return OrType;}(BaseType);var PredicateType=/** @class */function(_super){__extends(PredicateType,_super);function PredicateType(name,predicate){var _this=_super.call(this)||this;_this.name=name;_this.predicate=predicate;_this.kind="PredicateType";return _this;}PredicateType.prototype.toString=function(){return this.name;};PredicateType.prototype.check=function(value,deep){var result=this.predicate(value,deep);if(!result&&typeof deep==="function"){deep(this,value);}return result;};return PredicateType;}(BaseType);var Def=/** @class */function(){function Def(type,typeName){this.type=type;this.typeName=typeName;this.baseNames=[];this.ownFields=Object.create(null);// Includes own typeName. Populated during finalization.
  this.allSupertypes=Object.create(null);// Linear inheritance hierarchy. Populated during finalization.
  this.supertypeList=[];// Includes inherited fields.
  this.allFields=Object.create(null);// Non-hidden keys of allFields.
  this.fieldNames=[];// This property will be overridden as true by individual Def instances
  // when they are finalized.
  this.finalized=false;// False by default until .build(...) is called on an instance.
  this.buildable=false;this.buildParams=[];}Def.prototype.isSupertypeOf=function(that){if(that instanceof Def){if(this.finalized!==true||that.finalized!==true){throw new Error("");}return hasOwn.call(that.allSupertypes,this.typeName);}else{throw new Error(that+" is not a Def");}};Def.prototype.checkAllFields=function(value,deep){var allFields=this.allFields;if(this.finalized!==true){throw new Error(""+this.typeName);}function checkFieldByName(name){var field=allFields[name];var type=field.type;var child=field.getValue(value);return type.check(child,deep);}return value!==null&&typeof value==="object"&&Object.keys(allFields).every(checkFieldByName);};Def.prototype.bases=function(){var supertypeNames=[];for(var _i=0;_i<arguments.length;_i++){supertypeNames[_i]=arguments[_i];}var bases=this.baseNames;if(this.finalized){if(supertypeNames.length!==bases.length){throw new Error("");}for(var i=0;i<supertypeNames.length;i++){if(supertypeNames[i]!==bases[i]){throw new Error("");}}return this;}supertypeNames.forEach(function(baseName){// This indexOf lookup may be O(n), but the typical number of base
  // names is very small, and indexOf is a native Array method.
  if(bases.indexOf(baseName)<0){bases.push(baseName);}});return this;// For chaining.
  };return Def;}();exports.Def=Def;var Field=/** @class */function(){function Field(name,type,defaultFn,hidden){this.name=name;this.type=type;this.defaultFn=defaultFn;this.hidden=!!hidden;}Field.prototype.toString=function(){return JSON.stringify(this.name)+": "+this.type;};Field.prototype.getValue=function(obj){var value=obj[this.name];if(typeof value!=="undefined"){return value;}if(typeof this.defaultFn==="function"){value=this.defaultFn.call(obj);}return value;};return Field;}();function shallowStringify(value){if(Array.isArray(value)){return "["+value.map(shallowStringify).join(", ")+"]";}if(value&&typeof value==="object"){return "{ "+Object.keys(value).map(function(key){return key+": "+value[key];}).join(", ")+" }";}return JSON.stringify(value);}function typesPlugin(_fork){var Type={or:function or(){var types=[];for(var _i=0;_i<arguments.length;_i++){types[_i]=arguments[_i];}return new OrType(types.map(function(type){return Type.from(type);}));},from:function from(value,name){if(value instanceof ArrayType||value instanceof IdentityType||value instanceof ObjectType||value instanceof OrType||value instanceof PredicateType){return value;}// The Def type is used as a helper for constructing compound
  // interface types for AST nodes.
  if(value instanceof Def){return value.type;}// Support [ElemType] syntax.
  if(isArray.check(value)){if(value.length!==1){throw new Error("only one element type is permitted for typed arrays");}return new ArrayType(Type.from(value[0]));}// Support { someField: FieldType, ... } syntax.
  if(isObject.check(value)){return new ObjectType(Object.keys(value).map(function(name){return new Field(name,Type.from(value[name],name));}));}if(typeof value==="function"){var bicfIndex=builtInCtorFns.indexOf(value);if(bicfIndex>=0){return builtInCtorTypes[bicfIndex];}if(typeof name!=="string"){throw new Error("missing name");}return new PredicateType(name,value);}// As a last resort, toType returns a type that matches any value that
  // is === from. This is primarily useful for literal values like
  // toType(null), but it has the additional advantage of allowing
  // toType to be a total function.
  return new IdentityType(value);},// Define a type whose name is registered in a namespace (the defCache) so
  // that future definitions will return the same type given the same name.
  // In particular, this system allows for circular and forward definitions.
  // The Def object d returned from Type.def may be used to configure the
  // type d.type by calling methods such as d.bases, d.build, and d.field.
  def:function def(typeName){return hasOwn.call(defCache,typeName)?defCache[typeName]:defCache[typeName]=new DefImpl(typeName);},hasDef:function hasDef(typeName){return hasOwn.call(defCache,typeName);}};var builtInCtorFns=[];var builtInCtorTypes=[];var builtInTypes={};function defBuiltInType(example,name){var objStr=objToStr.call(example);var type=new PredicateType(name,function(value){return objToStr.call(value)===objStr;});builtInTypes[name]=type;if(example&&typeof example.constructor==="function"){builtInCtorFns.push(example.constructor);builtInCtorTypes.push(type);}return type;}// These types check the underlying [[Class]] attribute of the given
  // value, rather than using the problematic typeof operator. Note however
  // that no subtyping is considered; so, for instance, isObject.check
  // returns false for [], /./, new Date, and null.
  var isString=defBuiltInType("truthy","string");var isFunction=defBuiltInType(function(){},"function");var isArray=defBuiltInType([],"array");var isObject=defBuiltInType({},"object");var isRegExp=defBuiltInType(/./,"RegExp");var isDate=defBuiltInType(new Date(),"Date");var isNumber=defBuiltInType(3,"number");var isBoolean=defBuiltInType(true,"boolean");var isNull=defBuiltInType(null,"null");var isUndefined=defBuiltInType(void 0,"undefined");// In order to return the same Def instance every time Type.def is called
  // with a particular name, those instances need to be stored in a cache.
  var defCache=Object.create(null);function defFromValue(value){if(value&&typeof value==="object"){var type=value.type;if(typeof type==="string"&&hasOwn.call(defCache,type)){var d=defCache[type];if(d.finalized){return d;}}}return null;}var DefImpl=/** @class */function(_super){__extends(DefImpl,_super);function DefImpl(typeName){var _this=_super.call(this,new PredicateType(typeName,function(value,deep){return _this.check(value,deep);}),typeName)||this;return _this;}DefImpl.prototype.check=function(value,deep){if(this.finalized!==true){throw new Error("prematurely checking unfinalized type "+this.typeName);}// A Def type can only match an object value.
  if(value===null||typeof value!=="object"){return false;}var vDef=defFromValue(value);if(!vDef){// If we couldn't infer the Def associated with the given value,
  // and we expected it to be a SourceLocation or a Position, it was
  // probably just missing a "type" field (because Esprima does not
  // assign a type property to such nodes). Be optimistic and let
  // this.checkAllFields make the final decision.
  if(this.typeName==="SourceLocation"||this.typeName==="Position"){return this.checkAllFields(value,deep);}// Calling this.checkAllFields for any other type of node is both
  // bad for performance and way too forgiving.
  return false;}// If checking deeply and vDef === this, then we only need to call
  // checkAllFields once. Calling checkAllFields is too strict when deep
  // is false, because then we only care about this.isSupertypeOf(vDef).
  if(deep&&vDef===this){return this.checkAllFields(value,deep);}// In most cases we rely exclusively on isSupertypeOf to make O(1)
  // subtyping determinations. This suffices in most situations outside
  // of unit tests, since interface conformance is checked whenever new
  // instances are created using builder functions.
  if(!this.isSupertypeOf(vDef)){return false;}// The exception is when deep is true; then, we recursively check all
  // fields.
  if(!deep){return true;}// Use the more specific Def (vDef) to perform the deep check, but
  // shallow-check fields defined by the less specific Def (this).
  return vDef.checkAllFields(value,deep)&&this.checkAllFields(value,false);};DefImpl.prototype.build=function(){var _this=this;var buildParams=[];for(var _i=0;_i<arguments.length;_i++){buildParams[_i]=arguments[_i];}// Calling Def.prototype.build multiple times has the effect of merely
  // redefining this property.
  this.buildParams=buildParams;if(this.buildable){// If this Def is already buildable, update self.buildParams and
  // continue using the old builder function.
  return this;}// Every buildable type will have its "type" field filled in
  // automatically. This includes types that are not subtypes of Node,
  // like SourceLocation, but that seems harmless (TODO?).
  this.field("type",String,function(){return _this.typeName;});// Override Dp.buildable for this Def instance.
  this.buildable=true;var addParam=function addParam(built,param,arg,isArgAvailable){if(hasOwn.call(built,param))return;var all=_this.allFields;if(!hasOwn.call(all,param)){throw new Error(""+param);}var field=all[param];var type=field.type;var value;if(isArgAvailable){value=arg;}else if(field.defaultFn){// Expose the partially-built object to the default
  // function as its `this` object.
  value=field.defaultFn.call(built);}else{var message="no value or default function given for field "+JSON.stringify(param)+" of "+_this.typeName+"("+_this.buildParams.map(function(name){return all[name];}).join(", ")+")";throw new Error(message);}if(!type.check(value)){throw new Error(shallowStringify(value)+" does not match field "+field+" of type "+_this.typeName);}built[param]=value;};// Calling the builder function will construct an instance of the Def,
  // with positional arguments mapped to the fields original passed to .build.
  // If not enough arguments are provided, the default value for the remaining fields
  // will be used.
  var builder=function builder(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}var argc=args.length;if(!_this.finalized){throw new Error("attempting to instantiate unfinalized type "+_this.typeName);}var built=Object.create(nodePrototype);_this.buildParams.forEach(function(param,i){if(i<argc){addParam(built,param,args[i],true);}else{addParam(built,param,null,false);}});Object.keys(_this.allFields).forEach(function(param){// Use the default value.
  addParam(built,param,null,false);});// Make sure that the "type" field was filled automatically.
  if(built.type!==_this.typeName){throw new Error("");}return built;};// Calling .from on the builder function will construct an instance of the Def,
  // using field values from the passed object. For fields missing from the passed object,
  // their default value will be used.
  builder.from=function(obj){if(!_this.finalized){throw new Error("attempting to instantiate unfinalized type "+_this.typeName);}var built=Object.create(nodePrototype);Object.keys(_this.allFields).forEach(function(param){if(hasOwn.call(obj,param)){addParam(built,param,obj[param],true);}else{addParam(built,param,null,false);}});// Make sure that the "type" field was filled automatically.
  if(built.type!==_this.typeName){throw new Error("");}return built;};Object.defineProperty(builders,getBuilderName(this.typeName),{enumerable:true,value:builder});return this;};// The reason fields are specified using .field(...) instead of an object
  // literal syntax is somewhat subtle: the object literal syntax would
  // support only one key and one value, but with .field(...) we can pass
  // any number of arguments to specify the field.
  DefImpl.prototype.field=function(name,type,defaultFn,hidden){if(this.finalized){console.error("Ignoring attempt to redefine field "+JSON.stringify(name)+" of finalized type "+JSON.stringify(this.typeName));return this;}this.ownFields[name]=new Field(name,Type.from(type),defaultFn,hidden);return this;// For chaining.
  };DefImpl.prototype.finalize=function(){var _this=this;// It's not an error to finalize a type more than once, but only the
  // first call to .finalize does anything.
  if(!this.finalized){var allFields=this.allFields;var allSupertypes=this.allSupertypes;this.baseNames.forEach(function(name){var def=defCache[name];if(def instanceof Def){def.finalize();extend(allFields,def.allFields);extend(allSupertypes,def.allSupertypes);}else{var message="unknown supertype name "+JSON.stringify(name)+" for subtype "+JSON.stringify(_this.typeName);throw new Error(message);}});// TODO Warn if fields are overridden with incompatible types.
  extend(allFields,this.ownFields);allSupertypes[this.typeName]=this;this.fieldNames.length=0;for(var fieldName in allFields){if(hasOwn.call(allFields,fieldName)&&!allFields[fieldName].hidden){this.fieldNames.push(fieldName);}}// Types are exported only once they have been finalized.
  Object.defineProperty(namedTypes,this.typeName,{enumerable:true,value:this.type});this.finalized=true;// A linearization of the inheritance hierarchy.
  populateSupertypeList(this.typeName,this.supertypeList);if(this.buildable&&this.supertypeList.lastIndexOf("Expression")>=0){wrapExpressionBuilderWithStatement(this.typeName);}}};return DefImpl;}(Def);// Note that the list returned by this function is a copy of the internal
  // supertypeList, *without* the typeName itself as the first element.
  function getSupertypeNames(typeName){if(!hasOwn.call(defCache,typeName)){throw new Error("");}var d=defCache[typeName];if(d.finalized!==true){throw new Error("");}return d.supertypeList.slice(1);}// Returns an object mapping from every known type in the defCache to the
  // most specific supertype whose name is an own property of the candidates
  // object.
  function computeSupertypeLookupTable(candidates){var table={};var typeNames=Object.keys(defCache);var typeNameCount=typeNames.length;for(var i=0;i<typeNameCount;++i){var typeName=typeNames[i];var d=defCache[typeName];if(d.finalized!==true){throw new Error(""+typeName);}for(var j=0;j<d.supertypeList.length;++j){var superTypeName=d.supertypeList[j];if(hasOwn.call(candidates,superTypeName)){table[typeName]=superTypeName;break;}}}return table;}var builders=Object.create(null);// This object is used as prototype for any node created by a builder.
  var nodePrototype={};// Call this function to define a new method to be shared by all AST
  // nodes. The replaced method (if any) is returned for easy wrapping.
  function defineMethod(name,func){var old=nodePrototype[name];// Pass undefined as func to delete nodePrototype[name].
  if(isUndefined.check(func)){delete nodePrototype[name];}else{isFunction.assert(func);Object.defineProperty(nodePrototype,name,{enumerable:true,configurable:true,value:func});}return old;}function getBuilderName(typeName){return typeName.replace(/^[A-Z]+/,function(upperCasePrefix){var len=upperCasePrefix.length;switch(len){case 0:return "";// If there's only one initial capital letter, just lower-case it.
  case 1:return upperCasePrefix.toLowerCase();default:// If there's more than one initial capital letter, lower-case
  // all but the last one, so that XMLDefaultDeclaration (for
  // example) becomes xmlDefaultDeclaration.
  return upperCasePrefix.slice(0,len-1).toLowerCase()+upperCasePrefix.charAt(len-1);}});}function getStatementBuilderName(typeName){typeName=getBuilderName(typeName);return typeName.replace(/(Expression)?$/,"Statement");}var namedTypes={};// Like Object.keys, but aware of what fields each AST type should have.
  function getFieldNames(object){var d=defFromValue(object);if(d){return d.fieldNames.slice(0);}if("type"in object){throw new Error("did not recognize object of type "+JSON.stringify(object.type));}return Object.keys(object);}// Get the value of an object property, taking object.type and default
  // functions into account.
  function getFieldValue(object,fieldName){var d=defFromValue(object);if(d){var field=d.allFields[fieldName];if(field){return field.getValue(object);}}return object&&object[fieldName];}// Iterate over all defined fields of an object, including those missing
  // or undefined, passing each field name and effective value (as returned
  // by getFieldValue) to the callback. If the object has no corresponding
  // Def, the callback will never be called.
  function eachField(object,callback,context){getFieldNames(object).forEach(function(name){callback.call(this,name,getFieldValue(object,name));},context);}// Similar to eachField, except that iteration stops as soon as the
  // callback returns a truthy value. Like Array.prototype.some, the final
  // result is either true or false to indicates whether the callback
  // returned true for any element or not.
  function someField(object,callback,context){return getFieldNames(object).some(function(name){return callback.call(this,name,getFieldValue(object,name));},context);}// Adds an additional builder for Expression subtypes
  // that wraps the built Expression in an ExpressionStatements.
  function wrapExpressionBuilderWithStatement(typeName){var wrapperName=getStatementBuilderName(typeName);// skip if the builder already exists
  if(builders[wrapperName])return;// the builder function to wrap with builders.ExpressionStatement
  var wrapped=builders[getBuilderName(typeName)];// skip if there is nothing to wrap
  if(!wrapped)return;var builder=function builder(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}return builders.expressionStatement(wrapped.apply(builders,args));};builder.from=function(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}return builders.expressionStatement(wrapped.from.apply(builders,args));};builders[wrapperName]=builder;}function populateSupertypeList(typeName,list){list.length=0;list.push(typeName);var lastSeen=Object.create(null);for(var pos=0;pos<list.length;++pos){typeName=list[pos];var d=defCache[typeName];if(d.finalized!==true){throw new Error("");}// If we saw typeName earlier in the breadth-first traversal,
  // delete the last-seen occurrence.
  if(hasOwn.call(lastSeen,typeName)){delete list[lastSeen[typeName]];}// Record the new index of the last-seen occurrence of typeName.
  lastSeen[typeName]=pos;// Enqueue the base names of this type.
  list.push.apply(list,d.baseNames);}// Compaction loop to remove array holes.
  for(var to=0,from=to,len=list.length;from<len;++from){if(hasOwn.call(list,from)){list[to++]=list[from];}}list.length=to;}function extend(into,from){Object.keys(from).forEach(function(name){into[name]=from[name];});return into;}function finalize(){Object.keys(defCache).forEach(function(name){defCache[name].finalize();});}return {Type:Type,builtInTypes:builtInTypes,getSupertypeNames:getSupertypeNames,computeSupertypeLookupTable:computeSupertypeLookupTable,builders:builders,defineMethod:defineMethod,getBuilderName:getBuilderName,getStatementBuilderName:getStatementBuilderName,namedTypes:namedTypes,getFieldNames:getFieldNames,getFieldValue:getFieldValue,eachField:eachField,someField:someField,finalize:finalize};}exports.default=typesPlugin;});unwrapExports(types);var types_1=types.Def;var path=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var Op=Object.prototype;var hasOwn=Op.hasOwnProperty;function pathPlugin(fork){var types=fork.use(types_1.default);var isArray=types.builtInTypes.array;var isNumber=types.builtInTypes.number;var Path=function Path(value,parentPath,name){if(!(this instanceof Path)){throw new Error("Path constructor cannot be invoked without 'new'");}if(parentPath){if(!(parentPath instanceof Path)){throw new Error("");}}else{parentPath=null;name=null;}// The value encapsulated by this Path, generally equal to
  // parentPath.value[name] if we have a parentPath.
  this.value=value;// The immediate parent Path of this Path.
  this.parentPath=parentPath;// The name of the property of parentPath.value through which this
  // Path's value was reached.
  this.name=name;// Calling path.get("child") multiple times always returns the same
  // child Path object, for both performance and consistency reasons.
  this.__childCache=null;};var Pp=Path.prototype;function getChildCache(path){// Lazily create the child cache. This also cheapens cache
  // invalidation, since you can just reset path.__childCache to null.
  return path.__childCache||(path.__childCache=Object.create(null));}function getChildPath(path,name){var cache=getChildCache(path);var actualChildValue=path.getValueProperty(name);var childPath=cache[name];if(!hasOwn.call(cache,name)||// Ensure consistency between cache and reality.
  childPath.value!==actualChildValue){childPath=cache[name]=new path.constructor(actualChildValue,path,name);}return childPath;}// This method is designed to be overridden by subclasses that need to
  // handle missing properties, etc.
  Pp.getValueProperty=function getValueProperty(name){return this.value[name];};Pp.get=function get(){var names=[];for(var _i=0;_i<arguments.length;_i++){names[_i]=arguments[_i];}var path=this;var count=names.length;for(var i=0;i<count;++i){path=getChildPath(path,names[i]);}return path;};Pp.each=function each(callback,context){var childPaths=[];var len=this.value.length;var i=0;// Collect all the original child paths before invoking the callback.
  for(var i=0;i<len;++i){if(hasOwn.call(this.value,i)){childPaths[i]=this.get(i);}}// Invoke the callback on just the original child paths, regardless of
  // any modifications made to the array by the callback. I chose these
  // semantics over cleverly invoking the callback on new elements because
  // this way is much easier to reason about.
  context=context||this;for(i=0;i<len;++i){if(hasOwn.call(childPaths,i)){callback.call(context,childPaths[i]);}}};Pp.map=function map(callback,context){var result=[];this.each(function(childPath){result.push(callback.call(this,childPath));},context);return result;};Pp.filter=function filter(callback,context){var result=[];this.each(function(childPath){if(callback.call(this,childPath)){result.push(childPath);}},context);return result;};function emptyMoves(){}function getMoves(path,offset,start,end){isArray.assert(path.value);if(offset===0){return emptyMoves;}var length=path.value.length;if(length<1){return emptyMoves;}var argc=arguments.length;if(argc===2){start=0;end=length;}else if(argc===3){start=Math.max(start,0);end=length;}else{start=Math.max(start,0);end=Math.min(end,length);}isNumber.assert(start);isNumber.assert(end);var moves=Object.create(null);var cache=getChildCache(path);for(var i=start;i<end;++i){if(hasOwn.call(path.value,i)){var childPath=path.get(i);if(childPath.name!==i){throw new Error("");}var newIndex=i+offset;childPath.name=newIndex;moves[newIndex]=childPath;delete cache[i];}}delete cache.length;return function(){for(var newIndex in moves){var childPath=moves[newIndex];if(childPath.name!==+newIndex){throw new Error("");}cache[newIndex]=childPath;path.value[newIndex]=childPath.value;}};}Pp.shift=function shift(){var move=getMoves(this,-1);var result=this.value.shift();move();return result;};Pp.unshift=function unshift(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}var move=getMoves(this,args.length);var result=this.value.unshift.apply(this.value,args);move();return result;};Pp.push=function push(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}isArray.assert(this.value);delete getChildCache(this).length;return this.value.push.apply(this.value,args);};Pp.pop=function pop(){isArray.assert(this.value);var cache=getChildCache(this);delete cache[this.value.length-1];delete cache.length;return this.value.pop();};Pp.insertAt=function insertAt(index){var argc=arguments.length;var move=getMoves(this,argc-1,index);if(move===emptyMoves&&argc<=1){return this;}index=Math.max(index,0);for(var i=1;i<argc;++i){this.value[index+i-1]=arguments[i];}move();return this;};Pp.insertBefore=function insertBefore(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}var pp=this.parentPath;var argc=args.length;var insertAtArgs=[this.name];for(var i=0;i<argc;++i){insertAtArgs.push(args[i]);}return pp.insertAt.apply(pp,insertAtArgs);};Pp.insertAfter=function insertAfter(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}var pp=this.parentPath;var argc=args.length;var insertAtArgs=[this.name+1];for(var i=0;i<argc;++i){insertAtArgs.push(args[i]);}return pp.insertAt.apply(pp,insertAtArgs);};function repairRelationshipWithParent(path){if(!(path instanceof Path)){throw new Error("");}var pp=path.parentPath;if(!pp){// Orphan paths have no relationship to repair.
  return path;}var parentValue=pp.value;var parentCache=getChildCache(pp);// Make sure parentCache[path.name] is populated.
  if(parentValue[path.name]===path.value){parentCache[path.name]=path;}else if(isArray.check(parentValue)){// Something caused path.name to become out of date, so attempt to
  // recover by searching for path.value in parentValue.
  var i=parentValue.indexOf(path.value);if(i>=0){parentCache[path.name=i]=path;}}else{// If path.value disagrees with parentValue[path.name], and
  // path.name is not an array index, let path.value become the new
  // parentValue[path.name] and update parentCache accordingly.
  parentValue[path.name]=path.value;parentCache[path.name]=path;}if(parentValue[path.name]!==path.value){throw new Error("");}if(path.parentPath.get(path.name)!==path){throw new Error("");}return path;}Pp.replace=function replace(replacement){var results=[];var parentValue=this.parentPath.value;var parentCache=getChildCache(this.parentPath);var count=arguments.length;repairRelationshipWithParent(this);if(isArray.check(parentValue)){var originalLength=parentValue.length;var move=getMoves(this.parentPath,count-1,this.name+1);var spliceArgs=[this.name,1];for(var i=0;i<count;++i){spliceArgs.push(arguments[i]);}var splicedOut=parentValue.splice.apply(parentValue,spliceArgs);if(splicedOut[0]!==this.value){throw new Error("");}if(parentValue.length!==originalLength-1+count){throw new Error("");}move();if(count===0){delete this.value;delete parentCache[this.name];this.__childCache=null;}else{if(parentValue[this.name]!==replacement){throw new Error("");}if(this.value!==replacement){this.value=replacement;this.__childCache=null;}for(i=0;i<count;++i){results.push(this.parentPath.get(this.name+i));}if(results[0]!==this){throw new Error("");}}}else if(count===1){if(this.value!==replacement){this.__childCache=null;}this.value=parentValue[this.name]=replacement;results.push(this);}else if(count===0){delete parentValue[this.name];delete this.value;this.__childCache=null;// Leave this path cached as parentCache[this.name], even though
  // it no longer has a value defined.
  }else{throw new Error("Could not replace path");}return results;};return Path;}exports.default=pathPlugin;module.exports=exports["default"];});unwrapExports(path);var scope=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var hasOwn=Object.prototype.hasOwnProperty;function scopePlugin(fork){var types=fork.use(types_1.default);var Type=types.Type;var namedTypes=types.namedTypes;var Node=namedTypes.Node;var Expression=namedTypes.Expression;var isArray=types.builtInTypes.array;var b=types.builders;var Scope=function Scope(path,parentScope){if(!(this instanceof Scope)){throw new Error("Scope constructor cannot be invoked without 'new'");}ScopeType.assert(path.value);var depth;if(parentScope){if(!(parentScope instanceof Scope)){throw new Error("");}depth=parentScope.depth+1;}else{parentScope=null;depth=0;}Object.defineProperties(this,{path:{value:path},node:{value:path.value},isGlobal:{value:!parentScope,enumerable:true},depth:{value:depth},parent:{value:parentScope},bindings:{value:{}},types:{value:{}}});};var scopeTypes=[// Program nodes introduce global scopes.
  namedTypes.Program,// Function is the supertype of FunctionExpression,
  // FunctionDeclaration, ArrowExpression, etc.
  namedTypes.Function,// In case you didn't know, the caught parameter shadows any variable
  // of the same name in an outer scope.
  namedTypes.CatchClause];var ScopeType=Type.or.apply(Type,scopeTypes);Scope.isEstablishedBy=function(node){return ScopeType.check(node);};var Sp=Scope.prototype;// Will be overridden after an instance lazily calls scanScope.
  Sp.didScan=false;Sp.declares=function(name){this.scan();return hasOwn.call(this.bindings,name);};Sp.declaresType=function(name){this.scan();return hasOwn.call(this.types,name);};Sp.declareTemporary=function(prefix){if(prefix){if(!/^[a-z$_]/i.test(prefix)){throw new Error("");}}else{prefix="t$";}// Include this.depth in the name to make sure the name does not
  // collide with any variables in nested/enclosing scopes.
  prefix+=this.depth.toString(36)+"$";this.scan();var index=0;while(this.declares(prefix+index)){++index;}var name=prefix+index;return this.bindings[name]=types.builders.identifier(name);};Sp.injectTemporary=function(identifier,init){identifier||(identifier=this.declareTemporary());var bodyPath=this.path.get("body");if(namedTypes.BlockStatement.check(bodyPath.value)){bodyPath=bodyPath.get("body");}bodyPath.unshift(b.variableDeclaration("var",[b.variableDeclarator(identifier,init||null)]));return identifier;};Sp.scan=function(force){if(force||!this.didScan){for(var name in this.bindings){// Empty out this.bindings, just in cases.
  delete this.bindings[name];}scanScope(this.path,this.bindings,this.types);this.didScan=true;}};Sp.getBindings=function(){this.scan();return this.bindings;};Sp.getTypes=function(){this.scan();return this.types;};function scanScope(path,bindings,scopeTypes){var node=path.value;ScopeType.assert(node);if(namedTypes.CatchClause.check(node)){// A catch clause establishes a new scope but the only variable
  // bound in that scope is the catch parameter. Any other
  // declarations create bindings in the outer scope.
  addPattern(path.get("param"),bindings);}else{recursiveScanScope(path,bindings,scopeTypes);}}function recursiveScanScope(path,bindings,scopeTypes){var node=path.value;if(path.parent&&namedTypes.FunctionExpression.check(path.parent.node)&&path.parent.node.id){addPattern(path.parent.get("id"),bindings);}if(!node);else if(isArray.check(node)){path.each(function(childPath){recursiveScanChild(childPath,bindings,scopeTypes);});}else if(namedTypes.Function.check(node)){path.get("params").each(function(paramPath){addPattern(paramPath,bindings);});recursiveScanChild(path.get("body"),bindings,scopeTypes);}else if(namedTypes.TypeAlias&&namedTypes.TypeAlias.check(node)||namedTypes.InterfaceDeclaration&&namedTypes.InterfaceDeclaration.check(node)||namedTypes.TSTypeAliasDeclaration&&namedTypes.TSTypeAliasDeclaration.check(node)||namedTypes.TSInterfaceDeclaration&&namedTypes.TSInterfaceDeclaration.check(node)){addTypePattern(path.get("id"),scopeTypes);}else if(namedTypes.VariableDeclarator.check(node)){addPattern(path.get("id"),bindings);recursiveScanChild(path.get("init"),bindings,scopeTypes);}else if(node.type==="ImportSpecifier"||node.type==="ImportNamespaceSpecifier"||node.type==="ImportDefaultSpecifier"){addPattern(// Esprima used to use the .name field to refer to the local
  // binding identifier for ImportSpecifier nodes, but .id for
  // ImportNamespaceSpecifier and ImportDefaultSpecifier nodes.
  // ESTree/Acorn/ESpree use .local for all three node types.
  path.get(node.local?"local":node.name?"name":"id"),bindings);}else if(Node.check(node)&&!Expression.check(node)){types.eachField(node,function(name,child){var childPath=path.get(name);if(!pathHasValue(childPath,child)){throw new Error("");}recursiveScanChild(childPath,bindings,scopeTypes);});}}function pathHasValue(path,value){if(path.value===value){return true;}// Empty arrays are probably produced by defaults.emptyArray, in which
  // case is makes sense to regard them as equivalent, if not ===.
  if(Array.isArray(path.value)&&path.value.length===0&&Array.isArray(value)&&value.length===0){return true;}return false;}function recursiveScanChild(path,bindings,scopeTypes){var node=path.value;if(!node||Expression.check(node));else if(namedTypes.FunctionDeclaration.check(node)&&node.id!==null){addPattern(path.get("id"),bindings);}else if(namedTypes.ClassDeclaration&&namedTypes.ClassDeclaration.check(node)){addPattern(path.get("id"),bindings);}else if(ScopeType.check(node)){if(namedTypes.CatchClause.check(node)&&// TODO Broaden this to accept any pattern.
  namedTypes.Identifier.check(node.param)){var catchParamName=node.param.name;var hadBinding=hasOwn.call(bindings,catchParamName);// Any declarations that occur inside the catch body that do
  // not have the same name as the catch parameter should count
  // as bindings in the outer scope.
  recursiveScanScope(path.get("body"),bindings,scopeTypes);// If a new binding matching the catch parameter name was
  // created while scanning the catch body, ignore it because it
  // actually refers to the catch parameter and not the outer
  // scope that we're currently scanning.
  if(!hadBinding){delete bindings[catchParamName];}}}else{recursiveScanScope(path,bindings,scopeTypes);}}function addPattern(patternPath,bindings){var pattern=patternPath.value;namedTypes.Pattern.assert(pattern);if(namedTypes.Identifier.check(pattern)){if(hasOwn.call(bindings,pattern.name)){bindings[pattern.name].push(patternPath);}else{bindings[pattern.name]=[patternPath];}}else if(namedTypes.AssignmentPattern&&namedTypes.AssignmentPattern.check(pattern)){addPattern(patternPath.get('left'),bindings);}else if(namedTypes.ObjectPattern&&namedTypes.ObjectPattern.check(pattern)){patternPath.get('properties').each(function(propertyPath){var property=propertyPath.value;if(namedTypes.Pattern.check(property)){addPattern(propertyPath,bindings);}else if(namedTypes.Property.check(property)){addPattern(propertyPath.get('value'),bindings);}else if(namedTypes.SpreadProperty&&namedTypes.SpreadProperty.check(property)){addPattern(propertyPath.get('argument'),bindings);}});}else if(namedTypes.ArrayPattern&&namedTypes.ArrayPattern.check(pattern)){patternPath.get('elements').each(function(elementPath){var element=elementPath.value;if(namedTypes.Pattern.check(element)){addPattern(elementPath,bindings);}else if(namedTypes.SpreadElement&&namedTypes.SpreadElement.check(element)){addPattern(elementPath.get("argument"),bindings);}});}else if(namedTypes.PropertyPattern&&namedTypes.PropertyPattern.check(pattern)){addPattern(patternPath.get('pattern'),bindings);}else if(namedTypes.SpreadElementPattern&&namedTypes.SpreadElementPattern.check(pattern)||namedTypes.SpreadPropertyPattern&&namedTypes.SpreadPropertyPattern.check(pattern)){addPattern(patternPath.get('argument'),bindings);}}function addTypePattern(patternPath,types){var pattern=patternPath.value;namedTypes.Pattern.assert(pattern);if(namedTypes.Identifier.check(pattern)){if(hasOwn.call(types,pattern.name)){types[pattern.name].push(patternPath);}else{types[pattern.name]=[patternPath];}}}Sp.lookup=function(name){for(var scope=this;scope;scope=scope.parent)if(scope.declares(name))break;return scope;};Sp.lookupType=function(name){for(var scope=this;scope;scope=scope.parent)if(scope.declaresType(name))break;return scope;};Sp.getGlobalScope=function(){var scope=this;while(!scope.isGlobal)scope=scope.parent;return scope;};return Scope;}exports.default=scopePlugin;module.exports=exports["default"];});unwrapExports(scope);var nodePath=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var path_1=__importDefault(path);var scope_1=__importDefault(scope);function nodePathPlugin(fork){var types=fork.use(types_1.default);var n=types.namedTypes;var b=types.builders;var isNumber=types.builtInTypes.number;var isArray=types.builtInTypes.array;var Path=fork.use(path_1.default);var Scope=fork.use(scope_1.default);var NodePath=function NodePath(value,parentPath,name){if(!(this instanceof NodePath)){throw new Error("NodePath constructor cannot be invoked without 'new'");}Path.call(this,value,parentPath,name);};var NPp=NodePath.prototype=Object.create(Path.prototype,{constructor:{value:NodePath,enumerable:false,writable:true,configurable:true}});Object.defineProperties(NPp,{node:{get:function get(){Object.defineProperty(this,"node",{configurable:true,value:this._computeNode()});return this.node;}},parent:{get:function get(){Object.defineProperty(this,"parent",{configurable:true,value:this._computeParent()});return this.parent;}},scope:{get:function get(){Object.defineProperty(this,"scope",{configurable:true,value:this._computeScope()});return this.scope;}}});NPp.replace=function(){delete this.node;delete this.parent;delete this.scope;return Path.prototype.replace.apply(this,arguments);};NPp.prune=function(){var remainingNodePath=this.parent;this.replace();return cleanUpNodesAfterPrune(remainingNodePath);};// The value of the first ancestor Path whose value is a Node.
  NPp._computeNode=function(){var value=this.value;if(n.Node.check(value)){return value;}var pp=this.parentPath;return pp&&pp.node||null;};// The first ancestor Path whose value is a Node distinct from this.node.
  NPp._computeParent=function(){var value=this.value;var pp=this.parentPath;if(!n.Node.check(value)){while(pp&&!n.Node.check(pp.value)){pp=pp.parentPath;}if(pp){pp=pp.parentPath;}}while(pp&&!n.Node.check(pp.value)){pp=pp.parentPath;}return pp||null;};// The closest enclosing scope that governs this node.
  NPp._computeScope=function(){var value=this.value;var pp=this.parentPath;var scope=pp&&pp.scope;if(n.Node.check(value)&&Scope.isEstablishedBy(value)){scope=new Scope(this,scope);}return scope||null;};NPp.getValueProperty=function(name){return types.getFieldValue(this.value,name);};/**
  	     * Determine whether this.node needs to be wrapped in parentheses in order
  	     * for a parser to reproduce the same local AST structure.
  	     *
  	     * For instance, in the expression `(1 + 2) * 3`, the BinaryExpression
  	     * whose operator is "+" needs parentheses, because `1 + 2 * 3` would
  	     * parse differently.
  	     *
  	     * If assumeExpressionContext === true, we don't worry about edge cases
  	     * like an anonymous FunctionExpression appearing lexically first in its
  	     * enclosing statement and thus needing parentheses to avoid being parsed
  	     * as a FunctionDeclaration with a missing name.
  	     */NPp.needsParens=function(assumeExpressionContext){var pp=this.parentPath;if(!pp){return false;}var node=this.value;// Only expressions need parentheses.
  if(!n.Expression.check(node)){return false;}// Identifiers never need parentheses.
  if(node.type==="Identifier"){return false;}while(!n.Node.check(pp.value)){pp=pp.parentPath;if(!pp){return false;}}var parent=pp.value;switch(node.type){case"UnaryExpression":case"SpreadElement":case"SpreadProperty":return parent.type==="MemberExpression"&&this.name==="object"&&parent.object===node;case"BinaryExpression":case"LogicalExpression":switch(parent.type){case"CallExpression":return this.name==="callee"&&parent.callee===node;case"UnaryExpression":case"SpreadElement":case"SpreadProperty":return true;case"MemberExpression":return this.name==="object"&&parent.object===node;case"BinaryExpression":case"LogicalExpression":{var n_1=node;var po=parent.operator;var pp_1=PRECEDENCE[po];var no=n_1.operator;var np=PRECEDENCE[no];if(pp_1>np){return true;}if(pp_1===np&&this.name==="right"){if(parent.right!==n_1){throw new Error("Nodes must be equal");}return true;}}default:return false;}case"SequenceExpression":switch(parent.type){case"ForStatement":// Although parentheses wouldn't hurt around sequence
  // expressions in the head of for loops, traditional style
  // dictates that e.g. i++, j++ should not be wrapped with
  // parentheses.
  return false;case"ExpressionStatement":return this.name!=="expression";default:// Otherwise err on the side of overparenthesization, adding
  // explicit exceptions above if this proves overzealous.
  return true;}case"YieldExpression":switch(parent.type){case"BinaryExpression":case"LogicalExpression":case"UnaryExpression":case"SpreadElement":case"SpreadProperty":case"CallExpression":case"MemberExpression":case"NewExpression":case"ConditionalExpression":case"YieldExpression":return true;default:return false;}case"Literal":return parent.type==="MemberExpression"&&isNumber.check(node.value)&&this.name==="object"&&parent.object===node;case"AssignmentExpression":case"ConditionalExpression":switch(parent.type){case"UnaryExpression":case"SpreadElement":case"SpreadProperty":case"BinaryExpression":case"LogicalExpression":return true;case"CallExpression":return this.name==="callee"&&parent.callee===node;case"ConditionalExpression":return this.name==="test"&&parent.test===node;case"MemberExpression":return this.name==="object"&&parent.object===node;default:return false;}default:if(parent.type==="NewExpression"&&this.name==="callee"&&parent.callee===node){return containsCallExpression(node);}}if(assumeExpressionContext!==true&&!this.canBeFirstInStatement()&&this.firstInStatement())return true;return false;};function isBinary(node){return n.BinaryExpression.check(node)||n.LogicalExpression.check(node);}var PRECEDENCE={};[["||"],["&&"],["|"],["^"],["&"],["==","===","!=","!=="],["<",">","<=",">=","in","instanceof"],[">>","<<",">>>"],["+","-"],["*","/","%"]].forEach(function(tier,i){tier.forEach(function(op){PRECEDENCE[op]=i;});});function containsCallExpression(node){if(n.CallExpression.check(node)){return true;}if(isArray.check(node)){return node.some(containsCallExpression);}if(n.Node.check(node)){return types.someField(node,function(_name,child){return containsCallExpression(child);});}return false;}NPp.canBeFirstInStatement=function(){var node=this.node;return !n.FunctionExpression.check(node)&&!n.ObjectExpression.check(node);};NPp.firstInStatement=function(){return firstInStatement(this);};function firstInStatement(path){for(var node,parent;path.parent;path=path.parent){node=path.node;parent=path.parent.node;if(n.BlockStatement.check(parent)&&path.parent.name==="body"&&path.name===0){if(parent.body[0]!==node){throw new Error("Nodes must be equal");}return true;}if(n.ExpressionStatement.check(parent)&&path.name==="expression"){if(parent.expression!==node){throw new Error("Nodes must be equal");}return true;}if(n.SequenceExpression.check(parent)&&path.parent.name==="expressions"&&path.name===0){if(parent.expressions[0]!==node){throw new Error("Nodes must be equal");}continue;}if(n.CallExpression.check(parent)&&path.name==="callee"){if(parent.callee!==node){throw new Error("Nodes must be equal");}continue;}if(n.MemberExpression.check(parent)&&path.name==="object"){if(parent.object!==node){throw new Error("Nodes must be equal");}continue;}if(n.ConditionalExpression.check(parent)&&path.name==="test"){if(parent.test!==node){throw new Error("Nodes must be equal");}continue;}if(isBinary(parent)&&path.name==="left"){if(parent.left!==node){throw new Error("Nodes must be equal");}continue;}if(n.UnaryExpression.check(parent)&&!parent.prefix&&path.name==="argument"){if(parent.argument!==node){throw new Error("Nodes must be equal");}continue;}return false;}return true;}/**
  	     * Pruning certain nodes will result in empty or incomplete nodes, here we clean those nodes up.
  	     */function cleanUpNodesAfterPrune(remainingNodePath){if(n.VariableDeclaration.check(remainingNodePath.node)){var declarations=remainingNodePath.get('declarations').value;if(!declarations||declarations.length===0){return remainingNodePath.prune();}}else if(n.ExpressionStatement.check(remainingNodePath.node)){if(!remainingNodePath.get('expression').value){return remainingNodePath.prune();}}else if(n.IfStatement.check(remainingNodePath.node)){cleanUpIfStatementAfterPrune(remainingNodePath);}return remainingNodePath;}function cleanUpIfStatementAfterPrune(ifStatement){var testExpression=ifStatement.get('test').value;var alternate=ifStatement.get('alternate').value;var consequent=ifStatement.get('consequent').value;if(!consequent&&!alternate){var testExpressionStatement=b.expressionStatement(testExpression);ifStatement.replace(testExpressionStatement);}else if(!consequent&&alternate){var negatedTestExpression=b.unaryExpression('!',testExpression,true);if(n.UnaryExpression.check(testExpression)&&testExpression.operator==='!'){negatedTestExpression=testExpression.argument;}ifStatement.get("test").replace(negatedTestExpression);ifStatement.get("consequent").replace(alternate);ifStatement.get("alternate").replace();}}return NodePath;}exports.default=nodePathPlugin;module.exports=exports["default"];});unwrapExports(nodePath);var pathVisitor=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var node_path_1=__importDefault(nodePath);var hasOwn=Object.prototype.hasOwnProperty;function pathVisitorPlugin(fork){var types=fork.use(types_1.default);var NodePath=fork.use(node_path_1.default);var isArray=types.builtInTypes.array;var isObject=types.builtInTypes.object;var isFunction=types.builtInTypes.function;var undefined$1;var PathVisitor=function PathVisitor(){if(!(this instanceof PathVisitor)){throw new Error("PathVisitor constructor cannot be invoked without 'new'");}// Permanent state.
  this._reusableContextStack=[];this._methodNameTable=computeMethodNameTable(this);this._shouldVisitComments=hasOwn.call(this._methodNameTable,"Block")||hasOwn.call(this._methodNameTable,"Line");this.Context=makeContextConstructor(this);// State reset every time PathVisitor.prototype.visit is called.
  this._visiting=false;this._changeReported=false;};function computeMethodNameTable(visitor){var typeNames=Object.create(null);for(var methodName in visitor){if(/^visit[A-Z]/.test(methodName)){typeNames[methodName.slice("visit".length)]=true;}}var supertypeTable=types.computeSupertypeLookupTable(typeNames);var methodNameTable=Object.create(null);var typeNameKeys=Object.keys(supertypeTable);var typeNameCount=typeNameKeys.length;for(var i=0;i<typeNameCount;++i){var typeName=typeNameKeys[i];methodName="visit"+supertypeTable[typeName];if(isFunction.check(visitor[methodName])){methodNameTable[typeName]=methodName;}}return methodNameTable;}PathVisitor.fromMethodsObject=function fromMethodsObject(methods){if(methods instanceof PathVisitor){return methods;}if(!isObject.check(methods)){// An empty visitor?
  return new PathVisitor();}var Visitor=function Visitor(){if(!(this instanceof Visitor)){throw new Error("Visitor constructor cannot be invoked without 'new'");}PathVisitor.call(this);};var Vp=Visitor.prototype=Object.create(PVp);Vp.constructor=Visitor;extend(Vp,methods);extend(Visitor,PathVisitor);isFunction.assert(Visitor.fromMethodsObject);isFunction.assert(Visitor.visit);return new Visitor();};function extend(target,source){for(var property in source){if(hasOwn.call(source,property)){target[property]=source[property];}}return target;}PathVisitor.visit=function visit(node,methods){return PathVisitor.fromMethodsObject(methods).visit(node);};var PVp=PathVisitor.prototype;PVp.visit=function(){if(this._visiting){throw new Error("Recursively calling visitor.visit(path) resets visitor state. "+"Try this.visit(path) or this.traverse(path) instead.");}// Private state that needs to be reset before every traversal.
  this._visiting=true;this._changeReported=false;this._abortRequested=false;var argc=arguments.length;var args=new Array(argc);for(var i=0;i<argc;++i){args[i]=arguments[i];}if(!(args[0]instanceof NodePath)){args[0]=new NodePath({root:args[0]}).get("root");}// Called with the same arguments as .visit.
  this.reset.apply(this,args);var didNotThrow;try{var root=this.visitWithoutReset(args[0]);didNotThrow=true;}finally{this._visiting=false;if(!didNotThrow&&this._abortRequested){// If this.visitWithoutReset threw an exception and
  // this._abortRequested was set to true, return the root of
  // the AST instead of letting the exception propagate, so that
  // client code does not have to provide a try-catch block to
  // intercept the AbortRequest exception.  Other kinds of
  // exceptions will propagate without being intercepted and
  // rethrown by a catch block, so their stacks will accurately
  // reflect the original throwing context.
  return args[0].value;}}return root;};PVp.AbortRequest=function AbortRequest(){};PVp.abort=function(){var visitor=this;visitor._abortRequested=true;var request=new visitor.AbortRequest();// If you decide to catch this exception and stop it from propagating,
  // make sure to call its cancel method to avoid silencing other
  // exceptions that might be thrown later in the traversal.
  request.cancel=function(){visitor._abortRequested=false;};throw request;};PVp.reset=function(_path/*, additional arguments */){// Empty stub; may be reassigned or overridden by subclasses.
  };PVp.visitWithoutReset=function(path){if(this instanceof this.Context){// Since this.Context.prototype === this, there's a chance we
  // might accidentally call context.visitWithoutReset. If that
  // happens, re-invoke the method against context.visitor.
  return this.visitor.visitWithoutReset(path);}if(!(path instanceof NodePath)){throw new Error("");}var value=path.value;var methodName=value&&typeof value==="object"&&typeof value.type==="string"&&this._methodNameTable[value.type];if(methodName){var context=this.acquireContext(path);try{return context.invokeVisitorMethod(methodName);}finally{this.releaseContext(context);}}else{// If there was no visitor method to call, visit the children of
  // this node generically.
  return visitChildren(path,this);}};function visitChildren(path,visitor){if(!(path instanceof NodePath)){throw new Error("");}if(!(visitor instanceof PathVisitor)){throw new Error("");}var value=path.value;if(isArray.check(value)){path.each(visitor.visitWithoutReset,visitor);}else if(!isObject.check(value));else{var childNames=types.getFieldNames(value);// The .comments field of the Node type is hidden, so we only
  // visit it if the visitor defines visitBlock or visitLine, and
  // value.comments is defined.
  if(visitor._shouldVisitComments&&value.comments&&childNames.indexOf("comments")<0){childNames.push("comments");}var childCount=childNames.length;var childPaths=[];for(var i=0;i<childCount;++i){var childName=childNames[i];if(!hasOwn.call(value,childName)){value[childName]=types.getFieldValue(value,childName);}childPaths.push(path.get(childName));}for(var i=0;i<childCount;++i){visitor.visitWithoutReset(childPaths[i]);}}return path.value;}PVp.acquireContext=function(path){if(this._reusableContextStack.length===0){return new this.Context(path);}return this._reusableContextStack.pop().reset(path);};PVp.releaseContext=function(context){if(!(context instanceof this.Context)){throw new Error("");}this._reusableContextStack.push(context);context.currentPath=null;};PVp.reportChanged=function(){this._changeReported=true;};PVp.wasChangeReported=function(){return this._changeReported;};function makeContextConstructor(visitor){function Context(path){if(!(this instanceof Context)){throw new Error("");}if(!(this instanceof PathVisitor)){throw new Error("");}if(!(path instanceof NodePath)){throw new Error("");}Object.defineProperty(this,"visitor",{value:visitor,writable:false,enumerable:true,configurable:false});this.currentPath=path;this.needToCallTraverse=true;Object.seal(this);}if(!(visitor instanceof PathVisitor)){throw new Error("");}// Note that the visitor object is the prototype of Context.prototype,
  // so all visitor methods are inherited by context objects.
  var Cp=Context.prototype=Object.create(visitor);Cp.constructor=Context;extend(Cp,sharedContextProtoMethods);return Context;}// Every PathVisitor has a different this.Context constructor and
  // this.Context.prototype object, but those prototypes can all use the
  // same reset, invokeVisitorMethod, and traverse function objects.
  var sharedContextProtoMethods=Object.create(null);sharedContextProtoMethods.reset=function reset(path){if(!(this instanceof this.Context)){throw new Error("");}if(!(path instanceof NodePath)){throw new Error("");}this.currentPath=path;this.needToCallTraverse=true;return this;};sharedContextProtoMethods.invokeVisitorMethod=function invokeVisitorMethod(methodName){if(!(this instanceof this.Context)){throw new Error("");}if(!(this.currentPath instanceof NodePath)){throw new Error("");}var result=this.visitor[methodName].call(this,this.currentPath);if(result===false){// Visitor methods return false to indicate that they have handled
  // their own traversal needs, and we should not complain if
  // this.needToCallTraverse is still true.
  this.needToCallTraverse=false;}else if(result!==undefined$1){// Any other non-undefined value returned from the visitor method
  // is interpreted as a replacement value.
  this.currentPath=this.currentPath.replace(result)[0];if(this.needToCallTraverse){// If this.traverse still hasn't been called, visit the
  // children of the replacement node.
  this.traverse(this.currentPath);}}if(this.needToCallTraverse!==false){throw new Error("Must either call this.traverse or return false in "+methodName);}var path=this.currentPath;return path&&path.value;};sharedContextProtoMethods.traverse=function traverse(path,newVisitor){if(!(this instanceof this.Context)){throw new Error("");}if(!(path instanceof NodePath)){throw new Error("");}if(!(this.currentPath instanceof NodePath)){throw new Error("");}this.needToCallTraverse=false;return visitChildren(path,PathVisitor.fromMethodsObject(newVisitor||this.visitor));};sharedContextProtoMethods.visit=function visit(path,newVisitor){if(!(this instanceof this.Context)){throw new Error("");}if(!(path instanceof NodePath)){throw new Error("");}if(!(this.currentPath instanceof NodePath)){throw new Error("");}this.needToCallTraverse=false;return PathVisitor.fromMethodsObject(newVisitor||this.visitor).visitWithoutReset(path);};sharedContextProtoMethods.reportChanged=function reportChanged(){this.visitor.reportChanged();};sharedContextProtoMethods.abort=function abort(){this.needToCallTraverse=false;this.visitor.abort();};return PathVisitor;}exports.default=pathVisitorPlugin;module.exports=exports["default"];});unwrapExports(pathVisitor);var equiv=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);function default_1(fork){var types=fork.use(types_1.default);var getFieldNames=types.getFieldNames;var getFieldValue=types.getFieldValue;var isArray=types.builtInTypes.array;var isObject=types.builtInTypes.object;var isDate=types.builtInTypes.Date;var isRegExp=types.builtInTypes.RegExp;var hasOwn=Object.prototype.hasOwnProperty;function astNodesAreEquivalent(a,b,problemPath){if(isArray.check(problemPath)){problemPath.length=0;}else{problemPath=null;}return areEquivalent(a,b,problemPath);}astNodesAreEquivalent.assert=function(a,b){var problemPath=[];if(!astNodesAreEquivalent(a,b,problemPath)){if(problemPath.length===0){if(a!==b){throw new Error("Nodes must be equal");}}else{throw new Error("Nodes differ in the following path: "+problemPath.map(subscriptForProperty).join(""));}}};function subscriptForProperty(property){if(/[_$a-z][_$a-z0-9]*/i.test(property)){return "."+property;}return "["+JSON.stringify(property)+"]";}function areEquivalent(a,b,problemPath){if(a===b){return true;}if(isArray.check(a)){return arraysAreEquivalent(a,b,problemPath);}if(isObject.check(a)){return objectsAreEquivalent(a,b,problemPath);}if(isDate.check(a)){return isDate.check(b)&&+a===+b;}if(isRegExp.check(a)){return isRegExp.check(b)&&a.source===b.source&&a.global===b.global&&a.multiline===b.multiline&&a.ignoreCase===b.ignoreCase;}return a==b;}function arraysAreEquivalent(a,b,problemPath){isArray.assert(a);var aLength=a.length;if(!isArray.check(b)||b.length!==aLength){if(problemPath){problemPath.push("length");}return false;}for(var i=0;i<aLength;++i){if(problemPath){problemPath.push(i);}if(i in a!==i in b){return false;}if(!areEquivalent(a[i],b[i],problemPath)){return false;}if(problemPath){var problemPathTail=problemPath.pop();if(problemPathTail!==i){throw new Error(""+problemPathTail);}}}return true;}function objectsAreEquivalent(a,b,problemPath){isObject.assert(a);if(!isObject.check(b)){return false;}// Fast path for a common property of AST nodes.
  if(a.type!==b.type){if(problemPath){problemPath.push("type");}return false;}var aNames=getFieldNames(a);var aNameCount=aNames.length;var bNames=getFieldNames(b);var bNameCount=bNames.length;if(aNameCount===bNameCount){for(var i=0;i<aNameCount;++i){var name=aNames[i];var aChild=getFieldValue(a,name);var bChild=getFieldValue(b,name);if(problemPath){problemPath.push(name);}if(!areEquivalent(aChild,bChild,problemPath)){return false;}if(problemPath){var problemPathTail=problemPath.pop();if(problemPathTail!==name){throw new Error(""+problemPathTail);}}}return true;}if(!problemPath){return false;}// Since aNameCount !== bNameCount, we need to find some name that's
  // missing in aNames but present in bNames, or vice-versa.
  var seenNames=Object.create(null);for(i=0;i<aNameCount;++i){seenNames[aNames[i]]=true;}for(i=0;i<bNameCount;++i){name=bNames[i];if(!hasOwn.call(seenNames,name)){problemPath.push(name);return false;}delete seenNames[name];}for(name in seenNames){problemPath.push(name);break;}return false;}return astNodesAreEquivalent;}exports.default=default_1;module.exports=exports["default"];});unwrapExports(equiv);var fork=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var path_visitor_1=__importDefault(pathVisitor);var equiv_1=__importDefault(equiv);var path_1=__importDefault(path);var node_path_1=__importDefault(nodePath);function default_1(defs){var fork=createFork();var types=fork.use(types_1.default);defs.forEach(fork.use);types.finalize();var PathVisitor=fork.use(path_visitor_1.default);return {Type:types.Type,builtInTypes:types.builtInTypes,namedTypes:types.namedTypes,builders:types.builders,defineMethod:types.defineMethod,getFieldNames:types.getFieldNames,getFieldValue:types.getFieldValue,eachField:types.eachField,someField:types.someField,getSupertypeNames:types.getSupertypeNames,getBuilderName:types.getBuilderName,astNodesAreEquivalent:fork.use(equiv_1.default),finalize:types.finalize,Path:fork.use(path_1.default),NodePath:fork.use(node_path_1.default),PathVisitor:PathVisitor,use:fork.use,visit:PathVisitor.visit};}exports.default=default_1;function createFork(){var used=[];var usedResult=[];function use(plugin){var idx=used.indexOf(plugin);if(idx===-1){idx=used.length;used.push(plugin);usedResult[idx]=plugin(fork);}return usedResult[idx];}var fork={use:use};return fork;}module.exports=exports["default"];});unwrapExports(fork);var shared=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);function default_1(fork){var types=fork.use(types_1.default);var Type=types.Type;var builtin=types.builtInTypes;var isNumber=builtin.number;// An example of constructing a new type with arbitrary constraints from
  // an existing type.
  function geq(than){return Type.from(function(value){return isNumber.check(value)&&value>=than;},isNumber+" >= "+than);}// Default value-returning functions that may optionally be passed as a
  // third argument to Def.prototype.field.
  var defaults={// Functions were used because (among other reasons) that's the most
  // elegant way to allow for the emptyArray one always to give a new
  // array instance.
  "null":function _null(){return null;},"emptyArray":function emptyArray(){return [];},"false":function _false(){return false;},"true":function _true(){return true;},"undefined":function undefined$1(){},"use strict":function useStrict(){return "use strict";}};var naiveIsPrimitive=Type.or(builtin.string,builtin.number,builtin.boolean,builtin.null,builtin.undefined);var isPrimitive=Type.from(function(value){if(value===null)return true;var type=typeof value;if(type==="object"||type==="function"){return false;}return true;},naiveIsPrimitive.toString());return {geq:geq,defaults:defaults,isPrimitive:isPrimitive};}exports.default=default_1;module.exports=exports["default"];});unwrapExports(shared);var core=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){var types=fork.use(types_1.default);var Type=types.Type;var def=Type.def;var or=Type.or;var shared=fork.use(shared_1.default);var defaults=shared.defaults;var geq=shared.geq;// Abstract supertype of all syntactic entities that are allowed to have a
  // .loc field.
  def("Printable").field("loc",or(def("SourceLocation"),null),defaults["null"],true);def("Node").bases("Printable").field("type",String).field("comments",or([def("Comment")],null),defaults["null"],true);def("SourceLocation").field("start",def("Position")).field("end",def("Position")).field("source",or(String,null),defaults["null"]);def("Position").field("line",geq(1)).field("column",geq(0));def("File").bases("Node").build("program","name").field("program",def("Program")).field("name",or(String,null),defaults["null"]);def("Program").bases("Node").build("body").field("body",[def("Statement")]);def("Function").bases("Node").field("id",or(def("Identifier"),null),defaults["null"]).field("params",[def("Pattern")]).field("body",def("BlockStatement")).field("generator",Boolean,defaults["false"]).field("async",Boolean,defaults["false"]);def("Statement").bases("Node");// The empty .build() here means that an EmptyStatement can be constructed
  // (i.e. it's not abstract) but that it needs no arguments.
  def("EmptyStatement").bases("Statement").build();def("BlockStatement").bases("Statement").build("body").field("body",[def("Statement")]);// TODO Figure out how to silently coerce Expressions to
  // ExpressionStatements where a Statement was expected.
  def("ExpressionStatement").bases("Statement").build("expression").field("expression",def("Expression"));def("IfStatement").bases("Statement").build("test","consequent","alternate").field("test",def("Expression")).field("consequent",def("Statement")).field("alternate",or(def("Statement"),null),defaults["null"]);def("LabeledStatement").bases("Statement").build("label","body").field("label",def("Identifier")).field("body",def("Statement"));def("BreakStatement").bases("Statement").build("label").field("label",or(def("Identifier"),null),defaults["null"]);def("ContinueStatement").bases("Statement").build("label").field("label",or(def("Identifier"),null),defaults["null"]);def("WithStatement").bases("Statement").build("object","body").field("object",def("Expression")).field("body",def("Statement"));def("SwitchStatement").bases("Statement").build("discriminant","cases","lexical").field("discriminant",def("Expression")).field("cases",[def("SwitchCase")]).field("lexical",Boolean,defaults["false"]);def("ReturnStatement").bases("Statement").build("argument").field("argument",or(def("Expression"),null));def("ThrowStatement").bases("Statement").build("argument").field("argument",def("Expression"));def("TryStatement").bases("Statement").build("block","handler","finalizer").field("block",def("BlockStatement")).field("handler",or(def("CatchClause"),null),function(){return this.handlers&&this.handlers[0]||null;}).field("handlers",[def("CatchClause")],function(){return this.handler?[this.handler]:[];},true)// Indicates this field is hidden from eachField iteration.
  .field("guardedHandlers",[def("CatchClause")],defaults.emptyArray).field("finalizer",or(def("BlockStatement"),null),defaults["null"]);def("CatchClause").bases("Node").build("param","guard","body")// https://github.com/tc39/proposal-optional-catch-binding
  .field("param",or(def("Pattern"),null),defaults["null"]).field("guard",or(def("Expression"),null),defaults["null"]).field("body",def("BlockStatement"));def("WhileStatement").bases("Statement").build("test","body").field("test",def("Expression")).field("body",def("Statement"));def("DoWhileStatement").bases("Statement").build("body","test").field("body",def("Statement")).field("test",def("Expression"));def("ForStatement").bases("Statement").build("init","test","update","body").field("init",or(def("VariableDeclaration"),def("Expression"),null)).field("test",or(def("Expression"),null)).field("update",or(def("Expression"),null)).field("body",def("Statement"));def("ForInStatement").bases("Statement").build("left","right","body").field("left",or(def("VariableDeclaration"),def("Expression"))).field("right",def("Expression")).field("body",def("Statement"));def("DebuggerStatement").bases("Statement").build();def("Declaration").bases("Statement");def("FunctionDeclaration").bases("Function","Declaration").build("id","params","body").field("id",def("Identifier"));def("FunctionExpression").bases("Function","Expression").build("id","params","body");def("VariableDeclaration").bases("Declaration").build("kind","declarations").field("kind",or("var","let","const")).field("declarations",[def("VariableDeclarator")]);def("VariableDeclarator").bases("Node").build("id","init").field("id",def("Pattern")).field("init",or(def("Expression"),null),defaults["null"]);def("Expression").bases("Node");def("ThisExpression").bases("Expression").build();def("ArrayExpression").bases("Expression").build("elements").field("elements",[or(def("Expression"),null)]);def("ObjectExpression").bases("Expression").build("properties").field("properties",[def("Property")]);// TODO Not in the Mozilla Parser API, but used by Esprima.
  def("Property").bases("Node")// Want to be able to visit Property Nodes.
  .build("kind","key","value").field("kind",or("init","get","set")).field("key",or(def("Literal"),def("Identifier"))).field("value",def("Expression"));def("SequenceExpression").bases("Expression").build("expressions").field("expressions",[def("Expression")]);var UnaryOperator=or("-","+","!","~","typeof","void","delete");def("UnaryExpression").bases("Expression").build("operator","argument","prefix").field("operator",UnaryOperator).field("argument",def("Expression"))// Esprima doesn't bother with this field, presumably because it's
  // always true for unary operators.
  .field("prefix",Boolean,defaults["true"]);var BinaryOperator=or("==","!=","===","!==","<","<=",">",">=","<<",">>",">>>","+","-","*","/","%","**","&",// TODO Missing from the Parser API.
  "|","^","in","instanceof");def("BinaryExpression").bases("Expression").build("operator","left","right").field("operator",BinaryOperator).field("left",def("Expression")).field("right",def("Expression"));var AssignmentOperator=or("=","+=","-=","*=","/=","%=","<<=",">>=",">>>=","|=","^=","&=");def("AssignmentExpression").bases("Expression").build("operator","left","right").field("operator",AssignmentOperator).field("left",or(def("Pattern"),def("MemberExpression"))).field("right",def("Expression"));var UpdateOperator=or("++","--");def("UpdateExpression").bases("Expression").build("operator","argument","prefix").field("operator",UpdateOperator).field("argument",def("Expression")).field("prefix",Boolean);var LogicalOperator=or("||","&&");def("LogicalExpression").bases("Expression").build("operator","left","right").field("operator",LogicalOperator).field("left",def("Expression")).field("right",def("Expression"));def("ConditionalExpression").bases("Expression").build("test","consequent","alternate").field("test",def("Expression")).field("consequent",def("Expression")).field("alternate",def("Expression"));def("NewExpression").bases("Expression").build("callee","arguments").field("callee",def("Expression"))// The Mozilla Parser API gives this type as [or(def("Expression"),
  // null)], but null values don't really make sense at the call site.
  // TODO Report this nonsense.
  .field("arguments",[def("Expression")]);def("CallExpression").bases("Expression").build("callee","arguments").field("callee",def("Expression"))// See comment for NewExpression above.
  .field("arguments",[def("Expression")]);def("MemberExpression").bases("Expression").build("object","property","computed").field("object",def("Expression")).field("property",or(def("Identifier"),def("Expression"))).field("computed",Boolean,function(){var type=this.property.type;if(type==='Literal'||type==='MemberExpression'||type==='BinaryExpression'){return true;}return false;});def("Pattern").bases("Node");def("SwitchCase").bases("Node").build("test","consequent").field("test",or(def("Expression"),null)).field("consequent",[def("Statement")]);def("Identifier").bases("Expression","Pattern").build("name").field("name",String).field("optional",Boolean,defaults["false"]);def("Literal").bases("Expression").build("value").field("value",or(String,Boolean,null,Number,RegExp)).field("regex",or({pattern:String,flags:String},null),function(){if(this.value instanceof RegExp){var flags="";if(this.value.ignoreCase)flags+="i";if(this.value.multiline)flags+="m";if(this.value.global)flags+="g";return {pattern:this.value.source,flags:flags};}return null;});// Abstract (non-buildable) comment supertype. Not a Node.
  def("Comment").bases("Printable").field("value",String)// A .leading comment comes before the node, whereas a .trailing
  // comment comes after it. These two fields should not both be true,
  // but they might both be false when the comment falls inside a node
  // and the node has no children for the comment to lead or trail,
  // e.g. { /*dangling*/ }.
  .field("leading",Boolean,defaults["true"]).field("trailing",Boolean,defaults["false"]);}exports.default=default_1;module.exports=exports["default"];});unwrapExports(core);var es6=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var core_1=__importDefault(core);var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){fork.use(core_1.default);var types=fork.use(types_1.default);var def=types.Type.def;var or=types.Type.or;var defaults=fork.use(shared_1.default).defaults;def("Function").field("generator",Boolean,defaults["false"]).field("expression",Boolean,defaults["false"]).field("defaults",[or(def("Expression"),null)],defaults.emptyArray)// TODO This could be represented as a RestElement in .params.
  .field("rest",or(def("Identifier"),null),defaults["null"]);// The ESTree way of representing a ...rest parameter.
  def("RestElement").bases("Pattern").build("argument").field("argument",def("Pattern")).field("typeAnnotation",// for Babylon. Flow parser puts it on the identifier
  or(def("TypeAnnotation"),def("TSTypeAnnotation"),null),defaults["null"]);def("SpreadElementPattern").bases("Pattern").build("argument").field("argument",def("Pattern"));def("FunctionDeclaration").build("id","params","body","generator","expression");def("FunctionExpression").build("id","params","body","generator","expression");// The Parser API calls this ArrowExpression, but Esprima and all other
  // actual parsers use ArrowFunctionExpression.
  def("ArrowFunctionExpression").bases("Function","Expression").build("params","body","expression")// The forced null value here is compatible with the overridden
  // definition of the "id" field in the Function interface.
  .field("id",null,defaults["null"])// Arrow function bodies are allowed to be expressions.
  .field("body",or(def("BlockStatement"),def("Expression")))// The current spec forbids arrow generators, so I have taken the
  // liberty of enforcing that. TODO Report this.
  .field("generator",false,defaults["false"]);def("ForOfStatement").bases("Statement").build("left","right","body").field("left",or(def("VariableDeclaration"),def("Pattern"))).field("right",def("Expression")).field("body",def("Statement"));def("YieldExpression").bases("Expression").build("argument","delegate").field("argument",or(def("Expression"),null)).field("delegate",Boolean,defaults["false"]);def("GeneratorExpression").bases("Expression").build("body","blocks","filter").field("body",def("Expression")).field("blocks",[def("ComprehensionBlock")]).field("filter",or(def("Expression"),null));def("ComprehensionExpression").bases("Expression").build("body","blocks","filter").field("body",def("Expression")).field("blocks",[def("ComprehensionBlock")]).field("filter",or(def("Expression"),null));def("ComprehensionBlock").bases("Node").build("left","right","each").field("left",def("Pattern")).field("right",def("Expression")).field("each",Boolean);def("Property").field("key",or(def("Literal"),def("Identifier"),def("Expression"))).field("value",or(def("Expression"),def("Pattern"))).field("method",Boolean,defaults["false"]).field("shorthand",Boolean,defaults["false"]).field("computed",Boolean,defaults["false"]);def("ObjectProperty").field("shorthand",Boolean,defaults["false"]);def("PropertyPattern").bases("Pattern").build("key","pattern").field("key",or(def("Literal"),def("Identifier"),def("Expression"))).field("pattern",def("Pattern")).field("computed",Boolean,defaults["false"]);def("ObjectPattern").bases("Pattern").build("properties").field("properties",[or(def("PropertyPattern"),def("Property"))]);def("ArrayPattern").bases("Pattern").build("elements").field("elements",[or(def("Pattern"),null)]);def("MethodDefinition").bases("Declaration").build("kind","key","value","static").field("kind",or("constructor","method","get","set")).field("key",def("Expression")).field("value",def("Function")).field("computed",Boolean,defaults["false"]).field("static",Boolean,defaults["false"]);def("SpreadElement").bases("Node").build("argument").field("argument",def("Expression"));def("ArrayExpression").field("elements",[or(def("Expression"),def("SpreadElement"),def("RestElement"),null)]);def("NewExpression").field("arguments",[or(def("Expression"),def("SpreadElement"))]);def("CallExpression").field("arguments",[or(def("Expression"),def("SpreadElement"))]);// Note: this node type is *not* an AssignmentExpression with a Pattern on
  // the left-hand side! The existing AssignmentExpression type already
  // supports destructuring assignments. AssignmentPattern nodes may appear
  // wherever a Pattern is allowed, and the right-hand side represents a
  // default value to be destructured against the left-hand side, if no
  // value is otherwise provided. For example: default parameter values.
  def("AssignmentPattern").bases("Pattern").build("left","right").field("left",def("Pattern")).field("right",def("Expression"));var ClassBodyElement=or(def("MethodDefinition"),def("VariableDeclarator"),def("ClassPropertyDefinition"),def("ClassProperty"));def("ClassProperty").bases("Declaration").build("key").field("key",or(def("Literal"),def("Identifier"),def("Expression"))).field("computed",Boolean,defaults["false"]);def("ClassPropertyDefinition")// static property
  .bases("Declaration").build("definition")// Yes, Virginia, circular definitions are permitted.
  .field("definition",ClassBodyElement);def("ClassBody").bases("Declaration").build("body").field("body",[ClassBodyElement]);def("ClassDeclaration").bases("Declaration").build("id","body","superClass").field("id",or(def("Identifier"),null)).field("body",def("ClassBody")).field("superClass",or(def("Expression"),null),defaults["null"]);def("ClassExpression").bases("Expression").build("id","body","superClass").field("id",or(def("Identifier"),null),defaults["null"]).field("body",def("ClassBody")).field("superClass",or(def("Expression"),null),defaults["null"]);// Specifier and ModuleSpecifier are abstract non-standard types
  // introduced for definitional convenience.
  def("Specifier").bases("Node");// This supertype is shared/abused by both def/babel.js and
  // def/esprima.js. In the future, it will be possible to load only one set
  // of definitions appropriate for a given parser, but until then we must
  // rely on default functions to reconcile the conflicting AST formats.
  def("ModuleSpecifier").bases("Specifier")// This local field is used by Babel/Acorn. It should not technically
  // be optional in the Babel/Acorn AST format, but it must be optional
  // in the Esprima AST format.
  .field("local",or(def("Identifier"),null),defaults["null"])// The id and name fields are used by Esprima. The id field should not
  // technically be optional in the Esprima AST format, but it must be
  // optional in the Babel/Acorn AST format.
  .field("id",or(def("Identifier"),null),defaults["null"]).field("name",or(def("Identifier"),null),defaults["null"]);// Like ModuleSpecifier, except type:"ImportSpecifier" and buildable.
  // import {<id [as name]>} from ...;
  def("ImportSpecifier").bases("ModuleSpecifier").build("id","name");// import <* as id> from ...;
  def("ImportNamespaceSpecifier").bases("ModuleSpecifier").build("id");// import <id> from ...;
  def("ImportDefaultSpecifier").bases("ModuleSpecifier").build("id");def("ImportDeclaration").bases("Declaration").build("specifiers","source","importKind").field("specifiers",[or(def("ImportSpecifier"),def("ImportNamespaceSpecifier"),def("ImportDefaultSpecifier"))],defaults.emptyArray).field("source",def("Literal")).field("importKind",or("value","type"),function(){return "value";});def("TaggedTemplateExpression").bases("Expression").build("tag","quasi").field("tag",def("Expression")).field("quasi",def("TemplateLiteral"));def("TemplateLiteral").bases("Expression").build("quasis","expressions").field("quasis",[def("TemplateElement")]).field("expressions",[def("Expression")]);def("TemplateElement").bases("Node").build("value","tail").field("value",{"cooked":String,"raw":String}).field("tail",Boolean);}exports.default=default_1;module.exports=exports["default"];});unwrapExports(es6);var es7=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var es6_1=__importDefault(es6);var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){fork.use(es6_1.default);var types=fork.use(types_1.default);var def=types.Type.def;var or=types.Type.or;var defaults=fork.use(shared_1.default).defaults;def("Function").field("async",Boolean,defaults["false"]);def("SpreadProperty").bases("Node").build("argument").field("argument",def("Expression"));def("ObjectExpression").field("properties",[or(def("Property"),def("SpreadProperty"),def("SpreadElement"))]);def("SpreadPropertyPattern").bases("Pattern").build("argument").field("argument",def("Pattern"));def("ObjectPattern").field("properties",[or(def("Property"),def("PropertyPattern"),def("SpreadPropertyPattern"))]);def("AwaitExpression").bases("Expression").build("argument","all").field("argument",or(def("Expression"),null)).field("all",Boolean,defaults["false"]);}exports.default=default_1;module.exports=exports["default"];});unwrapExports(es7);var jsx=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var es7_1=__importDefault(es7);var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){fork.use(es7_1.default);var types=fork.use(types_1.default);var def=types.Type.def;var or=types.Type.or;var defaults=fork.use(shared_1.default).defaults;def("JSXAttribute").bases("Node").build("name","value").field("name",or(def("JSXIdentifier"),def("JSXNamespacedName"))).field("value",or(def("Literal"),// attr="value"
  def("JSXExpressionContainer"),// attr={value}
  null// attr= or just attr
  ),defaults["null"]);def("JSXIdentifier").bases("Identifier").build("name").field("name",String);def("JSXNamespacedName").bases("Node").build("namespace","name").field("namespace",def("JSXIdentifier")).field("name",def("JSXIdentifier"));def("JSXMemberExpression").bases("MemberExpression").build("object","property").field("object",or(def("JSXIdentifier"),def("JSXMemberExpression"))).field("property",def("JSXIdentifier")).field("computed",Boolean,defaults.false);var JSXElementName=or(def("JSXIdentifier"),def("JSXNamespacedName"),def("JSXMemberExpression"));def("JSXSpreadAttribute").bases("Node").build("argument").field("argument",def("Expression"));var JSXAttributes=[or(def("JSXAttribute"),def("JSXSpreadAttribute"))];def("JSXExpressionContainer").bases("Expression").build("expression").field("expression",def("Expression"));def("JSXElement").bases("Expression").build("openingElement","closingElement","children").field("openingElement",def("JSXOpeningElement")).field("closingElement",or(def("JSXClosingElement"),null),defaults["null"]).field("children",[or(def("JSXElement"),def("JSXExpressionContainer"),def("JSXFragment"),def("JSXText"),def("Literal")// TODO Esprima should return JSXText instead.
  )],defaults.emptyArray).field("name",JSXElementName,function(){// Little-known fact: the `this` object inside a default function
  // is none other than the partially-built object itself, and any
  // fields initialized directly from builder function arguments
  // (like openingElement, closingElement, and children) are
  // guaranteed to be available.
  return this.openingElement.name;},true)// hidden from traversal
  .field("selfClosing",Boolean,function(){return this.openingElement.selfClosing;},true)// hidden from traversal
  .field("attributes",JSXAttributes,function(){return this.openingElement.attributes;},true);// hidden from traversal
  def("JSXOpeningElement").bases("Node")// TODO Does this make sense? Can't really be an JSXElement.
  .build("name","attributes","selfClosing").field("name",JSXElementName).field("attributes",JSXAttributes,defaults.emptyArray).field("selfClosing",Boolean,defaults["false"]);def("JSXClosingElement").bases("Node")// TODO Same concern.
  .build("name").field("name",JSXElementName);def("JSXFragment").bases("Expression").build("openingElement","closingElement","children").field("openingElement",def("JSXOpeningFragment")).field("closingElement",def("JSXClosingFragment")).field("children",[or(def("JSXElement"),def("JSXExpressionContainer"),def("JSXFragment"),def("JSXText"),def("Literal")// TODO Esprima should return JSXText instead.
  )],defaults.emptyArray);def("JSXOpeningFragment").bases("Node")// TODO Same concern.
  .build();def("JSXClosingFragment").bases("Node")// TODO Same concern.
  .build();def("JSXText").bases("Literal").build("value").field("value",String);def("JSXEmptyExpression").bases("Expression").build();// This PR has caused many people issues, but supporting it seems like a
  // good idea anyway: https://github.com/babel/babel/pull/4988
  def("JSXSpreadChild").bases("Expression").build("expression").field("expression",def("Expression"));}exports.default=default_1;module.exports=exports["default"];});unwrapExports(jsx);var typeAnnotations=createCommonjsModule(function(module,exports){/**
  	 * Type annotation defs shared between Flow and TypeScript.
  	 * These defs could not be defined in ./flow.ts or ./typescript.ts directly
  	 * because they use the same name.
  	 */var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){var types=fork.use(types_1.default);var def=types.Type.def;var or=types.Type.or;var defaults=fork.use(shared_1.default).defaults;var TypeAnnotation=or(def("TypeAnnotation"),def("TSTypeAnnotation"),null);var TypeParamDecl=or(def("TypeParameterDeclaration"),def("TSTypeParameterDeclaration"),null);def("Identifier").field("typeAnnotation",TypeAnnotation,defaults["null"]);def("ObjectPattern").field("typeAnnotation",TypeAnnotation,defaults["null"]);def("Function").field("returnType",TypeAnnotation,defaults["null"]).field("typeParameters",TypeParamDecl,defaults["null"]);def("ClassProperty").build("key","value","typeAnnotation","static").field("value",or(def("Expression"),null)).field("static",Boolean,defaults["false"]).field("typeAnnotation",TypeAnnotation,defaults["null"]);["ClassDeclaration","ClassExpression"].forEach(function(typeName){def(typeName).field("typeParameters",TypeParamDecl,defaults["null"]).field("superTypeParameters",or(def("TypeParameterInstantiation"),def("TSTypeParameterInstantiation"),null),defaults["null"]).field("implements",or([def("ClassImplements")],[def("TSExpressionWithTypeArguments")]),defaults.emptyArray);});}exports.default=default_1;module.exports=exports["default"];});unwrapExports(typeAnnotations);var flow=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var es7_1=__importDefault(es7);var type_annotations_1=__importDefault(typeAnnotations);var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){fork.use(es7_1.default);fork.use(type_annotations_1.default);var types=fork.use(types_1.default);var def=types.Type.def;var or=types.Type.or;var defaults=fork.use(shared_1.default).defaults;// Base types
  def("Flow").bases("Node");def("FlowType").bases("Flow");// Type annotations
  def("AnyTypeAnnotation").bases("FlowType").build();def("EmptyTypeAnnotation").bases("FlowType").build();def("MixedTypeAnnotation").bases("FlowType").build();def("VoidTypeAnnotation").bases("FlowType").build();def("NumberTypeAnnotation").bases("FlowType").build();def("NumberLiteralTypeAnnotation").bases("FlowType").build("value","raw").field("value",Number).field("raw",String);// Babylon 6 differs in AST from Flow
  // same as NumberLiteralTypeAnnotation
  def("NumericLiteralTypeAnnotation").bases("FlowType").build("value","raw").field("value",Number).field("raw",String);def("StringTypeAnnotation").bases("FlowType").build();def("StringLiteralTypeAnnotation").bases("FlowType").build("value","raw").field("value",String).field("raw",String);def("BooleanTypeAnnotation").bases("FlowType").build();def("BooleanLiteralTypeAnnotation").bases("FlowType").build("value","raw").field("value",Boolean).field("raw",String);def("TypeAnnotation").bases("Node").build("typeAnnotation").field("typeAnnotation",def("FlowType"));def("NullableTypeAnnotation").bases("FlowType").build("typeAnnotation").field("typeAnnotation",def("FlowType"));def("NullLiteralTypeAnnotation").bases("FlowType").build();def("NullTypeAnnotation").bases("FlowType").build();def("ThisTypeAnnotation").bases("FlowType").build();def("ExistsTypeAnnotation").bases("FlowType").build();def("ExistentialTypeParam").bases("FlowType").build();def("FunctionTypeAnnotation").bases("FlowType").build("params","returnType","rest","typeParameters").field("params",[def("FunctionTypeParam")]).field("returnType",def("FlowType")).field("rest",or(def("FunctionTypeParam"),null)).field("typeParameters",or(def("TypeParameterDeclaration"),null));def("FunctionTypeParam").bases("Node").build("name","typeAnnotation","optional").field("name",def("Identifier")).field("typeAnnotation",def("FlowType")).field("optional",Boolean);def("ArrayTypeAnnotation").bases("FlowType").build("elementType").field("elementType",def("FlowType"));def("ObjectTypeAnnotation").bases("FlowType").build("properties","indexers","callProperties").field("properties",[or(def("ObjectTypeProperty"),def("ObjectTypeSpreadProperty"))]).field("indexers",[def("ObjectTypeIndexer")],defaults.emptyArray).field("callProperties",[def("ObjectTypeCallProperty")],defaults.emptyArray).field("inexact",or(Boolean,void 0),defaults["undefined"]).field("exact",Boolean,defaults["false"]).field("internalSlots",[def("ObjectTypeInternalSlot")],defaults.emptyArray);def("Variance").bases("Node").build("kind").field("kind",or("plus","minus"));var LegacyVariance=or(def("Variance"),"plus","minus",null);def("ObjectTypeProperty").bases("Node").build("key","value","optional").field("key",or(def("Literal"),def("Identifier"))).field("value",def("FlowType")).field("optional",Boolean).field("variance",LegacyVariance,defaults["null"]);def("ObjectTypeIndexer").bases("Node").build("id","key","value").field("id",def("Identifier")).field("key",def("FlowType")).field("value",def("FlowType")).field("variance",LegacyVariance,defaults["null"]);def("ObjectTypeCallProperty").bases("Node").build("value").field("value",def("FunctionTypeAnnotation")).field("static",Boolean,defaults["false"]);def("QualifiedTypeIdentifier").bases("Node").build("qualification","id").field("qualification",or(def("Identifier"),def("QualifiedTypeIdentifier"))).field("id",def("Identifier"));def("GenericTypeAnnotation").bases("FlowType").build("id","typeParameters").field("id",or(def("Identifier"),def("QualifiedTypeIdentifier"))).field("typeParameters",or(def("TypeParameterInstantiation"),null));def("MemberTypeAnnotation").bases("FlowType").build("object","property").field("object",def("Identifier")).field("property",or(def("MemberTypeAnnotation"),def("GenericTypeAnnotation")));def("UnionTypeAnnotation").bases("FlowType").build("types").field("types",[def("FlowType")]);def("IntersectionTypeAnnotation").bases("FlowType").build("types").field("types",[def("FlowType")]);def("TypeofTypeAnnotation").bases("FlowType").build("argument").field("argument",def("FlowType"));def("ObjectTypeSpreadProperty").bases("Node").build("argument").field("argument",def("FlowType"));def("ObjectTypeInternalSlot").bases("Node").build("id","value","optional","static","method").field("id",def("Identifier")).field("value",def("FlowType")).field("optional",Boolean).field("static",Boolean).field("method",Boolean);def("TypeParameterDeclaration").bases("Node").build("params").field("params",[def("TypeParameter")]);def("TypeParameterInstantiation").bases("Node").build("params").field("params",[def("FlowType")]);def("TypeParameter").bases("FlowType").build("name","variance","bound").field("name",String).field("variance",LegacyVariance,defaults["null"]).field("bound",or(def("TypeAnnotation"),null),defaults["null"]);def("ClassProperty").field("variance",LegacyVariance,defaults["null"]);def("ClassImplements").bases("Node").build("id").field("id",def("Identifier")).field("superClass",or(def("Expression"),null),defaults["null"]).field("typeParameters",or(def("TypeParameterInstantiation"),null),defaults["null"]);def("InterfaceTypeAnnotation").bases("FlowType").build("body","extends").field("body",def("ObjectTypeAnnotation")).field("extends",or([def("InterfaceExtends")],null),defaults["null"]);def("InterfaceDeclaration").bases("Declaration").build("id","body","extends").field("id",def("Identifier")).field("typeParameters",or(def("TypeParameterDeclaration"),null),defaults["null"]).field("body",def("ObjectTypeAnnotation")).field("extends",[def("InterfaceExtends")]);def("DeclareInterface").bases("InterfaceDeclaration").build("id","body","extends");def("InterfaceExtends").bases("Node").build("id").field("id",def("Identifier")).field("typeParameters",or(def("TypeParameterInstantiation"),null),defaults["null"]);def("TypeAlias").bases("Declaration").build("id","typeParameters","right").field("id",def("Identifier")).field("typeParameters",or(def("TypeParameterDeclaration"),null)).field("right",def("FlowType"));def("OpaqueType").bases("Declaration").build("id","typeParameters","impltype","supertype").field("id",def("Identifier")).field("typeParameters",or(def("TypeParameterDeclaration"),null)).field("impltype",def("FlowType")).field("supertype",def("FlowType"));def("DeclareTypeAlias").bases("TypeAlias").build("id","typeParameters","right");def("DeclareOpaqueType").bases("TypeAlias").build("id","typeParameters","supertype");def("TypeCastExpression").bases("Expression").build("expression","typeAnnotation").field("expression",def("Expression")).field("typeAnnotation",def("TypeAnnotation"));def("TupleTypeAnnotation").bases("FlowType").build("types").field("types",[def("FlowType")]);def("DeclareVariable").bases("Statement").build("id").field("id",def("Identifier"));def("DeclareFunction").bases("Statement").build("id").field("id",def("Identifier"));def("DeclareClass").bases("InterfaceDeclaration").build("id");def("DeclareModule").bases("Statement").build("id","body").field("id",or(def("Identifier"),def("Literal"))).field("body",def("BlockStatement"));def("DeclareModuleExports").bases("Statement").build("typeAnnotation").field("typeAnnotation",def("TypeAnnotation"));def("DeclareExportDeclaration").bases("Declaration").build("default","declaration","specifiers","source").field("default",Boolean).field("declaration",or(def("DeclareVariable"),def("DeclareFunction"),def("DeclareClass"),def("FlowType"),// Implies default.
  null)).field("specifiers",[or(def("ExportSpecifier"),def("ExportBatchSpecifier"))],defaults.emptyArray).field("source",or(def("Literal"),null),defaults["null"]);def("DeclareExportAllDeclaration").bases("Declaration").build("source").field("source",or(def("Literal"),null),defaults["null"]);def("FlowPredicate").bases("Flow");def("InferredPredicate").bases("FlowPredicate").build();def("DeclaredPredicate").bases("FlowPredicate").build("value").field("value",def("Expression"));}exports.default=default_1;module.exports=exports["default"];});unwrapExports(flow);var esprima=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var es7_1=__importDefault(es7);var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){fork.use(es7_1.default);var types=fork.use(types_1.default);var defaults=fork.use(shared_1.default).defaults;var def=types.Type.def;var or=types.Type.or;def("VariableDeclaration").field("declarations",[or(def("VariableDeclarator"),def("Identifier")// Esprima deviation.
  )]);def("Property").field("value",or(def("Expression"),def("Pattern")// Esprima deviation.
  ));def("ArrayPattern").field("elements",[or(def("Pattern"),def("SpreadElement"),null)]);def("ObjectPattern").field("properties",[or(def("Property"),def("PropertyPattern"),def("SpreadPropertyPattern"),def("SpreadProperty")// Used by Esprima.
  )]);// Like ModuleSpecifier, except type:"ExportSpecifier" and buildable.
  // export {<id [as name]>} [from ...];
  def("ExportSpecifier").bases("ModuleSpecifier").build("id","name");// export <*> from ...;
  def("ExportBatchSpecifier").bases("Specifier").build();def("ExportDeclaration").bases("Declaration").build("default","declaration","specifiers","source").field("default",Boolean).field("declaration",or(def("Declaration"),def("Expression"),// Implies default.
  null)).field("specifiers",[or(def("ExportSpecifier"),def("ExportBatchSpecifier"))],defaults.emptyArray).field("source",or(def("Literal"),null),defaults["null"]);def("Block").bases("Comment").build("value",/*optional:*/"leading","trailing");def("Line").bases("Comment").build("value",/*optional:*/"leading","trailing");}exports.default=default_1;module.exports=exports["default"];});unwrapExports(esprima);var babelCore=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var shared_1=__importDefault(shared);var es7_1=__importDefault(es7);function default_1(fork){fork.use(es7_1.default);var types=fork.use(types_1.default);var defaults=fork.use(shared_1.default).defaults;var def=types.Type.def;var or=types.Type.or;def("Noop").bases("Statement").build();def("DoExpression").bases("Expression").build("body").field("body",[def("Statement")]);def("Super").bases("Expression").build();def("BindExpression").bases("Expression").build("object","callee").field("object",or(def("Expression"),null)).field("callee",def("Expression"));def("Decorator").bases("Node").build("expression").field("expression",def("Expression"));def("Property").field("decorators",or([def("Decorator")],null),defaults["null"]);def("MethodDefinition").field("decorators",or([def("Decorator")],null),defaults["null"]);def("MetaProperty").bases("Expression").build("meta","property").field("meta",def("Identifier")).field("property",def("Identifier"));def("ParenthesizedExpression").bases("Expression").build("expression").field("expression",def("Expression"));def("ImportSpecifier").bases("ModuleSpecifier").build("imported","local").field("imported",def("Identifier"));def("ImportDefaultSpecifier").bases("ModuleSpecifier").build("local");def("ImportNamespaceSpecifier").bases("ModuleSpecifier").build("local");def("ExportDefaultDeclaration").bases("Declaration").build("declaration").field("declaration",or(def("Declaration"),def("Expression")));def("ExportNamedDeclaration").bases("Declaration").build("declaration","specifiers","source").field("declaration",or(def("Declaration"),null)).field("specifiers",[def("ExportSpecifier")],defaults.emptyArray).field("source",or(def("Literal"),null),defaults["null"]);def("ExportSpecifier").bases("ModuleSpecifier").build("local","exported").field("exported",def("Identifier"));def("ExportNamespaceSpecifier").bases("Specifier").build("exported").field("exported",def("Identifier"));def("ExportDefaultSpecifier").bases("Specifier").build("exported").field("exported",def("Identifier"));def("ExportAllDeclaration").bases("Declaration").build("exported","source").field("exported",or(def("Identifier"),null)).field("source",def("Literal"));def("CommentBlock").bases("Comment").build("value",/*optional:*/"leading","trailing");def("CommentLine").bases("Comment").build("value",/*optional:*/"leading","trailing");def("Directive").bases("Node").build("value").field("value",def("DirectiveLiteral"));def("DirectiveLiteral").bases("Node","Expression").build("value").field("value",String,defaults["use strict"]);def("InterpreterDirective").bases("Node").build("value").field("value",String);def("BlockStatement").bases("Statement").build("body").field("body",[def("Statement")]).field("directives",[def("Directive")],defaults.emptyArray);def("Program").bases("Node").build("body").field("body",[def("Statement")]).field("directives",[def("Directive")],defaults.emptyArray).field("interpreter",or(def("InterpreterDirective"),null),defaults["null"]);// Split Literal
  def("StringLiteral").bases("Literal").build("value").field("value",String);def("NumericLiteral").bases("Literal").build("value").field("value",Number).field("raw",or(String,null),defaults["null"]).field("extra",{rawValue:Number,raw:String},function getDefault(){return {rawValue:this.value,raw:this.value+""};});def("BigIntLiteral").bases("Literal").build("value")// Only String really seems appropriate here, since BigInt values
  // often exceed the limits of JS numbers.
  .field("value",or(String,Number)).field("extra",{rawValue:String,raw:String},function getDefault(){return {rawValue:String(this.value),raw:this.value+"n"};});def("NullLiteral").bases("Literal").build().field("value",null,defaults["null"]);def("BooleanLiteral").bases("Literal").build("value").field("value",Boolean);def("RegExpLiteral").bases("Literal").build("pattern","flags").field("pattern",String).field("flags",String).field("value",RegExp,function(){return new RegExp(this.pattern,this.flags);});var ObjectExpressionProperty=or(def("Property"),def("ObjectMethod"),def("ObjectProperty"),def("SpreadProperty"),def("SpreadElement"));// Split Property -> ObjectProperty and ObjectMethod
  def("ObjectExpression").bases("Expression").build("properties").field("properties",[ObjectExpressionProperty]);// ObjectMethod hoist .value properties to own properties
  def("ObjectMethod").bases("Node","Function").build("kind","key","params","body","computed").field("kind",or("method","get","set")).field("key",or(def("Literal"),def("Identifier"),def("Expression"))).field("params",[def("Pattern")]).field("body",def("BlockStatement")).field("computed",Boolean,defaults["false"]).field("generator",Boolean,defaults["false"]).field("async",Boolean,defaults["false"]).field("accessibility",// TypeScript
  or(def("Literal"),null),defaults["null"]).field("decorators",or([def("Decorator")],null),defaults["null"]);def("ObjectProperty").bases("Node").build("key","value").field("key",or(def("Literal"),def("Identifier"),def("Expression"))).field("value",or(def("Expression"),def("Pattern"))).field("accessibility",// TypeScript
  or(def("Literal"),null),defaults["null"]).field("computed",Boolean,defaults["false"]);var ClassBodyElement=or(def("MethodDefinition"),def("VariableDeclarator"),def("ClassPropertyDefinition"),def("ClassProperty"),def("ClassPrivateProperty"),def("ClassMethod"),def("ClassPrivateMethod"));// MethodDefinition -> ClassMethod
  def("ClassBody").bases("Declaration").build("body").field("body",[ClassBodyElement]);def("ClassMethod").bases("Declaration","Function").build("kind","key","params","body","computed","static").field("key",or(def("Literal"),def("Identifier"),def("Expression")));def("ClassPrivateMethod").bases("Declaration","Function").build("key","params","body","kind","computed","static").field("key",def("PrivateName"));["ClassMethod","ClassPrivateMethod"].forEach(function(typeName){def(typeName).field("kind",or("get","set","method","constructor"),function(){return "method";}).field("body",def("BlockStatement")).field("computed",Boolean,defaults["false"]).field("static",or(Boolean,null),defaults["null"]).field("abstract",or(Boolean,null),defaults["null"]).field("access",or("public","private","protected",null),defaults["null"]).field("accessibility",or("public","private","protected",null),defaults["null"]).field("decorators",or([def("Decorator")],null),defaults["null"]).field("optional",or(Boolean,null),defaults["null"]);});def("ClassPrivateProperty").bases("ClassProperty").build("key","value").field("key",def("PrivateName")).field("value",or(def("Expression"),null),defaults["null"]);def("PrivateName").bases("Expression","Pattern").build("id").field("id",def("Identifier"));var ObjectPatternProperty=or(def("Property"),def("PropertyPattern"),def("SpreadPropertyPattern"),def("SpreadProperty"),// Used by Esprima
  def("ObjectProperty"),// Babel 6
  def("RestProperty")// Babel 6
  );// Split into RestProperty and SpreadProperty
  def("ObjectPattern").bases("Pattern").build("properties").field("properties",[ObjectPatternProperty]).field("decorators",or([def("Decorator")],null),defaults["null"]);def("SpreadProperty").bases("Node").build("argument").field("argument",def("Expression"));def("RestProperty").bases("Node").build("argument").field("argument",def("Expression"));def("ForAwaitStatement").bases("Statement").build("left","right","body").field("left",or(def("VariableDeclaration"),def("Expression"))).field("right",def("Expression")).field("body",def("Statement"));// The callee node of a dynamic import(...) expression.
  def("Import").bases("Expression").build();}exports.default=default_1;module.exports=exports["default"];});unwrapExports(babelCore);var babel=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var babel_core_1=__importDefault(babelCore);var flow_1=__importDefault(flow);function default_1(fork){fork.use(babel_core_1.default);fork.use(flow_1.default);}exports.default=default_1;module.exports=exports["default"];});unwrapExports(babel);var typescript=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var babel_core_1=__importDefault(babelCore);var type_annotations_1=__importDefault(typeAnnotations);var types_1=__importDefault(types);var shared_1=__importDefault(shared);function default_1(fork){// Since TypeScript is parsed by Babylon, include the core Babylon types
  // but omit the Flow-related types.
  fork.use(babel_core_1.default);fork.use(type_annotations_1.default);var types=fork.use(types_1.default);var n=types.namedTypes;var def=types.Type.def;var or=types.Type.or;var defaults=fork.use(shared_1.default).defaults;var StringLiteral=types.Type.from(function(value,deep){if(n.StringLiteral&&n.StringLiteral.check(value,deep)){return true;}if(n.Literal&&n.Literal.check(value,deep)&&typeof value.value==="string"){return true;}return false;},"StringLiteral");def("TSType").bases("Node");var TSEntityName=or(def("Identifier"),def("TSQualifiedName"));def("TSTypeReference").bases("TSType","TSHasOptionalTypeParameterInstantiation").build("typeName","typeParameters").field("typeName",TSEntityName);// An abstract (non-buildable) base type that provide a commonly-needed
  // optional .typeParameters field.
  def("TSHasOptionalTypeParameterInstantiation").field("typeParameters",or(def("TSTypeParameterInstantiation"),null),defaults["null"]);// An abstract (non-buildable) base type that provide a commonly-needed
  // optional .typeParameters field.
  def("TSHasOptionalTypeParameters").field("typeParameters",or(def("TSTypeParameterDeclaration"),null,void 0),defaults["null"]);// An abstract (non-buildable) base type that provide a commonly-needed
  // optional .typeAnnotation field.
  def("TSHasOptionalTypeAnnotation").field("typeAnnotation",or(def("TSTypeAnnotation"),null),defaults["null"]);def("TSQualifiedName").bases("Node").build("left","right").field("left",TSEntityName).field("right",TSEntityName);def("TSAsExpression").bases("Expression","Pattern").build("expression","typeAnnotation").field("expression",def("Expression")).field("typeAnnotation",def("TSType")).field("extra",or({parenthesized:Boolean},null),defaults["null"]);def("TSNonNullExpression").bases("Expression","Pattern").build("expression").field("expression",def("Expression"));["TSAnyKeyword","TSBigIntKeyword","TSBooleanKeyword","TSNeverKeyword","TSNullKeyword","TSNumberKeyword","TSObjectKeyword","TSStringKeyword","TSSymbolKeyword","TSUndefinedKeyword","TSUnknownKeyword","TSVoidKeyword","TSThisType"].forEach(function(keywordType){def(keywordType).bases("TSType").build();});def("TSArrayType").bases("TSType").build("elementType").field("elementType",def("TSType"));def("TSLiteralType").bases("TSType").build("literal").field("literal",or(def("NumericLiteral"),def("StringLiteral"),def("BooleanLiteral"),def("TemplateLiteral"),def("UnaryExpression")));["TSUnionType","TSIntersectionType"].forEach(function(typeName){def(typeName).bases("TSType").build("types").field("types",[def("TSType")]);});def("TSConditionalType").bases("TSType").build("checkType","extendsType","trueType","falseType").field("checkType",def("TSType")).field("extendsType",def("TSType")).field("trueType",def("TSType")).field("falseType",def("TSType"));def("TSInferType").bases("TSType").build("typeParameter").field("typeParameter",def("TSTypeParameter"));def("TSParenthesizedType").bases("TSType").build("typeAnnotation").field("typeAnnotation",def("TSType"));var ParametersType=[or(def("Identifier"),def("RestElement"),def("ArrayPattern"),def("ObjectPattern"))];["TSFunctionType","TSConstructorType"].forEach(function(typeName){def(typeName).bases("TSType","TSHasOptionalTypeParameters","TSHasOptionalTypeAnnotation").build("parameters").field("parameters",ParametersType);});def("TSDeclareFunction").bases("Declaration","TSHasOptionalTypeParameters").build("id","params","returnType").field("declare",Boolean,defaults["false"]).field("async",Boolean,defaults["false"]).field("generator",Boolean,defaults["false"]).field("id",or(def("Identifier"),null),defaults["null"]).field("params",[def("Pattern")])// tSFunctionTypeAnnotationCommon
  .field("returnType",or(def("TSTypeAnnotation"),def("Noop"),// Still used?
  null),defaults["null"]);def("TSDeclareMethod").bases("Declaration","TSHasOptionalTypeParameters").build("key","params","returnType").field("async",Boolean,defaults["false"]).field("generator",Boolean,defaults["false"]).field("params",[def("Pattern")])// classMethodOrPropertyCommon
  .field("abstract",Boolean,defaults["false"]).field("accessibility",or("public","private","protected",void 0),defaults["undefined"]).field("static",Boolean,defaults["false"]).field("computed",Boolean,defaults["false"]).field("optional",Boolean,defaults["false"]).field("key",or(def("Identifier"),def("StringLiteral"),def("NumericLiteral"),// Only allowed if .computed is true.
  def("Expression")))// classMethodOrDeclareMethodCommon
  .field("kind",or("get","set","method","constructor"),function getDefault(){return "method";}).field("access",// Not "accessibility"?
  or("public","private","protected",void 0),defaults["undefined"]).field("decorators",or([def("Decorator")],null),defaults["null"])// tSFunctionTypeAnnotationCommon
  .field("returnType",or(def("TSTypeAnnotation"),def("Noop"),// Still used?
  null),defaults["null"]);def("TSMappedType").bases("TSType").build("typeParameter","typeAnnotation").field("readonly",or(Boolean,"+","-"),defaults["false"]).field("typeParameter",def("TSTypeParameter")).field("optional",or(Boolean,"+","-"),defaults["false"]).field("typeAnnotation",or(def("TSType"),null),defaults["null"]);def("TSTupleType").bases("TSType").build("elementTypes").field("elementTypes",[def("TSType")]);def("TSRestType").bases("TSType").build("typeAnnotation").field("typeAnnotation",def("TSType"));def("TSOptionalType").bases("TSType").build("typeAnnotation").field("typeAnnotation",def("TSType"));def("TSIndexedAccessType").bases("TSType").build("objectType","indexType").field("objectType",def("TSType")).field("indexType",def("TSType"));def("TSTypeOperator").bases("TSType").build("operator").field("operator",String).field("typeAnnotation",def("TSType"));def("TSTypeAnnotation").bases("Node").build("typeAnnotation").field("typeAnnotation",or(def("TSType"),def("TSTypeAnnotation")));def("TSIndexSignature").bases("Declaration","TSHasOptionalTypeAnnotation").build("parameters","typeAnnotation").field("parameters",[def("Identifier")])// Length === 1
  .field("readonly",Boolean,defaults["false"]);def("TSPropertySignature").bases("Declaration","TSHasOptionalTypeAnnotation").build("key","typeAnnotation","optional").field("key",def("Expression")).field("computed",Boolean,defaults["false"]).field("readonly",Boolean,defaults["false"]).field("optional",Boolean,defaults["false"]).field("initializer",or(def("Expression"),null),defaults["null"]);def("TSMethodSignature").bases("Declaration","TSHasOptionalTypeParameters","TSHasOptionalTypeAnnotation").build("key","parameters","typeAnnotation").field("key",def("Expression")).field("computed",Boolean,defaults["false"]).field("optional",Boolean,defaults["false"]).field("parameters",ParametersType);def("TSTypePredicate").bases("TSTypeAnnotation").build("parameterName","typeAnnotation").field("parameterName",or(def("Identifier"),def("TSThisType"))).field("typeAnnotation",def("TSTypeAnnotation"));["TSCallSignatureDeclaration","TSConstructSignatureDeclaration"].forEach(function(typeName){def(typeName).bases("Declaration","TSHasOptionalTypeParameters","TSHasOptionalTypeAnnotation").build("parameters","typeAnnotation").field("parameters",ParametersType);});def("TSEnumMember").bases("Node").build("id","initializer").field("id",or(def("Identifier"),StringLiteral)).field("initializer",or(def("Expression"),null),defaults["null"]);def("TSTypeQuery").bases("TSType").build("exprName").field("exprName",or(TSEntityName,def("TSImportType")));// Inferred from Babylon's tsParseTypeMember method.
  var TSTypeMember=or(def("TSCallSignatureDeclaration"),def("TSConstructSignatureDeclaration"),def("TSIndexSignature"),def("TSMethodSignature"),def("TSPropertySignature"));def("TSTypeLiteral").bases("TSType").build("members").field("members",[TSTypeMember]);def("TSTypeParameter").bases("Identifier").build("name","constraint","default").field("name",String).field("constraint",or(def("TSType"),void 0),defaults["undefined"]).field("default",or(def("TSType"),void 0),defaults["undefined"]);def("TSTypeAssertion").bases("Expression","Pattern").build("typeAnnotation","expression").field("typeAnnotation",def("TSType")).field("expression",def("Expression")).field("extra",or({parenthesized:Boolean},null),defaults["null"]);def("TSTypeParameterDeclaration").bases("Declaration").build("params").field("params",[def("TSTypeParameter")]);def("TSTypeParameterInstantiation").bases("Node").build("params").field("params",[def("TSType")]);def("TSEnumDeclaration").bases("Declaration").build("id","members").field("id",def("Identifier")).field("const",Boolean,defaults["false"]).field("declare",Boolean,defaults["false"]).field("members",[def("TSEnumMember")]).field("initializer",or(def("Expression"),null),defaults["null"]);def("TSTypeAliasDeclaration").bases("Declaration","TSHasOptionalTypeParameters").build("id","typeAnnotation").field("id",def("Identifier")).field("declare",Boolean,defaults["false"]).field("typeAnnotation",def("TSType"));def("TSModuleBlock").bases("Node").build("body").field("body",[def("Statement")]);def("TSModuleDeclaration").bases("Declaration").build("id","body").field("id",or(StringLiteral,TSEntityName)).field("declare",Boolean,defaults["false"]).field("global",Boolean,defaults["false"]).field("body",or(def("TSModuleBlock"),def("TSModuleDeclaration"),null),defaults["null"]);def("TSImportType").bases("TSType","TSHasOptionalTypeParameterInstantiation").build("argument","qualifier","typeParameters").field("argument",StringLiteral).field("qualifier",or(TSEntityName,void 0),defaults["undefined"]);def("TSImportEqualsDeclaration").bases("Declaration").build("id","moduleReference").field("id",def("Identifier")).field("isExport",Boolean,defaults["false"]).field("moduleReference",or(TSEntityName,def("TSExternalModuleReference")));def("TSExternalModuleReference").bases("Declaration").build("expression").field("expression",StringLiteral);def("TSExportAssignment").bases("Statement").build("expression").field("expression",def("Expression"));def("TSNamespaceExportDeclaration").bases("Declaration").build("id").field("id",def("Identifier"));def("TSInterfaceBody").bases("Node").build("body").field("body",[TSTypeMember]);def("TSExpressionWithTypeArguments").bases("TSType","TSHasOptionalTypeParameterInstantiation").build("expression","typeParameters").field("expression",TSEntityName);def("TSInterfaceDeclaration").bases("Declaration","TSHasOptionalTypeParameters").build("id","body").field("id",TSEntityName).field("declare",Boolean,defaults["false"]).field("extends",or([def("TSExpressionWithTypeArguments")],null),defaults["null"]).field("body",def("TSInterfaceBody"));def("TSParameterProperty").bases("Pattern").build("parameter").field("accessibility",or("public","private","protected",void 0),defaults["undefined"]).field("readonly",Boolean,defaults["false"]).field("parameter",or(def("Identifier"),def("AssignmentPattern")));def("ClassProperty").field("access",// Not "accessibility"?
  or("public","private","protected",void 0),defaults["undefined"]);// Defined already in es6 and babel-core.
  def("ClassBody").field("body",[or(def("MethodDefinition"),def("VariableDeclarator"),def("ClassPropertyDefinition"),def("ClassProperty"),def("ClassPrivateProperty"),def("ClassMethod"),def("ClassPrivateMethod"),// Just need to add these types:
  def("TSDeclareMethod"),TSTypeMember)]);}exports.default=default_1;module.exports=exports["default"];});unwrapExports(typescript);var esProposals=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var types_1=__importDefault(types);var shared_1=__importDefault(shared);var core_1=__importDefault(core);function default_1(fork){fork.use(core_1.default);var types=fork.use(types_1.default);var Type=types.Type;var def=types.Type.def;var or=Type.or;var shared=fork.use(shared_1.default);var defaults=shared.defaults;// https://github.com/tc39/proposal-optional-chaining
  // `a?.b` as per https://github.com/estree/estree/issues/146
  def("OptionalMemberExpression").bases("MemberExpression").build("object","property","computed","optional").field("optional",Boolean,defaults["true"]);// a?.b()
  def("OptionalCallExpression").bases("CallExpression").build("callee","arguments","optional").field("optional",Boolean,defaults["true"]);// https://github.com/tc39/proposal-nullish-coalescing
  // `a ?? b` as per https://github.com/babel/babylon/pull/761/files
  var LogicalOperator=or("||","&&","??");def("LogicalExpression").field("operator",LogicalOperator);}exports.default=default_1;module.exports=exports["default"];});unwrapExports(esProposals);var namedTypes_1=createCommonjsModule(function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});var namedTypes;(function(namedTypes){})(namedTypes=exports.namedTypes||(exports.namedTypes={}));});unwrapExports(namedTypes_1);var namedTypes_2=namedTypes_1.namedTypes;var main=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var fork_1=__importDefault(fork);var core_1=__importDefault(core);var es6_1=__importDefault(es6);var es7_1=__importDefault(es7);var jsx_1=__importDefault(jsx);var flow_1=__importDefault(flow);var esprima_1=__importDefault(esprima);var babel_1=__importDefault(babel);var typescript_1=__importDefault(typescript);var es_proposals_1=__importDefault(esProposals);exports.namedTypes=namedTypes_1.namedTypes;var _a=fork_1.default([// This core module of AST types captures ES5 as it is parsed today by
  // git://github.com/ariya/esprima.git#master.
  core_1.default,// Feel free to add to or remove from this list of extension modules to
  // configure the precise type hierarchy that you need.
  es6_1.default,es7_1.default,jsx_1.default,flow_1.default,esprima_1.default,babel_1.default,typescript_1.default,es_proposals_1.default]),astNodesAreEquivalent=_a.astNodesAreEquivalent,builders=_a.builders,builtInTypes=_a.builtInTypes,defineMethod=_a.defineMethod,eachField=_a.eachField,finalize=_a.finalize,getBuilderName=_a.getBuilderName,getFieldNames=_a.getFieldNames,getFieldValue=_a.getFieldValue,getSupertypeNames=_a.getSupertypeNames,n=_a.namedTypes,NodePath=_a.NodePath,Path=_a.Path,PathVisitor=_a.PathVisitor,someField=_a.someField,Type=_a.Type,use=_a.use,visit=_a.visit;exports.astNodesAreEquivalent=astNodesAreEquivalent;exports.builders=builders;exports.builtInTypes=builtInTypes;exports.defineMethod=defineMethod;exports.eachField=eachField;exports.finalize=finalize;exports.getBuilderName=getBuilderName;exports.getFieldNames=getFieldNames;exports.getFieldValue=getFieldValue;exports.getSupertypeNames=getSupertypeNames;exports.NodePath=NodePath;exports.Path=Path;exports.PathVisitor=PathVisitor;exports.someField=someField;exports.Type=Type;exports.use=use;exports.visit=visit;// Populate the exported fields of the namedTypes namespace, while still
  // retaining its member types.
  Object.assign(namedTypes_1.namedTypes,n);});unwrapExports(main);var main_1=main.namedTypes;var main_2=main.astNodesAreEquivalent;var main_3=main.builders;var main_4=main.builtInTypes;var main_5=main.defineMethod;var main_6=main.eachField;var main_7=main.finalize;var main_8=main.getBuilderName;var main_9=main.getFieldNames;var main_10=main.getFieldValue;var main_11=main.getSupertypeNames;var main_12=main.NodePath;var main_13=main.Path;var main_14=main.PathVisitor;var main_15=main.someField;var main_16=main.Type;var main_17=main.use;var main_18=main.visit;var lookup=[];var revLookup=[];var Arr=typeof Uint8Array!=='undefined'?Uint8Array:Array;var inited=false;function init(){inited=true;var code='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';for(var i=0,len=code.length;i<len;++i){lookup[i]=code[i];revLookup[code.charCodeAt(i)]=i;}revLookup['-'.charCodeAt(0)]=62;revLookup['_'.charCodeAt(0)]=63;}function toByteArray(b64){if(!inited){init();}var i,j,l,tmp,placeHolders,arr;var len=b64.length;if(len%4>0){throw new Error('Invalid string. Length must be a multiple of 4');}// the number of equal signs (place holders)
  // if there are two placeholders, than the two characters before it
  // represent one byte
  // if there is only one, then the three characters before it represent 2 bytes
  // this is just a cheap hack to not do indexOf twice
  placeHolders=b64[len-2]==='='?2:b64[len-1]==='='?1:0;// base64 is 4/3 + up to two characters of the original data
  arr=new Arr(len*3/4-placeHolders);// if there are placeholders, only get up to the last complete 4 chars
  l=placeHolders>0?len-4:len;var L=0;for(i=0,j=0;i<l;i+=4,j+=3){tmp=revLookup[b64.charCodeAt(i)]<<18|revLookup[b64.charCodeAt(i+1)]<<12|revLookup[b64.charCodeAt(i+2)]<<6|revLookup[b64.charCodeAt(i+3)];arr[L++]=tmp>>16&0xFF;arr[L++]=tmp>>8&0xFF;arr[L++]=tmp&0xFF;}if(placeHolders===2){tmp=revLookup[b64.charCodeAt(i)]<<2|revLookup[b64.charCodeAt(i+1)]>>4;arr[L++]=tmp&0xFF;}else if(placeHolders===1){tmp=revLookup[b64.charCodeAt(i)]<<10|revLookup[b64.charCodeAt(i+1)]<<4|revLookup[b64.charCodeAt(i+2)]>>2;arr[L++]=tmp>>8&0xFF;arr[L++]=tmp&0xFF;}return arr;}function tripletToBase64(num){return lookup[num>>18&0x3F]+lookup[num>>12&0x3F]+lookup[num>>6&0x3F]+lookup[num&0x3F];}function encodeChunk(uint8,start,end){var tmp;var output=[];for(var i=start;i<end;i+=3){tmp=(uint8[i]<<16)+(uint8[i+1]<<8)+uint8[i+2];output.push(tripletToBase64(tmp));}return output.join('');}function fromByteArray(uint8){if(!inited){init();}var tmp;var len=uint8.length;var extraBytes=len%3;// if we have 1 byte left, pad 2 bytes
  var output='';var parts=[];var maxChunkLength=16383;// must be multiple of 3
  // go through the array every three bytes, we'll deal with trailing stuff later
  for(var i=0,len2=len-extraBytes;i<len2;i+=maxChunkLength){parts.push(encodeChunk(uint8,i,i+maxChunkLength>len2?len2:i+maxChunkLength));}// pad the end with zeros, but make sure to not forget the extra bytes
  if(extraBytes===1){tmp=uint8[len-1];output+=lookup[tmp>>2];output+=lookup[tmp<<4&0x3F];output+='==';}else if(extraBytes===2){tmp=(uint8[len-2]<<8)+uint8[len-1];output+=lookup[tmp>>10];output+=lookup[tmp>>4&0x3F];output+=lookup[tmp<<2&0x3F];output+='=';}parts.push(output);return parts.join('');}function read(buffer,offset,isLE,mLen,nBytes){var e,m;var eLen=nBytes*8-mLen-1;var eMax=(1<<eLen)-1;var eBias=eMax>>1;var nBits=-7;var i=isLE?nBytes-1:0;var d=isLE?-1:1;var s=buffer[offset+i];i+=d;e=s&(1<<-nBits)-1;s>>=-nBits;nBits+=eLen;for(;nBits>0;e=e*256+buffer[offset+i],i+=d,nBits-=8){}m=e&(1<<-nBits)-1;e>>=-nBits;nBits+=mLen;for(;nBits>0;m=m*256+buffer[offset+i],i+=d,nBits-=8){}if(e===0){e=1-eBias;}else if(e===eMax){return m?NaN:(s?-1:1)*Infinity;}else{m=m+Math.pow(2,mLen);e=e-eBias;}return (s?-1:1)*m*Math.pow(2,e-mLen);}function write(buffer,value,offset,isLE,mLen,nBytes){var e,m,c;var eLen=nBytes*8-mLen-1;var eMax=(1<<eLen)-1;var eBias=eMax>>1;var rt=mLen===23?Math.pow(2,-24)-Math.pow(2,-77):0;var i=isLE?0:nBytes-1;var d=isLE?1:-1;var s=value<0||value===0&&1/value<0?1:0;value=Math.abs(value);if(isNaN(value)||value===Infinity){m=isNaN(value)?1:0;e=eMax;}else{e=Math.floor(Math.log(value)/Math.LN2);if(value*(c=Math.pow(2,-e))<1){e--;c*=2;}if(e+eBias>=1){value+=rt/c;}else{value+=rt*Math.pow(2,1-eBias);}if(value*c>=2){e++;c/=2;}if(e+eBias>=eMax){m=0;e=eMax;}else if(e+eBias>=1){m=(value*c-1)*Math.pow(2,mLen);e=e+eBias;}else{m=value*Math.pow(2,eBias-1)*Math.pow(2,mLen);e=0;}}for(;mLen>=8;buffer[offset+i]=m&0xff,i+=d,m/=256,mLen-=8){}e=e<<mLen|m;eLen+=mLen;for(;eLen>0;buffer[offset+i]=e&0xff,i+=d,e/=256,eLen-=8){}buffer[offset+i-d]|=s*128;}var toString={}.toString;var isArray=Array.isArray||function(arr){return toString.call(arr)=='[object Array]';};/*!
  	 * The buffer module from node.js, for the browser.
  	 *
  	 * @author   Feross Aboukhadijeh <feross@feross.org> <http://feross.org>
  	 * @license  MIT
  	 */var INSPECT_MAX_BYTES=50;/**
  	 * If `Buffer.TYPED_ARRAY_SUPPORT`:
  	 *   === true    Use Uint8Array implementation (fastest)
  	 *   === false   Use Object implementation (most compatible, even IE6)
  	 *
  	 * Browsers that support typed arrays are IE 10+, Firefox 4+, Chrome 7+, Safari 5.1+,
  	 * Opera 11.6+, iOS 4.2+.
  	 *
  	 * Due to various browser bugs, sometimes the Object implementation will be used even
  	 * when the browser supports typed arrays.
  	 *
  	 * Note:
  	 *
  	 *   - Firefox 4-29 lacks support for adding new properties to `Uint8Array` instances,
  	 *     See: https://bugzilla.mozilla.org/show_bug.cgi?id=695438.
  	 *
  	 *   - Chrome 9-10 is missing the `TypedArray.prototype.subarray` function.
  	 *
  	 *   - IE10 has a broken `TypedArray.prototype.subarray` function which returns arrays of
  	 *     incorrect length in some situations.

  	 * We detect these buggy browsers and set `Buffer.TYPED_ARRAY_SUPPORT` to `false` so they
  	 * get the Object implementation, which is slower but behaves correctly.
  	 */Buffer.TYPED_ARRAY_SUPPORT=commonjsGlobal.TYPED_ARRAY_SUPPORT!==undefined?commonjsGlobal.TYPED_ARRAY_SUPPORT:true;function kMaxLength(){return Buffer.TYPED_ARRAY_SUPPORT?0x7fffffff:0x3fffffff;}function createBuffer(that,length){if(kMaxLength()<length){throw new RangeError('Invalid typed array length');}if(Buffer.TYPED_ARRAY_SUPPORT){// Return an augmented `Uint8Array` instance, for best performance
  that=new Uint8Array(length);that.__proto__=Buffer.prototype;}else{// Fallback: Return an object instance of the Buffer class
  if(that===null){that=new Buffer(length);}that.length=length;}return that;}/**
  	 * The Buffer constructor returns instances of `Uint8Array` that have their
  	 * prototype changed to `Buffer.prototype`. Furthermore, `Buffer` is a subclass of
  	 * `Uint8Array`, so the returned instances will have all the node `Buffer` methods
  	 * and the `Uint8Array` methods. Square bracket notation works as expected -- it
  	 * returns a single octet.
  	 *
  	 * The `Uint8Array` prototype remains unmodified.
  	 */function Buffer(arg,encodingOrOffset,length){if(!Buffer.TYPED_ARRAY_SUPPORT&&!(this instanceof Buffer)){return new Buffer(arg,encodingOrOffset,length);}// Common case.
  if(typeof arg==='number'){if(typeof encodingOrOffset==='string'){throw new Error('If encoding is specified then the first argument must be a string');}return allocUnsafe(this,arg);}return from(this,arg,encodingOrOffset,length);}Buffer.poolSize=8192;// not used by this implementation
  // TODO: Legacy, not needed anymore. Remove in next major version.
  Buffer._augment=function(arr){arr.__proto__=Buffer.prototype;return arr;};function from(that,value,encodingOrOffset,length){if(typeof value==='number'){throw new TypeError('"value" argument must not be a number');}if(typeof ArrayBuffer!=='undefined'&&value instanceof ArrayBuffer){return fromArrayBuffer(that,value,encodingOrOffset,length);}if(typeof value==='string'){return fromString(that,value,encodingOrOffset);}return fromObject(that,value);}/**
  	 * Functionally equivalent to Buffer(arg, encoding) but throws a TypeError
  	 * if value is a number.
  	 * Buffer.from(str[, encoding])
  	 * Buffer.from(array)
  	 * Buffer.from(buffer)
  	 * Buffer.from(arrayBuffer[, byteOffset[, length]])
  	 **/Buffer.from=function(value,encodingOrOffset,length){return from(null,value,encodingOrOffset,length);};if(Buffer.TYPED_ARRAY_SUPPORT){Buffer.prototype.__proto__=Uint8Array.prototype;Buffer.__proto__=Uint8Array;}function assertSize(size){if(typeof size!=='number'){throw new TypeError('"size" argument must be a number');}else if(size<0){throw new RangeError('"size" argument must not be negative');}}function alloc(that,size,fill,encoding){assertSize(size);if(size<=0){return createBuffer(that,size);}if(fill!==undefined){// Only pay attention to encoding if it's a string. This
  // prevents accidentally sending in a number that would
  // be interpretted as a start offset.
  return typeof encoding==='string'?createBuffer(that,size).fill(fill,encoding):createBuffer(that,size).fill(fill);}return createBuffer(that,size);}/**
  	 * Creates a new filled Buffer instance.
  	 * alloc(size[, fill[, encoding]])
  	 **/Buffer.alloc=function(size,fill,encoding){return alloc(null,size,fill,encoding);};function allocUnsafe(that,size){assertSize(size);that=createBuffer(that,size<0?0:checked(size)|0);if(!Buffer.TYPED_ARRAY_SUPPORT){for(var i=0;i<size;++i){that[i]=0;}}return that;}/**
  	 * Equivalent to Buffer(num), by default creates a non-zero-filled Buffer instance.
  	 * */Buffer.allocUnsafe=function(size){return allocUnsafe(null,size);};/**
  	 * Equivalent to SlowBuffer(num), by default creates a non-zero-filled Buffer instance.
  	 */Buffer.allocUnsafeSlow=function(size){return allocUnsafe(null,size);};function fromString(that,string,encoding){if(typeof encoding!=='string'||encoding===''){encoding='utf8';}if(!Buffer.isEncoding(encoding)){throw new TypeError('"encoding" must be a valid string encoding');}var length=byteLength(string,encoding)|0;that=createBuffer(that,length);var actual=that.write(string,encoding);if(actual!==length){// Writing a hex string, for example, that contains invalid characters will
  // cause everything after the first invalid character to be ignored. (e.g.
  // 'abxxcd' will be treated as 'ab')
  that=that.slice(0,actual);}return that;}function fromArrayLike(that,array){var length=array.length<0?0:checked(array.length)|0;that=createBuffer(that,length);for(var i=0;i<length;i+=1){that[i]=array[i]&255;}return that;}function fromArrayBuffer(that,array,byteOffset,length){array.byteLength;// this throws if `array` is not a valid ArrayBuffer
  if(byteOffset<0||array.byteLength<byteOffset){throw new RangeError('\'offset\' is out of bounds');}if(array.byteLength<byteOffset+(length||0)){throw new RangeError('\'length\' is out of bounds');}if(byteOffset===undefined&&length===undefined){array=new Uint8Array(array);}else if(length===undefined){array=new Uint8Array(array,byteOffset);}else{array=new Uint8Array(array,byteOffset,length);}if(Buffer.TYPED_ARRAY_SUPPORT){// Return an augmented `Uint8Array` instance, for best performance
  that=array;that.__proto__=Buffer.prototype;}else{// Fallback: Return an object instance of the Buffer class
  that=fromArrayLike(that,array);}return that;}function fromObject(that,obj){if(internalIsBuffer(obj)){var len=checked(obj.length)|0;that=createBuffer(that,len);if(that.length===0){return that;}obj.copy(that,0,0,len);return that;}if(obj){if(typeof ArrayBuffer!=='undefined'&&obj.buffer instanceof ArrayBuffer||'length'in obj){if(typeof obj.length!=='number'||isnan(obj.length)){return createBuffer(that,0);}return fromArrayLike(that,obj);}if(obj.type==='Buffer'&&isArray(obj.data)){return fromArrayLike(that,obj.data);}}throw new TypeError('First argument must be a string, Buffer, ArrayBuffer, Array, or array-like object.');}function checked(length){// Note: cannot use `length < kMaxLength()` here because that fails when
  // length is NaN (which is otherwise coerced to zero.)
  if(length>=kMaxLength()){throw new RangeError('Attempt to allocate Buffer larger than maximum '+'size: 0x'+kMaxLength().toString(16)+' bytes');}return length|0;}Buffer.isBuffer=isBuffer;function internalIsBuffer(b){return !!(b!=null&&b._isBuffer);}Buffer.compare=function compare(a,b){if(!internalIsBuffer(a)||!internalIsBuffer(b)){throw new TypeError('Arguments must be Buffers');}if(a===b)return 0;var x=a.length;var y=b.length;for(var i=0,len=Math.min(x,y);i<len;++i){if(a[i]!==b[i]){x=a[i];y=b[i];break;}}if(x<y)return -1;if(y<x)return 1;return 0;};Buffer.isEncoding=function isEncoding(encoding){switch(String(encoding).toLowerCase()){case'hex':case'utf8':case'utf-8':case'ascii':case'latin1':case'binary':case'base64':case'ucs2':case'ucs-2':case'utf16le':case'utf-16le':return true;default:return false;}};Buffer.concat=function concat(list,length){if(!isArray(list)){throw new TypeError('"list" argument must be an Array of Buffers');}if(list.length===0){return Buffer.alloc(0);}var i;if(length===undefined){length=0;for(i=0;i<list.length;++i){length+=list[i].length;}}var buffer=Buffer.allocUnsafe(length);var pos=0;for(i=0;i<list.length;++i){var buf=list[i];if(!internalIsBuffer(buf)){throw new TypeError('"list" argument must be an Array of Buffers');}buf.copy(buffer,pos);pos+=buf.length;}return buffer;};function byteLength(string,encoding){if(internalIsBuffer(string)){return string.length;}if(typeof ArrayBuffer!=='undefined'&&typeof ArrayBuffer.isView==='function'&&(ArrayBuffer.isView(string)||string instanceof ArrayBuffer)){return string.byteLength;}if(typeof string!=='string'){string=''+string;}var len=string.length;if(len===0)return 0;// Use a for loop to avoid recursion
  var loweredCase=false;for(;;){switch(encoding){case'ascii':case'latin1':case'binary':return len;case'utf8':case'utf-8':case undefined:return utf8ToBytes(string).length;case'ucs2':case'ucs-2':case'utf16le':case'utf-16le':return len*2;case'hex':return len>>>1;case'base64':return base64ToBytes(string).length;default:if(loweredCase)return utf8ToBytes(string).length;// assume utf8
  encoding=(''+encoding).toLowerCase();loweredCase=true;}}}Buffer.byteLength=byteLength;function slowToString(encoding,start,end){var loweredCase=false;// No need to verify that "this.length <= MAX_UINT32" since it's a read-only
  // property of a typed array.
  // This behaves neither like String nor Uint8Array in that we set start/end
  // to their upper/lower bounds if the value passed is out of range.
  // undefined is handled specially as per ECMA-262 6th Edition,
  // Section 13.3.3.7 Runtime Semantics: KeyedBindingInitialization.
  if(start===undefined||start<0){start=0;}// Return early if start > this.length. Done here to prevent potential uint32
  // coercion fail below.
  if(start>this.length){return '';}if(end===undefined||end>this.length){end=this.length;}if(end<=0){return '';}// Force coersion to uint32. This will also coerce falsey/NaN values to 0.
  end>>>=0;start>>>=0;if(end<=start){return '';}if(!encoding)encoding='utf8';while(true){switch(encoding){case'hex':return hexSlice(this,start,end);case'utf8':case'utf-8':return utf8Slice(this,start,end);case'ascii':return asciiSlice(this,start,end);case'latin1':case'binary':return latin1Slice(this,start,end);case'base64':return base64Slice(this,start,end);case'ucs2':case'ucs-2':case'utf16le':case'utf-16le':return utf16leSlice(this,start,end);default:if(loweredCase)throw new TypeError('Unknown encoding: '+encoding);encoding=(encoding+'').toLowerCase();loweredCase=true;}}}// The property is used by `Buffer.isBuffer` and `is-buffer` (in Safari 5-7) to detect
  // Buffer instances.
  Buffer.prototype._isBuffer=true;function swap(b,n,m){var i=b[n];b[n]=b[m];b[m]=i;}Buffer.prototype.swap16=function swap16(){var len=this.length;if(len%2!==0){throw new RangeError('Buffer size must be a multiple of 16-bits');}for(var i=0;i<len;i+=2){swap(this,i,i+1);}return this;};Buffer.prototype.swap32=function swap32(){var len=this.length;if(len%4!==0){throw new RangeError('Buffer size must be a multiple of 32-bits');}for(var i=0;i<len;i+=4){swap(this,i,i+3);swap(this,i+1,i+2);}return this;};Buffer.prototype.swap64=function swap64(){var len=this.length;if(len%8!==0){throw new RangeError('Buffer size must be a multiple of 64-bits');}for(var i=0;i<len;i+=8){swap(this,i,i+7);swap(this,i+1,i+6);swap(this,i+2,i+5);swap(this,i+3,i+4);}return this;};Buffer.prototype.toString=function toString(){var length=this.length|0;if(length===0)return '';if(arguments.length===0)return utf8Slice(this,0,length);return slowToString.apply(this,arguments);};Buffer.prototype.equals=function equals(b){if(!internalIsBuffer(b))throw new TypeError('Argument must be a Buffer');if(this===b)return true;return Buffer.compare(this,b)===0;};Buffer.prototype.inspect=function inspect(){var str='';var max=INSPECT_MAX_BYTES;if(this.length>0){str=this.toString('hex',0,max).match(/.{2}/g).join(' ');if(this.length>max)str+=' ... ';}return '<Buffer '+str+'>';};Buffer.prototype.compare=function compare(target,start,end,thisStart,thisEnd){if(!internalIsBuffer(target)){throw new TypeError('Argument must be a Buffer');}if(start===undefined){start=0;}if(end===undefined){end=target?target.length:0;}if(thisStart===undefined){thisStart=0;}if(thisEnd===undefined){thisEnd=this.length;}if(start<0||end>target.length||thisStart<0||thisEnd>this.length){throw new RangeError('out of range index');}if(thisStart>=thisEnd&&start>=end){return 0;}if(thisStart>=thisEnd){return -1;}if(start>=end){return 1;}start>>>=0;end>>>=0;thisStart>>>=0;thisEnd>>>=0;if(this===target)return 0;var x=thisEnd-thisStart;var y=end-start;var len=Math.min(x,y);var thisCopy=this.slice(thisStart,thisEnd);var targetCopy=target.slice(start,end);for(var i=0;i<len;++i){if(thisCopy[i]!==targetCopy[i]){x=thisCopy[i];y=targetCopy[i];break;}}if(x<y)return -1;if(y<x)return 1;return 0;};// Finds either the first index of `val` in `buffer` at offset >= `byteOffset`,
  // OR the last index of `val` in `buffer` at offset <= `byteOffset`.
  //
  // Arguments:
  // - buffer - a Buffer to search
  // - val - a string, Buffer, or number
  // - byteOffset - an index into `buffer`; will be clamped to an int32
  // - encoding - an optional encoding, relevant is val is a string
  // - dir - true for indexOf, false for lastIndexOf
  function bidirectionalIndexOf(buffer,val,byteOffset,encoding,dir){// Empty buffer means no match
  if(buffer.length===0)return -1;// Normalize byteOffset
  if(typeof byteOffset==='string'){encoding=byteOffset;byteOffset=0;}else if(byteOffset>0x7fffffff){byteOffset=0x7fffffff;}else if(byteOffset<-0x80000000){byteOffset=-0x80000000;}byteOffset=+byteOffset;// Coerce to Number.
  if(isNaN(byteOffset)){// byteOffset: it it's undefined, null, NaN, "foo", etc, search whole buffer
  byteOffset=dir?0:buffer.length-1;}// Normalize byteOffset: negative offsets start from the end of the buffer
  if(byteOffset<0)byteOffset=buffer.length+byteOffset;if(byteOffset>=buffer.length){if(dir)return -1;else byteOffset=buffer.length-1;}else if(byteOffset<0){if(dir)byteOffset=0;else return -1;}// Normalize val
  if(typeof val==='string'){val=Buffer.from(val,encoding);}// Finally, search either indexOf (if dir is true) or lastIndexOf
  if(internalIsBuffer(val)){// Special case: looking for empty string/buffer always fails
  if(val.length===0){return -1;}return arrayIndexOf(buffer,val,byteOffset,encoding,dir);}else if(typeof val==='number'){val=val&0xFF;// Search for a byte value [0-255]
  if(Buffer.TYPED_ARRAY_SUPPORT&&typeof Uint8Array.prototype.indexOf==='function'){if(dir){return Uint8Array.prototype.indexOf.call(buffer,val,byteOffset);}else{return Uint8Array.prototype.lastIndexOf.call(buffer,val,byteOffset);}}return arrayIndexOf(buffer,[val],byteOffset,encoding,dir);}throw new TypeError('val must be string, number or Buffer');}function arrayIndexOf(arr,val,byteOffset,encoding,dir){var indexSize=1;var arrLength=arr.length;var valLength=val.length;if(encoding!==undefined){encoding=String(encoding).toLowerCase();if(encoding==='ucs2'||encoding==='ucs-2'||encoding==='utf16le'||encoding==='utf-16le'){if(arr.length<2||val.length<2){return -1;}indexSize=2;arrLength/=2;valLength/=2;byteOffset/=2;}}function read(buf,i){if(indexSize===1){return buf[i];}else{return buf.readUInt16BE(i*indexSize);}}var i;if(dir){var foundIndex=-1;for(i=byteOffset;i<arrLength;i++){if(read(arr,i)===read(val,foundIndex===-1?0:i-foundIndex)){if(foundIndex===-1)foundIndex=i;if(i-foundIndex+1===valLength)return foundIndex*indexSize;}else{if(foundIndex!==-1)i-=i-foundIndex;foundIndex=-1;}}}else{if(byteOffset+valLength>arrLength)byteOffset=arrLength-valLength;for(i=byteOffset;i>=0;i--){var found=true;for(var j=0;j<valLength;j++){if(read(arr,i+j)!==read(val,j)){found=false;break;}}if(found)return i;}}return -1;}Buffer.prototype.includes=function includes(val,byteOffset,encoding){return this.indexOf(val,byteOffset,encoding)!==-1;};Buffer.prototype.indexOf=function indexOf(val,byteOffset,encoding){return bidirectionalIndexOf(this,val,byteOffset,encoding,true);};Buffer.prototype.lastIndexOf=function lastIndexOf(val,byteOffset,encoding){return bidirectionalIndexOf(this,val,byteOffset,encoding,false);};function hexWrite(buf,string,offset,length){offset=Number(offset)||0;var remaining=buf.length-offset;if(!length){length=remaining;}else{length=Number(length);if(length>remaining){length=remaining;}}// must be an even number of digits
  var strLen=string.length;if(strLen%2!==0)throw new TypeError('Invalid hex string');if(length>strLen/2){length=strLen/2;}for(var i=0;i<length;++i){var parsed=parseInt(string.substr(i*2,2),16);if(isNaN(parsed))return i;buf[offset+i]=parsed;}return i;}function utf8Write(buf,string,offset,length){return blitBuffer(utf8ToBytes(string,buf.length-offset),buf,offset,length);}function asciiWrite(buf,string,offset,length){return blitBuffer(asciiToBytes(string),buf,offset,length);}function latin1Write(buf,string,offset,length){return asciiWrite(buf,string,offset,length);}function base64Write(buf,string,offset,length){return blitBuffer(base64ToBytes(string),buf,offset,length);}function ucs2Write(buf,string,offset,length){return blitBuffer(utf16leToBytes(string,buf.length-offset),buf,offset,length);}Buffer.prototype.write=function write(string,offset,length,encoding){// Buffer#write(string)
  if(offset===undefined){encoding='utf8';length=this.length;offset=0;// Buffer#write(string, encoding)
  }else if(length===undefined&&typeof offset==='string'){encoding=offset;length=this.length;offset=0;// Buffer#write(string, offset[, length][, encoding])
  }else if(isFinite(offset)){offset=offset|0;if(isFinite(length)){length=length|0;if(encoding===undefined)encoding='utf8';}else{encoding=length;length=undefined;}// legacy write(string, encoding, offset, length) - remove in v0.13
  }else{throw new Error('Buffer.write(string, encoding, offset[, length]) is no longer supported');}var remaining=this.length-offset;if(length===undefined||length>remaining)length=remaining;if(string.length>0&&(length<0||offset<0)||offset>this.length){throw new RangeError('Attempt to write outside buffer bounds');}if(!encoding)encoding='utf8';var loweredCase=false;for(;;){switch(encoding){case'hex':return hexWrite(this,string,offset,length);case'utf8':case'utf-8':return utf8Write(this,string,offset,length);case'ascii':return asciiWrite(this,string,offset,length);case'latin1':case'binary':return latin1Write(this,string,offset,length);case'base64':// Warning: maxLength not taken into account in base64Write
  return base64Write(this,string,offset,length);case'ucs2':case'ucs-2':case'utf16le':case'utf-16le':return ucs2Write(this,string,offset,length);default:if(loweredCase)throw new TypeError('Unknown encoding: '+encoding);encoding=(''+encoding).toLowerCase();loweredCase=true;}}};Buffer.prototype.toJSON=function toJSON(){return {type:'Buffer',data:Array.prototype.slice.call(this._arr||this,0)};};function base64Slice(buf,start,end){if(start===0&&end===buf.length){return fromByteArray(buf);}else{return fromByteArray(buf.slice(start,end));}}function utf8Slice(buf,start,end){end=Math.min(buf.length,end);var res=[];var i=start;while(i<end){var firstByte=buf[i];var codePoint=null;var bytesPerSequence=firstByte>0xEF?4:firstByte>0xDF?3:firstByte>0xBF?2:1;if(i+bytesPerSequence<=end){var secondByte,thirdByte,fourthByte,tempCodePoint;switch(bytesPerSequence){case 1:if(firstByte<0x80){codePoint=firstByte;}break;case 2:secondByte=buf[i+1];if((secondByte&0xC0)===0x80){tempCodePoint=(firstByte&0x1F)<<0x6|secondByte&0x3F;if(tempCodePoint>0x7F){codePoint=tempCodePoint;}}break;case 3:secondByte=buf[i+1];thirdByte=buf[i+2];if((secondByte&0xC0)===0x80&&(thirdByte&0xC0)===0x80){tempCodePoint=(firstByte&0xF)<<0xC|(secondByte&0x3F)<<0x6|thirdByte&0x3F;if(tempCodePoint>0x7FF&&(tempCodePoint<0xD800||tempCodePoint>0xDFFF)){codePoint=tempCodePoint;}}break;case 4:secondByte=buf[i+1];thirdByte=buf[i+2];fourthByte=buf[i+3];if((secondByte&0xC0)===0x80&&(thirdByte&0xC0)===0x80&&(fourthByte&0xC0)===0x80){tempCodePoint=(firstByte&0xF)<<0x12|(secondByte&0x3F)<<0xC|(thirdByte&0x3F)<<0x6|fourthByte&0x3F;if(tempCodePoint>0xFFFF&&tempCodePoint<0x110000){codePoint=tempCodePoint;}}}}if(codePoint===null){// we did not generate a valid codePoint so insert a
  // replacement char (U+FFFD) and advance only 1 byte
  codePoint=0xFFFD;bytesPerSequence=1;}else if(codePoint>0xFFFF){// encode to utf16 (surrogate pair dance)
  codePoint-=0x10000;res.push(codePoint>>>10&0x3FF|0xD800);codePoint=0xDC00|codePoint&0x3FF;}res.push(codePoint);i+=bytesPerSequence;}return decodeCodePointsArray(res);}// Based on http://stackoverflow.com/a/22747272/680742, the browser with
  // the lowest limit is Chrome, with 0x10000 args.
  // We go 1 magnitude less, for safety
  var MAX_ARGUMENTS_LENGTH=0x1000;function decodeCodePointsArray(codePoints){var len=codePoints.length;if(len<=MAX_ARGUMENTS_LENGTH){return String.fromCharCode.apply(String,codePoints);// avoid extra slice()
  }// Decode in chunks to avoid "call stack size exceeded".
  var res='';var i=0;while(i<len){res+=String.fromCharCode.apply(String,codePoints.slice(i,i+=MAX_ARGUMENTS_LENGTH));}return res;}function asciiSlice(buf,start,end){var ret='';end=Math.min(buf.length,end);for(var i=start;i<end;++i){ret+=String.fromCharCode(buf[i]&0x7F);}return ret;}function latin1Slice(buf,start,end){var ret='';end=Math.min(buf.length,end);for(var i=start;i<end;++i){ret+=String.fromCharCode(buf[i]);}return ret;}function hexSlice(buf,start,end){var len=buf.length;if(!start||start<0)start=0;if(!end||end<0||end>len)end=len;var out='';for(var i=start;i<end;++i){out+=toHex(buf[i]);}return out;}function utf16leSlice(buf,start,end){var bytes=buf.slice(start,end);var res='';for(var i=0;i<bytes.length;i+=2){res+=String.fromCharCode(bytes[i]+bytes[i+1]*256);}return res;}Buffer.prototype.slice=function slice(start,end){var len=this.length;start=~~start;end=end===undefined?len:~~end;if(start<0){start+=len;if(start<0)start=0;}else if(start>len){start=len;}if(end<0){end+=len;if(end<0)end=0;}else if(end>len){end=len;}if(end<start)end=start;var newBuf;if(Buffer.TYPED_ARRAY_SUPPORT){newBuf=this.subarray(start,end);newBuf.__proto__=Buffer.prototype;}else{var sliceLen=end-start;newBuf=new Buffer(sliceLen,undefined);for(var i=0;i<sliceLen;++i){newBuf[i]=this[i+start];}}return newBuf;};/*
  	 * Need to make sure that buffer isn't trying to write out of bounds.
  	 */function checkOffset(offset,ext,length){if(offset%1!==0||offset<0)throw new RangeError('offset is not uint');if(offset+ext>length)throw new RangeError('Trying to access beyond buffer length');}Buffer.prototype.readUIntLE=function readUIntLE(offset,byteLength,noAssert){offset=offset|0;byteLength=byteLength|0;if(!noAssert)checkOffset(offset,byteLength,this.length);var val=this[offset];var mul=1;var i=0;while(++i<byteLength&&(mul*=0x100)){val+=this[offset+i]*mul;}return val;};Buffer.prototype.readUIntBE=function readUIntBE(offset,byteLength,noAssert){offset=offset|0;byteLength=byteLength|0;if(!noAssert){checkOffset(offset,byteLength,this.length);}var val=this[offset+--byteLength];var mul=1;while(byteLength>0&&(mul*=0x100)){val+=this[offset+--byteLength]*mul;}return val;};Buffer.prototype.readUInt8=function readUInt8(offset,noAssert){if(!noAssert)checkOffset(offset,1,this.length);return this[offset];};Buffer.prototype.readUInt16LE=function readUInt16LE(offset,noAssert){if(!noAssert)checkOffset(offset,2,this.length);return this[offset]|this[offset+1]<<8;};Buffer.prototype.readUInt16BE=function readUInt16BE(offset,noAssert){if(!noAssert)checkOffset(offset,2,this.length);return this[offset]<<8|this[offset+1];};Buffer.prototype.readUInt32LE=function readUInt32LE(offset,noAssert){if(!noAssert)checkOffset(offset,4,this.length);return (this[offset]|this[offset+1]<<8|this[offset+2]<<16)+this[offset+3]*0x1000000;};Buffer.prototype.readUInt32BE=function readUInt32BE(offset,noAssert){if(!noAssert)checkOffset(offset,4,this.length);return this[offset]*0x1000000+(this[offset+1]<<16|this[offset+2]<<8|this[offset+3]);};Buffer.prototype.readIntLE=function readIntLE(offset,byteLength,noAssert){offset=offset|0;byteLength=byteLength|0;if(!noAssert)checkOffset(offset,byteLength,this.length);var val=this[offset];var mul=1;var i=0;while(++i<byteLength&&(mul*=0x100)){val+=this[offset+i]*mul;}mul*=0x80;if(val>=mul)val-=Math.pow(2,8*byteLength);return val;};Buffer.prototype.readIntBE=function readIntBE(offset,byteLength,noAssert){offset=offset|0;byteLength=byteLength|0;if(!noAssert)checkOffset(offset,byteLength,this.length);var i=byteLength;var mul=1;var val=this[offset+--i];while(i>0&&(mul*=0x100)){val+=this[offset+--i]*mul;}mul*=0x80;if(val>=mul)val-=Math.pow(2,8*byteLength);return val;};Buffer.prototype.readInt8=function readInt8(offset,noAssert){if(!noAssert)checkOffset(offset,1,this.length);if(!(this[offset]&0x80))return this[offset];return (0xff-this[offset]+1)*-1;};Buffer.prototype.readInt16LE=function readInt16LE(offset,noAssert){if(!noAssert)checkOffset(offset,2,this.length);var val=this[offset]|this[offset+1]<<8;return val&0x8000?val|0xFFFF0000:val;};Buffer.prototype.readInt16BE=function readInt16BE(offset,noAssert){if(!noAssert)checkOffset(offset,2,this.length);var val=this[offset+1]|this[offset]<<8;return val&0x8000?val|0xFFFF0000:val;};Buffer.prototype.readInt32LE=function readInt32LE(offset,noAssert){if(!noAssert)checkOffset(offset,4,this.length);return this[offset]|this[offset+1]<<8|this[offset+2]<<16|this[offset+3]<<24;};Buffer.prototype.readInt32BE=function readInt32BE(offset,noAssert){if(!noAssert)checkOffset(offset,4,this.length);return this[offset]<<24|this[offset+1]<<16|this[offset+2]<<8|this[offset+3];};Buffer.prototype.readFloatLE=function readFloatLE(offset,noAssert){if(!noAssert)checkOffset(offset,4,this.length);return read(this,offset,true,23,4);};Buffer.prototype.readFloatBE=function readFloatBE(offset,noAssert){if(!noAssert)checkOffset(offset,4,this.length);return read(this,offset,false,23,4);};Buffer.prototype.readDoubleLE=function readDoubleLE(offset,noAssert){if(!noAssert)checkOffset(offset,8,this.length);return read(this,offset,true,52,8);};Buffer.prototype.readDoubleBE=function readDoubleBE(offset,noAssert){if(!noAssert)checkOffset(offset,8,this.length);return read(this,offset,false,52,8);};function checkInt(buf,value,offset,ext,max,min){if(!internalIsBuffer(buf))throw new TypeError('"buffer" argument must be a Buffer instance');if(value>max||value<min)throw new RangeError('"value" argument is out of bounds');if(offset+ext>buf.length)throw new RangeError('Index out of range');}Buffer.prototype.writeUIntLE=function writeUIntLE(value,offset,byteLength,noAssert){value=+value;offset=offset|0;byteLength=byteLength|0;if(!noAssert){var maxBytes=Math.pow(2,8*byteLength)-1;checkInt(this,value,offset,byteLength,maxBytes,0);}var mul=1;var i=0;this[offset]=value&0xFF;while(++i<byteLength&&(mul*=0x100)){this[offset+i]=value/mul&0xFF;}return offset+byteLength;};Buffer.prototype.writeUIntBE=function writeUIntBE(value,offset,byteLength,noAssert){value=+value;offset=offset|0;byteLength=byteLength|0;if(!noAssert){var maxBytes=Math.pow(2,8*byteLength)-1;checkInt(this,value,offset,byteLength,maxBytes,0);}var i=byteLength-1;var mul=1;this[offset+i]=value&0xFF;while(--i>=0&&(mul*=0x100)){this[offset+i]=value/mul&0xFF;}return offset+byteLength;};Buffer.prototype.writeUInt8=function writeUInt8(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,1,0xff,0);if(!Buffer.TYPED_ARRAY_SUPPORT)value=Math.floor(value);this[offset]=value&0xff;return offset+1;};function objectWriteUInt16(buf,value,offset,littleEndian){if(value<0)value=0xffff+value+1;for(var i=0,j=Math.min(buf.length-offset,2);i<j;++i){buf[offset+i]=(value&0xff<<8*(littleEndian?i:1-i))>>>(littleEndian?i:1-i)*8;}}Buffer.prototype.writeUInt16LE=function writeUInt16LE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,2,0xffff,0);if(Buffer.TYPED_ARRAY_SUPPORT){this[offset]=value&0xff;this[offset+1]=value>>>8;}else{objectWriteUInt16(this,value,offset,true);}return offset+2;};Buffer.prototype.writeUInt16BE=function writeUInt16BE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,2,0xffff,0);if(Buffer.TYPED_ARRAY_SUPPORT){this[offset]=value>>>8;this[offset+1]=value&0xff;}else{objectWriteUInt16(this,value,offset,false);}return offset+2;};function objectWriteUInt32(buf,value,offset,littleEndian){if(value<0)value=0xffffffff+value+1;for(var i=0,j=Math.min(buf.length-offset,4);i<j;++i){buf[offset+i]=value>>>(littleEndian?i:3-i)*8&0xff;}}Buffer.prototype.writeUInt32LE=function writeUInt32LE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,4,0xffffffff,0);if(Buffer.TYPED_ARRAY_SUPPORT){this[offset+3]=value>>>24;this[offset+2]=value>>>16;this[offset+1]=value>>>8;this[offset]=value&0xff;}else{objectWriteUInt32(this,value,offset,true);}return offset+4;};Buffer.prototype.writeUInt32BE=function writeUInt32BE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,4,0xffffffff,0);if(Buffer.TYPED_ARRAY_SUPPORT){this[offset]=value>>>24;this[offset+1]=value>>>16;this[offset+2]=value>>>8;this[offset+3]=value&0xff;}else{objectWriteUInt32(this,value,offset,false);}return offset+4;};Buffer.prototype.writeIntLE=function writeIntLE(value,offset,byteLength,noAssert){value=+value;offset=offset|0;if(!noAssert){var limit=Math.pow(2,8*byteLength-1);checkInt(this,value,offset,byteLength,limit-1,-limit);}var i=0;var mul=1;var sub=0;this[offset]=value&0xFF;while(++i<byteLength&&(mul*=0x100)){if(value<0&&sub===0&&this[offset+i-1]!==0){sub=1;}this[offset+i]=(value/mul>>0)-sub&0xFF;}return offset+byteLength;};Buffer.prototype.writeIntBE=function writeIntBE(value,offset,byteLength,noAssert){value=+value;offset=offset|0;if(!noAssert){var limit=Math.pow(2,8*byteLength-1);checkInt(this,value,offset,byteLength,limit-1,-limit);}var i=byteLength-1;var mul=1;var sub=0;this[offset+i]=value&0xFF;while(--i>=0&&(mul*=0x100)){if(value<0&&sub===0&&this[offset+i+1]!==0){sub=1;}this[offset+i]=(value/mul>>0)-sub&0xFF;}return offset+byteLength;};Buffer.prototype.writeInt8=function writeInt8(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,1,0x7f,-0x80);if(!Buffer.TYPED_ARRAY_SUPPORT)value=Math.floor(value);if(value<0)value=0xff+value+1;this[offset]=value&0xff;return offset+1;};Buffer.prototype.writeInt16LE=function writeInt16LE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,2,0x7fff,-0x8000);if(Buffer.TYPED_ARRAY_SUPPORT){this[offset]=value&0xff;this[offset+1]=value>>>8;}else{objectWriteUInt16(this,value,offset,true);}return offset+2;};Buffer.prototype.writeInt16BE=function writeInt16BE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,2,0x7fff,-0x8000);if(Buffer.TYPED_ARRAY_SUPPORT){this[offset]=value>>>8;this[offset+1]=value&0xff;}else{objectWriteUInt16(this,value,offset,false);}return offset+2;};Buffer.prototype.writeInt32LE=function writeInt32LE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,4,0x7fffffff,-0x80000000);if(Buffer.TYPED_ARRAY_SUPPORT){this[offset]=value&0xff;this[offset+1]=value>>>8;this[offset+2]=value>>>16;this[offset+3]=value>>>24;}else{objectWriteUInt32(this,value,offset,true);}return offset+4;};Buffer.prototype.writeInt32BE=function writeInt32BE(value,offset,noAssert){value=+value;offset=offset|0;if(!noAssert)checkInt(this,value,offset,4,0x7fffffff,-0x80000000);if(value<0)value=0xffffffff+value+1;if(Buffer.TYPED_ARRAY_SUPPORT){this[offset]=value>>>24;this[offset+1]=value>>>16;this[offset+2]=value>>>8;this[offset+3]=value&0xff;}else{objectWriteUInt32(this,value,offset,false);}return offset+4;};function checkIEEE754(buf,value,offset,ext,max,min){if(offset+ext>buf.length)throw new RangeError('Index out of range');if(offset<0)throw new RangeError('Index out of range');}function writeFloat(buf,value,offset,littleEndian,noAssert){if(!noAssert){checkIEEE754(buf,value,offset,4);}write(buf,value,offset,littleEndian,23,4);return offset+4;}Buffer.prototype.writeFloatLE=function writeFloatLE(value,offset,noAssert){return writeFloat(this,value,offset,true,noAssert);};Buffer.prototype.writeFloatBE=function writeFloatBE(value,offset,noAssert){return writeFloat(this,value,offset,false,noAssert);};function writeDouble(buf,value,offset,littleEndian,noAssert){if(!noAssert){checkIEEE754(buf,value,offset,8);}write(buf,value,offset,littleEndian,52,8);return offset+8;}Buffer.prototype.writeDoubleLE=function writeDoubleLE(value,offset,noAssert){return writeDouble(this,value,offset,true,noAssert);};Buffer.prototype.writeDoubleBE=function writeDoubleBE(value,offset,noAssert){return writeDouble(this,value,offset,false,noAssert);};// copy(targetBuffer, targetStart=0, sourceStart=0, sourceEnd=buffer.length)
  Buffer.prototype.copy=function copy(target,targetStart,start,end){if(!start)start=0;if(!end&&end!==0)end=this.length;if(targetStart>=target.length)targetStart=target.length;if(!targetStart)targetStart=0;if(end>0&&end<start)end=start;// Copy 0 bytes; we're done
  if(end===start)return 0;if(target.length===0||this.length===0)return 0;// Fatal error conditions
  if(targetStart<0){throw new RangeError('targetStart out of bounds');}if(start<0||start>=this.length)throw new RangeError('sourceStart out of bounds');if(end<0)throw new RangeError('sourceEnd out of bounds');// Are we oob?
  if(end>this.length)end=this.length;if(target.length-targetStart<end-start){end=target.length-targetStart+start;}var len=end-start;var i;if(this===target&&start<targetStart&&targetStart<end){// descending copy from end
  for(i=len-1;i>=0;--i){target[i+targetStart]=this[i+start];}}else if(len<1000||!Buffer.TYPED_ARRAY_SUPPORT){// ascending copy from start
  for(i=0;i<len;++i){target[i+targetStart]=this[i+start];}}else{Uint8Array.prototype.set.call(target,this.subarray(start,start+len),targetStart);}return len;};// Usage:
  //    buffer.fill(number[, offset[, end]])
  //    buffer.fill(buffer[, offset[, end]])
  //    buffer.fill(string[, offset[, end]][, encoding])
  Buffer.prototype.fill=function fill(val,start,end,encoding){// Handle string cases:
  if(typeof val==='string'){if(typeof start==='string'){encoding=start;start=0;end=this.length;}else if(typeof end==='string'){encoding=end;end=this.length;}if(val.length===1){var code=val.charCodeAt(0);if(code<256){val=code;}}if(encoding!==undefined&&typeof encoding!=='string'){throw new TypeError('encoding must be a string');}if(typeof encoding==='string'&&!Buffer.isEncoding(encoding)){throw new TypeError('Unknown encoding: '+encoding);}}else if(typeof val==='number'){val=val&255;}// Invalid ranges are not set to a default, so can range check early.
  if(start<0||this.length<start||this.length<end){throw new RangeError('Out of range index');}if(end<=start){return this;}start=start>>>0;end=end===undefined?this.length:end>>>0;if(!val)val=0;var i;if(typeof val==='number'){for(i=start;i<end;++i){this[i]=val;}}else{var bytes=internalIsBuffer(val)?val:utf8ToBytes(new Buffer(val,encoding).toString());var len=bytes.length;for(i=0;i<end-start;++i){this[i+start]=bytes[i%len];}}return this;};// HELPER FUNCTIONS
  // ================
  var INVALID_BASE64_RE=/[^+\/0-9A-Za-z-_]/g;function base64clean(str){// Node strips out invalid characters like \n and \t from the string, base64-js does not
  str=stringtrim(str).replace(INVALID_BASE64_RE,'');// Node converts strings with length < 2 to ''
  if(str.length<2)return '';// Node allows for non-padded base64 strings (missing trailing ===), base64-js does not
  while(str.length%4!==0){str=str+'=';}return str;}function stringtrim(str){if(str.trim)return str.trim();return str.replace(/^\s+|\s+$/g,'');}function toHex(n){if(n<16)return '0'+n.toString(16);return n.toString(16);}function utf8ToBytes(string,units){units=units||Infinity;var codePoint;var length=string.length;var leadSurrogate=null;var bytes=[];for(var i=0;i<length;++i){codePoint=string.charCodeAt(i);// is surrogate component
  if(codePoint>0xD7FF&&codePoint<0xE000){// last char was a lead
  if(!leadSurrogate){// no lead yet
  if(codePoint>0xDBFF){// unexpected trail
  if((units-=3)>-1)bytes.push(0xEF,0xBF,0xBD);continue;}else if(i+1===length){// unpaired lead
  if((units-=3)>-1)bytes.push(0xEF,0xBF,0xBD);continue;}// valid lead
  leadSurrogate=codePoint;continue;}// 2 leads in a row
  if(codePoint<0xDC00){if((units-=3)>-1)bytes.push(0xEF,0xBF,0xBD);leadSurrogate=codePoint;continue;}// valid surrogate pair
  codePoint=(leadSurrogate-0xD800<<10|codePoint-0xDC00)+0x10000;}else if(leadSurrogate){// valid bmp char, but last char was a lead
  if((units-=3)>-1)bytes.push(0xEF,0xBF,0xBD);}leadSurrogate=null;// encode utf8
  if(codePoint<0x80){if((units-=1)<0)break;bytes.push(codePoint);}else if(codePoint<0x800){if((units-=2)<0)break;bytes.push(codePoint>>0x6|0xC0,codePoint&0x3F|0x80);}else if(codePoint<0x10000){if((units-=3)<0)break;bytes.push(codePoint>>0xC|0xE0,codePoint>>0x6&0x3F|0x80,codePoint&0x3F|0x80);}else if(codePoint<0x110000){if((units-=4)<0)break;bytes.push(codePoint>>0x12|0xF0,codePoint>>0xC&0x3F|0x80,codePoint>>0x6&0x3F|0x80,codePoint&0x3F|0x80);}else{throw new Error('Invalid code point');}}return bytes;}function asciiToBytes(str){var byteArray=[];for(var i=0;i<str.length;++i){// Node's code seems to be doing this and not & 0x7F..
  byteArray.push(str.charCodeAt(i)&0xFF);}return byteArray;}function utf16leToBytes(str,units){var c,hi,lo;var byteArray=[];for(var i=0;i<str.length;++i){if((units-=2)<0)break;c=str.charCodeAt(i);hi=c>>8;lo=c%256;byteArray.push(lo);byteArray.push(hi);}return byteArray;}function base64ToBytes(str){return toByteArray(base64clean(str));}function blitBuffer(src,dst,offset,length){for(var i=0;i<length;++i){if(i+offset>=dst.length||i>=src.length)break;dst[i+offset]=src[i];}return i;}function isnan(val){return val!==val;// eslint-disable-line no-self-compare
  }// the following is from is-buffer, also by Feross Aboukhadijeh and with same lisence
  // The _isBuffer check is for Safari 5-7 support, because it's missing
  // Object.prototype.constructor. Remove this eventually
  function isBuffer(obj){return obj!=null&&(!!obj._isBuffer||isFastBuffer(obj)||isSlowBuffer(obj));}function isFastBuffer(obj){return !!obj.constructor&&typeof obj.constructor.isBuffer==='function'&&obj.constructor.isBuffer(obj);}// For Node v0.10 support. Remove this eventually.
  function isSlowBuffer(obj){return typeof obj.readFloatLE==='function'&&typeof obj.slice==='function'&&isFastBuffer(obj.slice(0,0));}// shim for using process in browser
  if(typeof commonjsGlobal.setTimeout==='function');if(typeof commonjsGlobal.clearTimeout==='function');// from https://github.com/kumavis/browser-process-hrtime/blob/master/index.js
  var performance=commonjsGlobal.performance||{};var performanceNow=performance.now||performance.mozNow||performance.msNow||performance.oNow||performance.webkitNow||function(){return new Date().getTime();};var inherits;if(typeof Object.create==='function'){inherits=function inherits(ctor,superCtor){// implementation from standard node.js 'util' module
  ctor.super_=superCtor;ctor.prototype=Object.create(superCtor.prototype,{constructor:{value:ctor,enumerable:false,writable:true,configurable:true}});};}else{inherits=function inherits(ctor,superCtor){ctor.super_=superCtor;var TempCtor=function TempCtor(){};TempCtor.prototype=superCtor.prototype;ctor.prototype=new TempCtor();ctor.prototype.constructor=ctor;};}var inherits$1=inherits;// Copyright Joyent, Inc. and other Node contributors.
  /**
  	 * Echos the value of a value. Trys to print the value out
  	 * in the best way possible given the different types.
  	 *
  	 * @param {Object} obj The object to print out.
  	 * @param {Object} opts Optional options object that alters the output.
  	 */ /* legacy: obj, showHidden, depth, colors*/function inspect(obj,opts){// default options
  var ctx={seen:[],stylize:stylizeNoColor};// legacy...
  if(arguments.length>=3)ctx.depth=arguments[2];if(arguments.length>=4)ctx.colors=arguments[3];if(isBoolean(opts)){// legacy...
  ctx.showHidden=opts;}else if(opts){// got an "options" object
  _extend(ctx,opts);}// set default options
  if(isUndefined(ctx.showHidden))ctx.showHidden=false;if(isUndefined(ctx.depth))ctx.depth=2;if(isUndefined(ctx.colors))ctx.colors=false;if(isUndefined(ctx.customInspect))ctx.customInspect=true;if(ctx.colors)ctx.stylize=stylizeWithColor;return formatValue(ctx,obj,ctx.depth);}// http://en.wikipedia.org/wiki/ANSI_escape_code#graphics
  inspect.colors={'bold':[1,22],'italic':[3,23],'underline':[4,24],'inverse':[7,27],'white':[37,39],'grey':[90,39],'black':[30,39],'blue':[34,39],'cyan':[36,39],'green':[32,39],'magenta':[35,39],'red':[31,39],'yellow':[33,39]};// Don't use 'blue' not visible on cmd.exe
  inspect.styles={'special':'cyan','number':'yellow','boolean':'yellow','undefined':'grey','null':'bold','string':'green','date':'magenta',// "name": intentionally not styling
  'regexp':'red'};function stylizeWithColor(str,styleType){var style=inspect.styles[styleType];if(style){return '\u001b['+inspect.colors[style][0]+'m'+str+'\u001b['+inspect.colors[style][1]+'m';}else{return str;}}function stylizeNoColor(str,styleType){return str;}function arrayToHash(array){var hash={};array.forEach(function(val,idx){hash[val]=true;});return hash;}function formatValue(ctx,value,recurseTimes){// Provide a hook for user-specified inspect functions.
  // Check that value is an object with an inspect function on it
  if(ctx.customInspect&&value&&isFunction(value.inspect)&&// Filter out the util module, it's inspect function is special
  value.inspect!==inspect&&// Also filter out any prototype objects using the circular check.
  !(value.constructor&&value.constructor.prototype===value)){var ret=value.inspect(recurseTimes,ctx);if(!isString(ret)){ret=formatValue(ctx,ret,recurseTimes);}return ret;}// Primitive types cannot have properties
  var primitive=formatPrimitive(ctx,value);if(primitive){return primitive;}// Look up the keys of the object.
  var keys=Object.keys(value);var visibleKeys=arrayToHash(keys);if(ctx.showHidden){keys=Object.getOwnPropertyNames(value);}// IE doesn't make error fields non-enumerable
  // http://msdn.microsoft.com/en-us/library/ie/dww52sbt(v=vs.94).aspx
  if(isError(value)&&(keys.indexOf('message')>=0||keys.indexOf('description')>=0)){return formatError(value);}// Some type of object without properties can be shortcutted.
  if(keys.length===0){if(isFunction(value)){var name=value.name?': '+value.name:'';return ctx.stylize('[Function'+name+']','special');}if(isRegExp(value)){return ctx.stylize(RegExp.prototype.toString.call(value),'regexp');}if(isDate(value)){return ctx.stylize(Date.prototype.toString.call(value),'date');}if(isError(value)){return formatError(value);}}var base='',array=false,braces=['{','}'];// Make Array say that they are Array
  if(isArray$1(value)){array=true;braces=['[',']'];}// Make functions say that they are functions
  if(isFunction(value)){var n=value.name?': '+value.name:'';base=' [Function'+n+']';}// Make RegExps say that they are RegExps
  if(isRegExp(value)){base=' '+RegExp.prototype.toString.call(value);}// Make dates with properties first say the date
  if(isDate(value)){base=' '+Date.prototype.toUTCString.call(value);}// Make error with message first say the error
  if(isError(value)){base=' '+formatError(value);}if(keys.length===0&&(!array||value.length==0)){return braces[0]+base+braces[1];}if(recurseTimes<0){if(isRegExp(value)){return ctx.stylize(RegExp.prototype.toString.call(value),'regexp');}else{return ctx.stylize('[Object]','special');}}ctx.seen.push(value);var output;if(array){output=formatArray(ctx,value,recurseTimes,visibleKeys,keys);}else{output=keys.map(function(key){return formatProperty(ctx,value,recurseTimes,visibleKeys,key,array);});}ctx.seen.pop();return reduceToSingleString(output,base,braces);}function formatPrimitive(ctx,value){if(isUndefined(value))return ctx.stylize('undefined','undefined');if(isString(value)){var simple='\''+JSON.stringify(value).replace(/^"|"$/g,'').replace(/'/g,"\\'").replace(/\\"/g,'"')+'\'';return ctx.stylize(simple,'string');}if(isNumber(value))return ctx.stylize(''+value,'number');if(isBoolean(value))return ctx.stylize(''+value,'boolean');// For some reason typeof null is "object", so special case here.
  if(isNull(value))return ctx.stylize('null','null');}function formatError(value){return '['+Error.prototype.toString.call(value)+']';}function formatArray(ctx,value,recurseTimes,visibleKeys,keys){var output=[];for(var i=0,l=value.length;i<l;++i){if(hasOwnProperty(value,String(i))){output.push(formatProperty(ctx,value,recurseTimes,visibleKeys,String(i),true));}else{output.push('');}}keys.forEach(function(key){if(!key.match(/^\d+$/)){output.push(formatProperty(ctx,value,recurseTimes,visibleKeys,key,true));}});return output;}function formatProperty(ctx,value,recurseTimes,visibleKeys,key,array){var name,str,desc;desc=Object.getOwnPropertyDescriptor(value,key)||{value:value[key]};if(desc.get){if(desc.set){str=ctx.stylize('[Getter/Setter]','special');}else{str=ctx.stylize('[Getter]','special');}}else{if(desc.set){str=ctx.stylize('[Setter]','special');}}if(!hasOwnProperty(visibleKeys,key)){name='['+key+']';}if(!str){if(ctx.seen.indexOf(desc.value)<0){if(isNull(recurseTimes)){str=formatValue(ctx,desc.value,null);}else{str=formatValue(ctx,desc.value,recurseTimes-1);}if(str.indexOf('\n')>-1){if(array){str=str.split('\n').map(function(line){return '  '+line;}).join('\n').substr(2);}else{str='\n'+str.split('\n').map(function(line){return '   '+line;}).join('\n');}}}else{str=ctx.stylize('[Circular]','special');}}if(isUndefined(name)){if(array&&key.match(/^\d+$/)){return str;}name=JSON.stringify(''+key);if(name.match(/^"([a-zA-Z_][a-zA-Z_0-9]*)"$/)){name=name.substr(1,name.length-2);name=ctx.stylize(name,'name');}else{name=name.replace(/'/g,"\\'").replace(/\\"/g,'"').replace(/(^"|"$)/g,"'");name=ctx.stylize(name,'string');}}return name+': '+str;}function reduceToSingleString(output,base,braces){var length=output.reduce(function(prev,cur){if(cur.indexOf('\n')>=0);return prev+cur.replace(/\u001b\[\d\d?m/g,'').length+1;},0);if(length>60){return braces[0]+(base===''?'':base+'\n ')+' '+output.join(',\n  ')+' '+braces[1];}return braces[0]+base+' '+output.join(', ')+' '+braces[1];}// NOTE: These type checking functions intentionally don't use `instanceof`
  // because it is fragile and can be easily faked with `Object.create()`.
  function isArray$1(ar){return Array.isArray(ar);}function isBoolean(arg){return typeof arg==='boolean';}function isNull(arg){return arg===null;}function isNumber(arg){return typeof arg==='number';}function isString(arg){return typeof arg==='string';}function isUndefined(arg){return arg===void 0;}function isRegExp(re){return isObject(re)&&objectToString(re)==='[object RegExp]';}function isObject(arg){return typeof arg==='object'&&arg!==null;}function isDate(d){return isObject(d)&&objectToString(d)==='[object Date]';}function isError(e){return isObject(e)&&(objectToString(e)==='[object Error]'||e instanceof Error);}function isFunction(arg){return typeof arg==='function';}function isPrimitive(arg){return arg===null||typeof arg==='boolean'||typeof arg==='number'||typeof arg==='string'||typeof arg==='symbol'||// ES6 symbol
  typeof arg==='undefined';}function objectToString(o){return Object.prototype.toString.call(o);}function _extend(origin,add){// Don't do anything if add isn't an object
  if(!add||!isObject(add))return origin;var keys=Object.keys(add);var i=keys.length;while(i--){origin[keys[i]]=add[keys[i]];}return origin;}function hasOwnProperty(obj,prop){return Object.prototype.hasOwnProperty.call(obj,prop);}function compare(a,b){if(a===b){return 0;}var x=a.length;var y=b.length;for(var i=0,len=Math.min(x,y);i<len;++i){if(a[i]!==b[i]){x=a[i];y=b[i];break;}}if(x<y){return -1;}if(y<x){return 1;}return 0;}var hasOwn=Object.prototype.hasOwnProperty;var objectKeys=Object.keys||function(obj){var keys=[];for(var key in obj){if(hasOwn.call(obj,key))keys.push(key);}return keys;};var pSlice=Array.prototype.slice;var _functionsHaveNames;function functionsHaveNames(){if(typeof _functionsHaveNames!=='undefined'){return _functionsHaveNames;}return _functionsHaveNames=function(){return function foo(){}.name==='foo';}();}function pToString(obj){return Object.prototype.toString.call(obj);}function isView(arrbuf){if(isBuffer(arrbuf)){return false;}if(typeof commonjsGlobal.ArrayBuffer!=='function'){return false;}if(typeof ArrayBuffer.isView==='function'){return ArrayBuffer.isView(arrbuf);}if(!arrbuf){return false;}if(arrbuf instanceof DataView){return true;}if(arrbuf.buffer&&arrbuf.buffer instanceof ArrayBuffer){return true;}return false;}// 1. The assert module provides functions that throw
  // AssertionError's when particular conditions are not met. The
  // assert module must conform to the following interface.
  function assert(value,message){if(!value)fail(value,true,message,'==',ok);}// 2. The AssertionError is defined in assert.
  // new assert.AssertionError({ message: message,
  //                             actual: actual,
  //                             expected: expected })
  var regex=/\s*function\s+([^\(\s]*)\s*/;// based on https://github.com/ljharb/function.prototype.name/blob/adeeeec8bfcc6068b187d7d9fb3d5bb1d3a30899/implementation.js
  function getName(func){if(!isFunction(func)){return;}if(functionsHaveNames()){return func.name;}var str=func.toString();var match=str.match(regex);return match&&match[1];}assert.AssertionError=AssertionError;function AssertionError(options){this.name='AssertionError';this.actual=options.actual;this.expected=options.expected;this.operator=options.operator;if(options.message){this.message=options.message;this.generatedMessage=false;}else{this.message=getMessage(this);this.generatedMessage=true;}var stackStartFunction=options.stackStartFunction||fail;if(Error.captureStackTrace){Error.captureStackTrace(this,stackStartFunction);}else{// non v8 browsers so we can have a stacktrace
  var err=new Error();if(err.stack){var out=err.stack;// try to strip useless frames
  var fn_name=getName(stackStartFunction);var idx=out.indexOf('\n'+fn_name);if(idx>=0){// once we have located the function frame
  // we need to strip out everything before it (and its line)
  var next_line=out.indexOf('\n',idx+1);out=out.substring(next_line+1);}this.stack=out;}}}// assert.AssertionError instanceof Error
  inherits$1(AssertionError,Error);function truncate(s,n){if(typeof s==='string'){return s.length<n?s:s.slice(0,n);}else{return s;}}function inspect$1(something){if(functionsHaveNames()||!isFunction(something)){return inspect(something);}var rawname=getName(something);var name=rawname?': '+rawname:'';return '[Function'+name+']';}function getMessage(self){return truncate(inspect$1(self.actual),128)+' '+self.operator+' '+truncate(inspect$1(self.expected),128);}// At present only the three keys mentioned above are used and
  // understood by the spec. Implementations or sub modules can pass
  // other keys to the AssertionError's constructor - they will be
  // ignored.
  // 3. All of the following functions must throw an AssertionError
  // when a corresponding condition is not met, with a message that
  // may be undefined if not provided.  All assertion methods provide
  // both the actual and expected values to the assertion error for
  // display purposes.
  function fail(actual,expected,message,operator,stackStartFunction){throw new AssertionError({message:message,actual:actual,expected:expected,operator:operator,stackStartFunction:stackStartFunction});}// EXTENSION! allows for well behaved errors defined elsewhere.
  assert.fail=fail;// 4. Pure assertion tests whether a value is truthy, as determined
  // by !!guard.
  // assert.ok(guard, message_opt);
  // This statement is equivalent to assert.equal(true, !!guard,
  // message_opt);. To test strictly for the value true, use
  // assert.strictEqual(true, guard, message_opt);.
  function ok(value,message){if(!value)fail(value,true,message,'==',ok);}assert.ok=ok;// 5. The equality assertion tests shallow, coercive equality with
  // ==.
  // assert.equal(actual, expected, message_opt);
  assert.equal=equal;function equal(actual,expected,message){if(actual!=expected)fail(actual,expected,message,'==',equal);}// 6. The non-equality assertion tests for whether two objects are not equal
  // with != assert.notEqual(actual, expected, message_opt);
  assert.notEqual=notEqual;function notEqual(actual,expected,message){if(actual==expected){fail(actual,expected,message,'!=',notEqual);}}// 7. The equivalence assertion tests a deep equality relation.
  // assert.deepEqual(actual, expected, message_opt);
  assert.deepEqual=deepEqual;function deepEqual(actual,expected,message){if(!_deepEqual(actual,expected,false)){fail(actual,expected,message,'deepEqual',deepEqual);}}assert.deepStrictEqual=deepStrictEqual;function deepStrictEqual(actual,expected,message){if(!_deepEqual(actual,expected,true)){fail(actual,expected,message,'deepStrictEqual',deepStrictEqual);}}function _deepEqual(actual,expected,strict,memos){// 7.1. All identical values are equivalent, as determined by ===.
  if(actual===expected){return true;}else if(isBuffer(actual)&&isBuffer(expected)){return compare(actual,expected)===0;// 7.2. If the expected value is a Date object, the actual value is
  // equivalent if it is also a Date object that refers to the same time.
  }else if(isDate(actual)&&isDate(expected)){return actual.getTime()===expected.getTime();// 7.3 If the expected value is a RegExp object, the actual value is
  // equivalent if it is also a RegExp object with the same source and
  // properties (`global`, `multiline`, `lastIndex`, `ignoreCase`).
  }else if(isRegExp(actual)&&isRegExp(expected)){return actual.source===expected.source&&actual.global===expected.global&&actual.multiline===expected.multiline&&actual.lastIndex===expected.lastIndex&&actual.ignoreCase===expected.ignoreCase;// 7.4. Other pairs that do not both pass typeof value == 'object',
  // equivalence is determined by ==.
  }else if((actual===null||typeof actual!=='object')&&(expected===null||typeof expected!=='object')){return strict?actual===expected:actual==expected;// If both values are instances of typed arrays, wrap their underlying
  // ArrayBuffers in a Buffer each to increase performance
  // This optimization requires the arrays to have the same type as checked by
  // Object.prototype.toString (aka pToString). Never perform binary
  // comparisons for Float*Arrays, though, since e.g. +0 === -0 but their
  // bit patterns are not identical.
  }else if(isView(actual)&&isView(expected)&&pToString(actual)===pToString(expected)&&!(actual instanceof Float32Array||actual instanceof Float64Array)){return compare(new Uint8Array(actual.buffer),new Uint8Array(expected.buffer))===0;// 7.5 For all other Object pairs, including Array objects, equivalence is
  // determined by having the same number of owned properties (as verified
  // with Object.prototype.hasOwnProperty.call), the same set of keys
  // (although not necessarily the same order), equivalent values for every
  // corresponding key, and an identical 'prototype' property. Note: this
  // accounts for both named and indexed properties on Arrays.
  }else if(isBuffer(actual)!==isBuffer(expected)){return false;}else{memos=memos||{actual:[],expected:[]};var actualIndex=memos.actual.indexOf(actual);if(actualIndex!==-1){if(actualIndex===memos.expected.indexOf(expected)){return true;}}memos.actual.push(actual);memos.expected.push(expected);return objEquiv(actual,expected,strict,memos);}}function isArguments(object){return Object.prototype.toString.call(object)=='[object Arguments]';}function objEquiv(a,b,strict,actualVisitedObjects){if(a===null||a===undefined||b===null||b===undefined)return false;// if one is a primitive, the other must be same
  if(isPrimitive(a)||isPrimitive(b))return a===b;if(strict&&Object.getPrototypeOf(a)!==Object.getPrototypeOf(b))return false;var aIsArgs=isArguments(a);var bIsArgs=isArguments(b);if(aIsArgs&&!bIsArgs||!aIsArgs&&bIsArgs)return false;if(aIsArgs){a=pSlice.call(a);b=pSlice.call(b);return _deepEqual(a,b,strict);}var ka=objectKeys(a);var kb=objectKeys(b);var key,i;// having the same number of owned properties (keys incorporates
  // hasOwnProperty)
  if(ka.length!==kb.length)return false;//the same set of keys (although not necessarily the same order),
  ka.sort();kb.sort();//~~~cheap key test
  for(i=ka.length-1;i>=0;i--){if(ka[i]!==kb[i])return false;}//equivalent values for every corresponding key, and
  //~~~possibly expensive deep test
  for(i=ka.length-1;i>=0;i--){key=ka[i];if(!_deepEqual(a[key],b[key],strict,actualVisitedObjects))return false;}return true;}// 8. The non-equivalence assertion tests for any deep inequality.
  // assert.notDeepEqual(actual, expected, message_opt);
  assert.notDeepEqual=notDeepEqual;function notDeepEqual(actual,expected,message){if(_deepEqual(actual,expected,false)){fail(actual,expected,message,'notDeepEqual',notDeepEqual);}}assert.notDeepStrictEqual=notDeepStrictEqual;function notDeepStrictEqual(actual,expected,message){if(_deepEqual(actual,expected,true)){fail(actual,expected,message,'notDeepStrictEqual',notDeepStrictEqual);}}// 9. The strict equality assertion tests strict equality, as determined by ===.
  // assert.strictEqual(actual, expected, message_opt);
  assert.strictEqual=strictEqual;function strictEqual(actual,expected,message){if(actual!==expected){fail(actual,expected,message,'===',strictEqual);}}// 10. The strict non-equality assertion tests for strict inequality, as
  // determined by !==.  assert.notStrictEqual(actual, expected, message_opt);
  assert.notStrictEqual=notStrictEqual;function notStrictEqual(actual,expected,message){if(actual===expected){fail(actual,expected,message,'!==',notStrictEqual);}}function expectedException(actual,expected){if(!actual||!expected){return false;}if(Object.prototype.toString.call(expected)=='[object RegExp]'){return expected.test(actual);}try{if(actual instanceof expected){return true;}}catch(e){// Ignore.  The instanceof check doesn't work for arrow functions.
  }if(Error.isPrototypeOf(expected)){return false;}return expected.call({},actual)===true;}function _tryBlock(block){var error;try{block();}catch(e){error=e;}return error;}function _throws(shouldThrow,block,expected,message){var actual;if(typeof block!=='function'){throw new TypeError('"block" argument must be a function');}if(typeof expected==='string'){message=expected;expected=null;}actual=_tryBlock(block);message=(expected&&expected.name?' ('+expected.name+').':'.')+(message?' '+message:'.');if(shouldThrow&&!actual){fail(actual,expected,'Missing expected exception'+message);}var userProvidedMessage=typeof message==='string';var isUnwantedException=!shouldThrow&&isError(actual);var isUnexpectedException=!shouldThrow&&actual&&!expected;if(isUnwantedException&&userProvidedMessage&&expectedException(actual,expected)||isUnexpectedException){fail(actual,expected,'Got unwanted exception'+message);}if(shouldThrow&&actual&&expected&&!expectedException(actual,expected)||!shouldThrow&&actual){throw actual;}}// 11. Expected to throw an error:
  // assert.throws(block, Error_opt, message_opt);
  assert.throws=throws;function throws(block,/*optional*/error,/*optional*/message){_throws(true,block,error,message);}// EXTENSION! This is annoying to write outside this module.
  assert.doesNotThrow=doesNotThrow;function doesNotThrow(block,/*optional*/error,/*optional*/message){_throws(false,block,error,message);}assert.ifError=ifError;function ifError(err){if(err)throw err;}var sourceMap=createCommonjsModule(function(module,exports){(function webpackUniversalModuleDefinition(root,factory){module.exports=factory(fs,path$1);})(typeof self!=='undefined'?self:this,function(__WEBPACK_EXTERNAL_MODULE_10__,__WEBPACK_EXTERNAL_MODULE_11__){return(/******/function(modules){// webpackBootstrap
  /******/ // The module cache
  /******/var installedModules={};/******/ /******/ // The require function
  /******/function __webpack_require__(moduleId){/******/ /******/ // Check if module is in cache
  /******/if(installedModules[moduleId]){/******/return installedModules[moduleId].exports;/******/}/******/ // Create a new module (and put it into the cache)
  /******/var module=installedModules[moduleId]={/******/i:moduleId,/******/l:false,/******/exports:{}/******/};/******/ /******/ // Execute the module function
  /******/modules[moduleId].call(module.exports,module,module.exports,__webpack_require__);/******/ /******/ // Flag the module as loaded
  /******/module.l=true;/******/ /******/ // Return the exports of the module
  /******/return module.exports;/******/}/******/ /******/ /******/ // expose the modules object (__webpack_modules__)
  /******/__webpack_require__.m=modules;/******/ /******/ // expose the module cache
  /******/__webpack_require__.c=installedModules;/******/ /******/ // define getter function for harmony exports
  /******/__webpack_require__.d=function(exports,name,getter){/******/if(!__webpack_require__.o(exports,name)){/******/Object.defineProperty(exports,name,{/******/configurable:false,/******/enumerable:true,/******/get:getter/******/});/******/}/******/};/******/ /******/ // getDefaultExport function for compatibility with non-harmony modules
  /******/__webpack_require__.n=function(module){/******/var getter=module&&module.__esModule?/******/function getDefault(){return module['default'];}:/******/function getModuleExports(){return module;};/******/__webpack_require__.d(getter,'a',getter);/******/return getter;/******/};/******/ /******/ // Object.prototype.hasOwnProperty.call
  /******/__webpack_require__.o=function(object,property){return Object.prototype.hasOwnProperty.call(object,property);};/******/ /******/ // __webpack_public_path__
  /******/__webpack_require__.p="";/******/ /******/ // Load entry module and return exports
  /******/return __webpack_require__(__webpack_require__.s=5);/******/}(/************************************************************************/ /******/[/* 0 */ /***/function(module,exports){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */ /**
  	 * This is a helper function for getting values from parameter/options
  	 * objects.
  	 *
  	 * @param args The object we are extracting values from
  	 * @param name The name of the property we are getting.
  	 * @param defaultValue An optional value to return if the property is missing
  	 * from the object. If this is not specified and the property is missing, an
  	 * error will be thrown.
  	 */function getArg(aArgs,aName,aDefaultValue){if(aName in aArgs){return aArgs[aName];}else if(arguments.length===3){return aDefaultValue;}throw new Error('"'+aName+'" is a required argument.');}exports.getArg=getArg;const urlRegexp=/^(?:([\w+\-.]+):)?\/\/(?:(\w+:\w+)@)?([\w.-]*)(?::(\d+))?(.*)$/;const dataUrlRegexp=/^data:.+\,.+$/;function urlParse(aUrl){const match=aUrl.match(urlRegexp);if(!match){return null;}return {scheme:match[1],auth:match[2],host:match[3],port:match[4],path:match[5]};}exports.urlParse=urlParse;function urlGenerate(aParsedUrl){let url="";if(aParsedUrl.scheme){url+=aParsedUrl.scheme+":";}url+="//";if(aParsedUrl.auth){url+=aParsedUrl.auth+"@";}if(aParsedUrl.host){url+=aParsedUrl.host;}if(aParsedUrl.port){url+=":"+aParsedUrl.port;}if(aParsedUrl.path){url+=aParsedUrl.path;}return url;}exports.urlGenerate=urlGenerate;const MAX_CACHED_INPUTS=32;/**
  	 * Takes some function `f(input) -> result` and returns a memoized version of
  	 * `f`.
  	 *
  	 * We keep at most `MAX_CACHED_INPUTS` memoized results of `f` alive. The
  	 * memoization is a dumb-simple, linear least-recently-used cache.
  	 */function lruMemoize(f){const cache=[];return function(input){for(let i=0;i<cache.length;i++){if(cache[i].input===input){const temp=cache[0];cache[0]=cache[i];cache[i]=temp;return cache[0].result;}}const result=f(input);cache.unshift({input,result});if(cache.length>MAX_CACHED_INPUTS){cache.pop();}return result;};}/**
  	 * Normalizes a path, or the path portion of a URL:
  	 *
  	 * - Replaces consecutive slashes with one slash.
  	 * - Removes unnecessary '.' parts.
  	 * - Removes unnecessary '<dir>/..' parts.
  	 *
  	 * Based on code in the Node.js 'path' core module.
  	 *
  	 * @param aPath The path or url to normalize.
  	 */const normalize=lruMemoize(function normalize(aPath){let path=aPath;const url=urlParse(aPath);if(url){if(!url.path){return aPath;}path=url.path;}const isAbsolute=exports.isAbsolute(path);// Split the path into parts between `/` characters. This is much faster than
  // using `.split(/\/+/g)`.
  const parts=[];let start=0;let i=0;while(true){start=i;i=path.indexOf("/",start);if(i===-1){parts.push(path.slice(start));break;}else{parts.push(path.slice(start,i));while(i<path.length&&path[i]==="/"){i++;}}}let up=0;for(i=parts.length-1;i>=0;i--){const part=parts[i];if(part==="."){parts.splice(i,1);}else if(part===".."){up++;}else if(up>0){if(part===""){// The first part is blank if the path is absolute. Trying to go
  // above the root is a no-op. Therefore we can remove all '..' parts
  // directly after the root.
  parts.splice(i+1,up);up=0;}else{parts.splice(i,2);up--;}}}path=parts.join("/");if(path===""){path=isAbsolute?"/":".";}if(url){url.path=path;return urlGenerate(url);}return path;});exports.normalize=normalize;/**
  	 * Joins two paths/URLs.
  	 *
  	 * @param aRoot The root path or URL.
  	 * @param aPath The path or URL to be joined with the root.
  	 *
  	 * - If aPath is a URL or a data URI, aPath is returned, unless aPath is a
  	 *   scheme-relative URL: Then the scheme of aRoot, if any, is prepended
  	 *   first.
  	 * - Otherwise aPath is a path. If aRoot is a URL, then its path portion
  	 *   is updated with the result and aRoot is returned. Otherwise the result
  	 *   is returned.
  	 *   - If aPath is absolute, the result is aPath.
  	 *   - Otherwise the two paths are joined with a slash.
  	 * - Joining for example 'http://' and 'www.example.com' is also supported.
  	 */function join(aRoot,aPath){if(aRoot===""){aRoot=".";}if(aPath===""){aPath=".";}const aPathUrl=urlParse(aPath);const aRootUrl=urlParse(aRoot);if(aRootUrl){aRoot=aRootUrl.path||"/";}// `join(foo, '//www.example.org')`
  if(aPathUrl&&!aPathUrl.scheme){if(aRootUrl){aPathUrl.scheme=aRootUrl.scheme;}return urlGenerate(aPathUrl);}if(aPathUrl||aPath.match(dataUrlRegexp)){return aPath;}// `join('http://', 'www.example.com')`
  if(aRootUrl&&!aRootUrl.host&&!aRootUrl.path){aRootUrl.host=aPath;return urlGenerate(aRootUrl);}const joined=aPath.charAt(0)==="/"?aPath:normalize(aRoot.replace(/\/+$/,"")+"/"+aPath);if(aRootUrl){aRootUrl.path=joined;return urlGenerate(aRootUrl);}return joined;}exports.join=join;exports.isAbsolute=function(aPath){return aPath.charAt(0)==="/"||urlRegexp.test(aPath);};/**
  	 * Make a path relative to a URL or another path.
  	 *
  	 * @param aRoot The root path or URL.
  	 * @param aPath The path or URL to be made relative to aRoot.
  	 */function relative(aRoot,aPath){if(aRoot===""){aRoot=".";}aRoot=aRoot.replace(/\/$/,"");// It is possible for the path to be above the root. In this case, simply
  // checking whether the root is a prefix of the path won't work. Instead, we
  // need to remove components from the root one by one, until either we find
  // a prefix that fits, or we run out of components to remove.
  let level=0;while(aPath.indexOf(aRoot+"/")!==0){const index=aRoot.lastIndexOf("/");if(index<0){return aPath;}// If the only part of the root that is left is the scheme (i.e. http://,
  // file:///, etc.), one or more slashes (/), or simply nothing at all, we
  // have exhausted all components, so the path is not relative to the root.
  aRoot=aRoot.slice(0,index);if(aRoot.match(/^([^\/]+:\/)?\/*$/)){return aPath;}++level;}// Make sure we add a "../" for each component we removed from the root.
  return Array(level+1).join("../")+aPath.substr(aRoot.length+1);}exports.relative=relative;const supportsNullProto=function(){const obj=Object.create(null);return !("__proto__"in obj);}();function identity(s){return s;}/**
  	 * Because behavior goes wacky when you set `__proto__` on objects, we
  	 * have to prefix all the strings in our set with an arbitrary character.
  	 *
  	 * See https://github.com/mozilla/source-map/pull/31 and
  	 * https://github.com/mozilla/source-map/issues/30
  	 *
  	 * @param String aStr
  	 */function toSetString(aStr){if(isProtoString(aStr)){return "$"+aStr;}return aStr;}exports.toSetString=supportsNullProto?identity:toSetString;function fromSetString(aStr){if(isProtoString(aStr)){return aStr.slice(1);}return aStr;}exports.fromSetString=supportsNullProto?identity:fromSetString;function isProtoString(s){if(!s){return false;}const length=s.length;if(length<9/* "__proto__".length */){return false;}/* eslint-disable no-multi-spaces */if(s.charCodeAt(length-1)!==95/* '_' */||s.charCodeAt(length-2)!==95/* '_' */||s.charCodeAt(length-3)!==111/* 'o' */||s.charCodeAt(length-4)!==116/* 't' */||s.charCodeAt(length-5)!==111/* 'o' */||s.charCodeAt(length-6)!==114/* 'r' */||s.charCodeAt(length-7)!==112/* 'p' */||s.charCodeAt(length-8)!==95/* '_' */||s.charCodeAt(length-9)!==95/* '_' */){return false;}/* eslint-enable no-multi-spaces */for(let i=length-10;i>=0;i--){if(s.charCodeAt(i)!==36/* '$' */){return false;}}return true;}/**
  	 * Comparator between two mappings where the original positions are compared.
  	 *
  	 * Optionally pass in `true` as `onlyCompareGenerated` to consider two
  	 * mappings with the same original source/line/column, but different generated
  	 * line and column the same. Useful when searching for a mapping with a
  	 * stubbed out mapping.
  	 */function compareByOriginalPositions(mappingA,mappingB,onlyCompareOriginal){let cmp=strcmp(mappingA.source,mappingB.source);if(cmp!==0){return cmp;}cmp=mappingA.originalLine-mappingB.originalLine;if(cmp!==0){return cmp;}cmp=mappingA.originalColumn-mappingB.originalColumn;if(cmp!==0||onlyCompareOriginal){return cmp;}cmp=mappingA.generatedColumn-mappingB.generatedColumn;if(cmp!==0){return cmp;}cmp=mappingA.generatedLine-mappingB.generatedLine;if(cmp!==0){return cmp;}return strcmp(mappingA.name,mappingB.name);}exports.compareByOriginalPositions=compareByOriginalPositions;/**
  	 * Comparator between two mappings with deflated source and name indices where
  	 * the generated positions are compared.
  	 *
  	 * Optionally pass in `true` as `onlyCompareGenerated` to consider two
  	 * mappings with the same generated line and column, but different
  	 * source/name/original line and column the same. Useful when searching for a
  	 * mapping with a stubbed out mapping.
  	 */function compareByGeneratedPositionsDeflated(mappingA,mappingB,onlyCompareGenerated){let cmp=mappingA.generatedLine-mappingB.generatedLine;if(cmp!==0){return cmp;}cmp=mappingA.generatedColumn-mappingB.generatedColumn;if(cmp!==0||onlyCompareGenerated){return cmp;}cmp=strcmp(mappingA.source,mappingB.source);if(cmp!==0){return cmp;}cmp=mappingA.originalLine-mappingB.originalLine;if(cmp!==0){return cmp;}cmp=mappingA.originalColumn-mappingB.originalColumn;if(cmp!==0){return cmp;}return strcmp(mappingA.name,mappingB.name);}exports.compareByGeneratedPositionsDeflated=compareByGeneratedPositionsDeflated;function strcmp(aStr1,aStr2){if(aStr1===aStr2){return 0;}if(aStr1===null){return 1;// aStr2 !== null
  }if(aStr2===null){return -1;// aStr1 !== null
  }if(aStr1>aStr2){return 1;}return -1;}/**
  	 * Comparator between two mappings with inflated source and name strings where
  	 * the generated positions are compared.
  	 */function compareByGeneratedPositionsInflated(mappingA,mappingB){let cmp=mappingA.generatedLine-mappingB.generatedLine;if(cmp!==0){return cmp;}cmp=mappingA.generatedColumn-mappingB.generatedColumn;if(cmp!==0){return cmp;}cmp=strcmp(mappingA.source,mappingB.source);if(cmp!==0){return cmp;}cmp=mappingA.originalLine-mappingB.originalLine;if(cmp!==0){return cmp;}cmp=mappingA.originalColumn-mappingB.originalColumn;if(cmp!==0){return cmp;}return strcmp(mappingA.name,mappingB.name);}exports.compareByGeneratedPositionsInflated=compareByGeneratedPositionsInflated;/**
  	 * Strip any JSON XSSI avoidance prefix from the string (as documented
  	 * in the source maps specification), and then parse the string as
  	 * JSON.
  	 */function parseSourceMapInput(str){return JSON.parse(str.replace(/^\)]}'[^\n]*\n/,""));}exports.parseSourceMapInput=parseSourceMapInput;/**
  	 * Compute the URL of a source given the the source root, the source's
  	 * URL, and the source map's URL.
  	 */function computeSourceURL(sourceRoot,sourceURL,sourceMapURL){sourceURL=sourceURL||"";if(sourceRoot){// This follows what Chrome does.
  if(sourceRoot[sourceRoot.length-1]!=="/"&&sourceURL[0]!=="/"){sourceRoot+="/";}// The spec says:
  //   Line 4: An optional source root, useful for relocating source
  //   files on a server or removing repeated values in the
  //   “sources” entry.  This value is prepended to the individual
  //   entries in the “source” field.
  sourceURL=sourceRoot+sourceURL;}// Historically, SourceMapConsumer did not take the sourceMapURL as
  // a parameter.  This mode is still somewhat supported, which is why
  // this code block is conditional.  However, it's preferable to pass
  // the source map URL to SourceMapConsumer, so that this function
  // can implement the source URL resolution algorithm as outlined in
  // the spec.  This block is basically the equivalent of:
  //    new URL(sourceURL, sourceMapURL).toString()
  // ... except it avoids using URL, which wasn't available in the
  // older releases of node still supported by this library.
  //
  // The spec says:
  //   If the sources are not absolute URLs after prepending of the
  //   “sourceRoot”, the sources are resolved relative to the
  //   SourceMap (like resolving script src in a html document).
  if(sourceMapURL){const parsed=urlParse(sourceMapURL);if(!parsed){throw new Error("sourceMapURL could not be parsed");}if(parsed.path){// Strip the last path component, but keep the "/".
  const index=parsed.path.lastIndexOf("/");if(index>=0){parsed.path=parsed.path.substring(0,index+1);}}sourceURL=join(urlGenerate(parsed),sourceURL);}return normalize(sourceURL);}exports.computeSourceURL=computeSourceURL;/***/},/* 1 */ /***/function(module,exports,__webpack_require__){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */const base64VLQ=__webpack_require__(2);const util=__webpack_require__(0);const ArraySet=__webpack_require__(3).ArraySet;const MappingList=__webpack_require__(7).MappingList;/**
  	 * An instance of the SourceMapGenerator represents a source map which is
  	 * being built incrementally. You may pass an object with the following
  	 * properties:
  	 *
  	 *   - file: The filename of the generated source.
  	 *   - sourceRoot: A root for all relative URLs in this source map.
  	 */class SourceMapGenerator{constructor(aArgs){if(!aArgs){aArgs={};}this._file=util.getArg(aArgs,"file",null);this._sourceRoot=util.getArg(aArgs,"sourceRoot",null);this._skipValidation=util.getArg(aArgs,"skipValidation",false);this._sources=new ArraySet();this._names=new ArraySet();this._mappings=new MappingList();this._sourcesContents=null;}/**
  	   * Creates a new SourceMapGenerator based on a SourceMapConsumer
  	   *
  	   * @param aSourceMapConsumer The SourceMap.
  	   */static fromSourceMap(aSourceMapConsumer){const sourceRoot=aSourceMapConsumer.sourceRoot;const generator=new SourceMapGenerator({file:aSourceMapConsumer.file,sourceRoot});aSourceMapConsumer.eachMapping(function(mapping){const newMapping={generated:{line:mapping.generatedLine,column:mapping.generatedColumn}};if(mapping.source!=null){newMapping.source=mapping.source;if(sourceRoot!=null){newMapping.source=util.relative(sourceRoot,newMapping.source);}newMapping.original={line:mapping.originalLine,column:mapping.originalColumn};if(mapping.name!=null){newMapping.name=mapping.name;}}generator.addMapping(newMapping);});aSourceMapConsumer.sources.forEach(function(sourceFile){let sourceRelative=sourceFile;if(sourceRoot!==null){sourceRelative=util.relative(sourceRoot,sourceFile);}if(!generator._sources.has(sourceRelative)){generator._sources.add(sourceRelative);}const content=aSourceMapConsumer.sourceContentFor(sourceFile);if(content!=null){generator.setSourceContent(sourceFile,content);}});return generator;}/**
  	   * Add a single mapping from original source line and column to the generated
  	   * source's line and column for this source map being created. The mapping
  	   * object should have the following properties:
  	   *
  	   *   - generated: An object with the generated line and column positions.
  	   *   - original: An object with the original line and column positions.
  	   *   - source: The original source file (relative to the sourceRoot).
  	   *   - name: An optional original token name for this mapping.
  	   */addMapping(aArgs){const generated=util.getArg(aArgs,"generated");const original=util.getArg(aArgs,"original",null);let source=util.getArg(aArgs,"source",null);let name=util.getArg(aArgs,"name",null);if(!this._skipValidation){this._validateMapping(generated,original,source,name);}if(source!=null){source=String(source);if(!this._sources.has(source)){this._sources.add(source);}}if(name!=null){name=String(name);if(!this._names.has(name)){this._names.add(name);}}this._mappings.add({generatedLine:generated.line,generatedColumn:generated.column,originalLine:original!=null&&original.line,originalColumn:original!=null&&original.column,source,name});}/**
  	   * Set the source content for a source file.
  	   */setSourceContent(aSourceFile,aSourceContent){let source=aSourceFile;if(this._sourceRoot!=null){source=util.relative(this._sourceRoot,source);}if(aSourceContent!=null){// Add the source content to the _sourcesContents map.
  // Create a new _sourcesContents map if the property is null.
  if(!this._sourcesContents){this._sourcesContents=Object.create(null);}this._sourcesContents[util.toSetString(source)]=aSourceContent;}else if(this._sourcesContents){// Remove the source file from the _sourcesContents map.
  // If the _sourcesContents map is empty, set the property to null.
  delete this._sourcesContents[util.toSetString(source)];if(Object.keys(this._sourcesContents).length===0){this._sourcesContents=null;}}}/**
  	   * Applies the mappings of a sub-source-map for a specific source file to the
  	   * source map being generated. Each mapping to the supplied source file is
  	   * rewritten using the supplied source map. Note: The resolution for the
  	   * resulting mappings is the minimium of this map and the supplied map.
  	   *
  	   * @param aSourceMapConsumer The source map to be applied.
  	   * @param aSourceFile Optional. The filename of the source file.
  	   *        If omitted, SourceMapConsumer's file property will be used.
  	   * @param aSourceMapPath Optional. The dirname of the path to the source map
  	   *        to be applied. If relative, it is relative to the SourceMapConsumer.
  	   *        This parameter is needed when the two source maps aren't in the same
  	   *        directory, and the source map to be applied contains relative source
  	   *        paths. If so, those relative source paths need to be rewritten
  	   *        relative to the SourceMapGenerator.
  	   */applySourceMap(aSourceMapConsumer,aSourceFile,aSourceMapPath){let sourceFile=aSourceFile;// If aSourceFile is omitted, we will use the file property of the SourceMap
  if(aSourceFile==null){if(aSourceMapConsumer.file==null){throw new Error("SourceMapGenerator.prototype.applySourceMap requires either an explicit source file, "+'or the source map\'s "file" property. Both were omitted.');}sourceFile=aSourceMapConsumer.file;}const sourceRoot=this._sourceRoot;// Make "sourceFile" relative if an absolute Url is passed.
  if(sourceRoot!=null){sourceFile=util.relative(sourceRoot,sourceFile);}// Applying the SourceMap can add and remove items from the sources and
  // the names array.
  const newSources=this._mappings.toArray().length>0?new ArraySet():this._sources;const newNames=new ArraySet();// Find mappings for the "sourceFile"
  this._mappings.unsortedForEach(function(mapping){if(mapping.source===sourceFile&&mapping.originalLine!=null){// Check if it can be mapped by the source map, then update the mapping.
  const original=aSourceMapConsumer.originalPositionFor({line:mapping.originalLine,column:mapping.originalColumn});if(original.source!=null){// Copy mapping
  mapping.source=original.source;if(aSourceMapPath!=null){mapping.source=util.join(aSourceMapPath,mapping.source);}if(sourceRoot!=null){mapping.source=util.relative(sourceRoot,mapping.source);}mapping.originalLine=original.line;mapping.originalColumn=original.column;if(original.name!=null){mapping.name=original.name;}}}const source=mapping.source;if(source!=null&&!newSources.has(source)){newSources.add(source);}const name=mapping.name;if(name!=null&&!newNames.has(name)){newNames.add(name);}},this);this._sources=newSources;this._names=newNames;// Copy sourcesContents of applied map.
  aSourceMapConsumer.sources.forEach(function(srcFile){const content=aSourceMapConsumer.sourceContentFor(srcFile);if(content!=null){if(aSourceMapPath!=null){srcFile=util.join(aSourceMapPath,srcFile);}if(sourceRoot!=null){srcFile=util.relative(sourceRoot,srcFile);}this.setSourceContent(srcFile,content);}},this);}/**
  	   * A mapping can have one of the three levels of data:
  	   *
  	   *   1. Just the generated position.
  	   *   2. The Generated position, original position, and original source.
  	   *   3. Generated and original position, original source, as well as a name
  	   *      token.
  	   *
  	   * To maintain consistency, we validate that any new mapping being added falls
  	   * in to one of these categories.
  	   */_validateMapping(aGenerated,aOriginal,aSource,aName){// When aOriginal is truthy but has empty values for .line and .column,
  // it is most likely a programmer error. In this case we throw a very
  // specific error message to try to guide them the right way.
  // For example: https://github.com/Polymer/polymer-bundler/pull/519
  if(aOriginal&&typeof aOriginal.line!=="number"&&typeof aOriginal.column!=="number"){throw new Error("original.line and original.column are not numbers -- you probably meant to omit "+"the original mapping entirely and only map the generated position. If so, pass "+"null for the original mapping instead of an object with empty or null values.");}if(aGenerated&&"line"in aGenerated&&"column"in aGenerated&&aGenerated.line>0&&aGenerated.column>=0&&!aOriginal&&!aSource&&!aName);else if(aGenerated&&"line"in aGenerated&&"column"in aGenerated&&aOriginal&&"line"in aOriginal&&"column"in aOriginal&&aGenerated.line>0&&aGenerated.column>=0&&aOriginal.line>0&&aOriginal.column>=0&&aSource);else{throw new Error("Invalid mapping: "+JSON.stringify({generated:aGenerated,source:aSource,original:aOriginal,name:aName}));}}/**
  	   * Serialize the accumulated mappings in to the stream of base 64 VLQs
  	   * specified by the source map format.
  	   */_serializeMappings(){let previousGeneratedColumn=0;let previousGeneratedLine=1;let previousOriginalColumn=0;let previousOriginalLine=0;let previousName=0;let previousSource=0;let result="";let next;let mapping;let nameIdx;let sourceIdx;const mappings=this._mappings.toArray();for(let i=0,len=mappings.length;i<len;i++){mapping=mappings[i];next="";if(mapping.generatedLine!==previousGeneratedLine){previousGeneratedColumn=0;while(mapping.generatedLine!==previousGeneratedLine){next+=";";previousGeneratedLine++;}}else if(i>0){if(!util.compareByGeneratedPositionsInflated(mapping,mappings[i-1])){continue;}next+=",";}next+=base64VLQ.encode(mapping.generatedColumn-previousGeneratedColumn);previousGeneratedColumn=mapping.generatedColumn;if(mapping.source!=null){sourceIdx=this._sources.indexOf(mapping.source);next+=base64VLQ.encode(sourceIdx-previousSource);previousSource=sourceIdx;// lines are stored 0-based in SourceMap spec version 3
  next+=base64VLQ.encode(mapping.originalLine-1-previousOriginalLine);previousOriginalLine=mapping.originalLine-1;next+=base64VLQ.encode(mapping.originalColumn-previousOriginalColumn);previousOriginalColumn=mapping.originalColumn;if(mapping.name!=null){nameIdx=this._names.indexOf(mapping.name);next+=base64VLQ.encode(nameIdx-previousName);previousName=nameIdx;}}result+=next;}return result;}_generateSourcesContent(aSources,aSourceRoot){return aSources.map(function(source){if(!this._sourcesContents){return null;}if(aSourceRoot!=null){source=util.relative(aSourceRoot,source);}const key=util.toSetString(source);return Object.prototype.hasOwnProperty.call(this._sourcesContents,key)?this._sourcesContents[key]:null;},this);}/**
  	   * Externalize the source map.
  	   */toJSON(){const map={version:this._version,sources:this._sources.toArray(),names:this._names.toArray(),mappings:this._serializeMappings()};if(this._file!=null){map.file=this._file;}if(this._sourceRoot!=null){map.sourceRoot=this._sourceRoot;}if(this._sourcesContents){map.sourcesContent=this._generateSourcesContent(map.sources,map.sourceRoot);}return map;}/**
  	   * Render the source map being generated to a string.
  	   */toString(){return JSON.stringify(this.toJSON());}}SourceMapGenerator.prototype._version=3;exports.SourceMapGenerator=SourceMapGenerator;/***/},/* 2 */ /***/function(module,exports,__webpack_require__){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 *
  	 * Based on the Base 64 VLQ implementation in Closure Compiler:
  	 * https://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/debugging/sourcemap/Base64VLQ.java
  	 *
  	 * Copyright 2011 The Closure Compiler Authors. All rights reserved.
  	 * Redistribution and use in source and binary forms, with or without
  	 * modification, are permitted provided that the following conditions are
  	 * met:
  	 *
  	 *  * Redistributions of source code must retain the above copyright
  	 *    notice, this list of conditions and the following disclaimer.
  	 *  * Redistributions in binary form must reproduce the above
  	 *    copyright notice, this list of conditions and the following
  	 *    disclaimer in the documentation and/or other materials provided
  	 *    with the distribution.
  	 *  * Neither the name of Google Inc. nor the names of its
  	 *    contributors may be used to endorse or promote products derived
  	 *    from this software without specific prior written permission.
  	 *
  	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  	 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  	 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  	 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  	 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  	 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  	 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  	 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  	 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  	 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  	 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  	 */const base64=__webpack_require__(6);// A single base 64 digit can contain 6 bits of data. For the base 64 variable
  // length quantities we use in the source map spec, the first bit is the sign,
  // the next four bits are the actual value, and the 6th bit is the
  // continuation bit. The continuation bit tells us whether there are more
  // digits in this value following this digit.
  //
  //   Continuation
  //   |    Sign
  //   |    |
  //   V    V
  //   101011
  const VLQ_BASE_SHIFT=5;// binary: 100000
  const VLQ_BASE=1<<VLQ_BASE_SHIFT;// binary: 011111
  const VLQ_BASE_MASK=VLQ_BASE-1;// binary: 100000
  const VLQ_CONTINUATION_BIT=VLQ_BASE;/**
  	 * Converts from a two-complement value to a value where the sign bit is
  	 * placed in the least significant bit.  For example, as decimals:
  	 *   1 becomes 2 (10 binary), -1 becomes 3 (11 binary)
  	 *   2 becomes 4 (100 binary), -2 becomes 5 (101 binary)
  	 */function toVLQSigned(aValue){return aValue<0?(-aValue<<1)+1:(aValue<<1)+0;}/**
  	 * Returns the base 64 VLQ encoded value.
  	 */exports.encode=function base64VLQ_encode(aValue){let encoded="";let digit;let vlq=toVLQSigned(aValue);do{digit=vlq&VLQ_BASE_MASK;vlq>>>=VLQ_BASE_SHIFT;if(vlq>0){// There are still more digits in this value, so we must make sure the
  // continuation bit is marked.
  digit|=VLQ_CONTINUATION_BIT;}encoded+=base64.encode(digit);}while(vlq>0);return encoded;};/***/},/* 3 */ /***/function(module,exports){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */ /**
  	 * A data structure which is a combination of an array and a set. Adding a new
  	 * member is O(1), testing for membership is O(1), and finding the index of an
  	 * element is O(1). Removing elements from the set is not supported. Only
  	 * strings are supported for membership.
  	 */class ArraySet{constructor(){this._array=[];this._set=new Map();}/**
  	   * Static method for creating ArraySet instances from an existing array.
  	   */static fromArray(aArray,aAllowDuplicates){const set=new ArraySet();for(let i=0,len=aArray.length;i<len;i++){set.add(aArray[i],aAllowDuplicates);}return set;}/**
  	   * Return how many unique items are in this ArraySet. If duplicates have been
  	   * added, than those do not count towards the size.
  	   *
  	   * @returns Number
  	   */size(){return this._set.size;}/**
  	   * Add the given string to this set.
  	   *
  	   * @param String aStr
  	   */add(aStr,aAllowDuplicates){const isDuplicate=this.has(aStr);const idx=this._array.length;if(!isDuplicate||aAllowDuplicates){this._array.push(aStr);}if(!isDuplicate){this._set.set(aStr,idx);}}/**
  	   * Is the given string a member of this set?
  	   *
  	   * @param String aStr
  	   */has(aStr){return this._set.has(aStr);}/**
  	   * What is the index of the given string in the array?
  	   *
  	   * @param String aStr
  	   */indexOf(aStr){const idx=this._set.get(aStr);if(idx>=0){return idx;}throw new Error('"'+aStr+'" is not in the set.');}/**
  	   * What is the element at the given index?
  	   *
  	   * @param Number aIdx
  	   */at(aIdx){if(aIdx>=0&&aIdx<this._array.length){return this._array[aIdx];}throw new Error("No element indexed by "+aIdx);}/**
  	   * Returns the array representation of this set (which has the proper indices
  	   * indicated by indexOf). Note that this is a copy of the internal array used
  	   * for storing the members so that no one can mess with internal state.
  	   */toArray(){return this._array.slice();}}exports.ArraySet=ArraySet;/***/},/* 4 */ /***/function(module,exports,__webpack_require__){/* WEBPACK VAR INJECTION */(function(__dirname){if(typeof fetch==="function"){// Web version of reading a wasm file into an array buffer.
  let mappingsWasmUrl=null;module.exports=function readWasm(){if(typeof mappingsWasmUrl!=="string"){throw new Error("You must provide the URL of lib/mappings.wasm by calling "+"SourceMapConsumer.initialize({ 'lib/mappings.wasm': ... }) "+"before using SourceMapConsumer");}return fetch(mappingsWasmUrl).then(response=>response.arrayBuffer());};module.exports.initialize=url=>mappingsWasmUrl=url;}else{// Node version of reading a wasm file into an array buffer.
  const fs=__webpack_require__(10);const path=__webpack_require__(11);module.exports=function readWasm(){return new Promise((resolve,reject)=>{const wasmPath=path.join(__dirname,"mappings.wasm");fs.readFile(wasmPath,null,(error,data)=>{if(error){reject(error);return;}resolve(data.buffer);});});};module.exports.initialize=_=>{console.debug("SourceMapConsumer.initialize is a no-op when running in node.js");};}/* WEBPACK VAR INJECTION */}).call(exports,"/");/***/},/* 5 */ /***/function(module,exports,__webpack_require__){/*
  	 * Copyright 2009-2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE.txt or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */exports.SourceMapGenerator=__webpack_require__(1).SourceMapGenerator;exports.SourceMapConsumer=__webpack_require__(8).SourceMapConsumer;exports.SourceNode=__webpack_require__(13).SourceNode;/***/},/* 6 */ /***/function(module,exports){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */const intToCharMap="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".split("");/**
  	 * Encode an integer in the range of 0 to 63 to a single base 64 digit.
  	 */exports.encode=function(number){if(0<=number&&number<intToCharMap.length){return intToCharMap[number];}throw new TypeError("Must be between 0 and 63: "+number);};/***/},/* 7 */ /***/function(module,exports,__webpack_require__){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2014 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */const util=__webpack_require__(0);/**
  	 * Determine whether mappingB is after mappingA with respect to generated
  	 * position.
  	 */function generatedPositionAfter(mappingA,mappingB){// Optimized for most common case
  const lineA=mappingA.generatedLine;const lineB=mappingB.generatedLine;const columnA=mappingA.generatedColumn;const columnB=mappingB.generatedColumn;return lineB>lineA||lineB==lineA&&columnB>=columnA||util.compareByGeneratedPositionsInflated(mappingA,mappingB)<=0;}/**
  	 * A data structure to provide a sorted view of accumulated mappings in a
  	 * performance conscious manner. It trades a negligible overhead in general
  	 * case for a large speedup in case of mappings being added in order.
  	 */class MappingList{constructor(){this._array=[];this._sorted=true;// Serves as infimum
  this._last={generatedLine:-1,generatedColumn:0};}/**
  	   * Iterate through internal items. This method takes the same arguments that
  	   * `Array.prototype.forEach` takes.
  	   *
  	   * NOTE: The order of the mappings is NOT guaranteed.
  	   */unsortedForEach(aCallback,aThisArg){this._array.forEach(aCallback,aThisArg);}/**
  	   * Add the given source mapping.
  	   *
  	   * @param Object aMapping
  	   */add(aMapping){if(generatedPositionAfter(this._last,aMapping)){this._last=aMapping;this._array.push(aMapping);}else{this._sorted=false;this._array.push(aMapping);}}/**
  	   * Returns the flat, sorted array of mappings. The mappings are sorted by
  	   * generated position.
  	   *
  	   * WARNING: This method returns internal data without copying, for
  	   * performance. The return value must NOT be mutated, and should be treated as
  	   * an immutable borrow. If you want to take ownership, you must make your own
  	   * copy.
  	   */toArray(){if(!this._sorted){this._array.sort(util.compareByGeneratedPositionsInflated);this._sorted=true;}return this._array;}}exports.MappingList=MappingList;/***/},/* 8 */ /***/function(module,exports,__webpack_require__){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */const util=__webpack_require__(0);const binarySearch=__webpack_require__(9);const ArraySet=__webpack_require__(3).ArraySet;const base64VLQ=__webpack_require__(2);// eslint-disable-line no-unused-vars
  const readWasm=__webpack_require__(4);const wasm=__webpack_require__(12);const INTERNAL=Symbol("smcInternal");class SourceMapConsumer{constructor(aSourceMap,aSourceMapURL){// If the constructor was called by super(), just return Promise<this>.
  // Yes, this is a hack to retain the pre-existing API of the base-class
  // constructor also being an async factory function.
  if(aSourceMap==INTERNAL){return Promise.resolve(this);}return _factory(aSourceMap,aSourceMapURL);}static initialize(opts){readWasm.initialize(opts["lib/mappings.wasm"]);}static fromSourceMap(aSourceMap,aSourceMapURL){return _factoryBSM(aSourceMap,aSourceMapURL);}/**
  	   * Construct a new `SourceMapConsumer` from `rawSourceMap` and `sourceMapUrl`
  	   * (see the `SourceMapConsumer` constructor for details. Then, invoke the `async
  	   * function f(SourceMapConsumer) -> T` with the newly constructed consumer, wait
  	   * for `f` to complete, call `destroy` on the consumer, and return `f`'s return
  	   * value.
  	   *
  	   * You must not use the consumer after `f` completes!
  	   *
  	   * By using `with`, you do not have to remember to manually call `destroy` on
  	   * the consumer, since it will be called automatically once `f` completes.
  	   *
  	   * ```js
  	   * const xSquared = await SourceMapConsumer.with(
  	   *   myRawSourceMap,
  	   *   null,
  	   *   async function (consumer) {
  	   *     // Use `consumer` inside here and don't worry about remembering
  	   *     // to call `destroy`.
  	   *
  	   *     const x = await whatever(consumer);
  	   *     return x * x;
  	   *   }
  	   * );
  	   *
  	   * // You may not use that `consumer` anymore out here; it has
  	   * // been destroyed. But you can use `xSquared`.
  	   * console.log(xSquared);
  	   * ```
  	   */static with(rawSourceMap,sourceMapUrl,f){// Note: The `acorn` version that `webpack` currently depends on doesn't
  // support `async` functions, and the nodes that we support don't all have
  // `.finally`. Therefore, this is written a bit more convolutedly than it
  // should really be.
  let consumer=null;const promise=new SourceMapConsumer(rawSourceMap,sourceMapUrl);return promise.then(c=>{consumer=c;return f(c);}).then(x=>{if(consumer){consumer.destroy();}return x;},e=>{if(consumer){consumer.destroy();}throw e;});}/**
  	   * Parse the mappings in a string in to a data structure which we can easily
  	   * query (the ordered arrays in the `this.__generatedMappings` and
  	   * `this.__originalMappings` properties).
  	   */_parseMappings(aStr,aSourceRoot){throw new Error("Subclasses must implement _parseMappings");}/**
  	   * Iterate over each mapping between an original source/line/column and a
  	   * generated line/column in this source map.
  	   *
  	   * @param Function aCallback
  	   *        The function that is called with each mapping.
  	   * @param Object aContext
  	   *        Optional. If specified, this object will be the value of `this` every
  	   *        time that `aCallback` is called.
  	   * @param aOrder
  	   *        Either `SourceMapConsumer.GENERATED_ORDER` or
  	   *        `SourceMapConsumer.ORIGINAL_ORDER`. Specifies whether you want to
  	   *        iterate over the mappings sorted by the generated file's line/column
  	   *        order or the original's source/line/column order, respectively. Defaults to
  	   *        `SourceMapConsumer.GENERATED_ORDER`.
  	   */eachMapping(aCallback,aContext,aOrder){throw new Error("Subclasses must implement eachMapping");}/**
  	   * Returns all generated line and column information for the original source,
  	   * line, and column provided. If no column is provided, returns all mappings
  	   * corresponding to a either the line we are searching for or the next
  	   * closest line that has any mappings. Otherwise, returns all mappings
  	   * corresponding to the given line and either the column we are searching for
  	   * or the next closest column that has any offsets.
  	   *
  	   * The only argument is an object with the following properties:
  	   *
  	   *   - source: The filename of the original source.
  	   *   - line: The line number in the original source.  The line number is 1-based.
  	   *   - column: Optional. the column number in the original source.
  	   *    The column number is 0-based.
  	   *
  	   * and an array of objects is returned, each with the following properties:
  	   *
  	   *   - line: The line number in the generated source, or null.  The
  	   *    line number is 1-based.
  	   *   - column: The column number in the generated source, or null.
  	   *    The column number is 0-based.
  	   */allGeneratedPositionsFor(aArgs){throw new Error("Subclasses must implement allGeneratedPositionsFor");}destroy(){throw new Error("Subclasses must implement destroy");}}/**
  	 * The version of the source mapping spec that we are consuming.
  	 */SourceMapConsumer.prototype._version=3;SourceMapConsumer.GENERATED_ORDER=1;SourceMapConsumer.ORIGINAL_ORDER=2;SourceMapConsumer.GREATEST_LOWER_BOUND=1;SourceMapConsumer.LEAST_UPPER_BOUND=2;exports.SourceMapConsumer=SourceMapConsumer;/**
  	 * A BasicSourceMapConsumer instance represents a parsed source map which we can
  	 * query for information about the original file positions by giving it a file
  	 * position in the generated source.
  	 *
  	 * The first parameter is the raw source map (either as a JSON string, or
  	 * already parsed to an object). According to the spec, source maps have the
  	 * following attributes:
  	 *
  	 *   - version: Which version of the source map spec this map is following.
  	 *   - sources: An array of URLs to the original source files.
  	 *   - names: An array of identifiers which can be referenced by individual mappings.
  	 *   - sourceRoot: Optional. The URL root from which all sources are relative.
  	 *   - sourcesContent: Optional. An array of contents of the original source files.
  	 *   - mappings: A string of base64 VLQs which contain the actual mappings.
  	 *   - file: Optional. The generated file this source map is associated with.
  	 *
  	 * Here is an example source map, taken from the source map spec[0]:
  	 *
  	 *     {
  	 *       version : 3,
  	 *       file: "out.js",
  	 *       sourceRoot : "",
  	 *       sources: ["foo.js", "bar.js"],
  	 *       names: ["src", "maps", "are", "fun"],
  	 *       mappings: "AA,AB;;ABCDE;"
  	 *     }
  	 *
  	 * The second parameter, if given, is a string whose value is the URL
  	 * at which the source map was found.  This URL is used to compute the
  	 * sources array.
  	 *
  	 * [0]: https://docs.google.com/document/d/1U1RGAehQwRypUTovF1KRlpiOFze0b-_2gc6fAH0KY0k/edit?pli=1#
  	 */class BasicSourceMapConsumer extends SourceMapConsumer{constructor(aSourceMap,aSourceMapURL){return super(INTERNAL).then(that=>{let sourceMap=aSourceMap;if(typeof aSourceMap==="string"){sourceMap=util.parseSourceMapInput(aSourceMap);}const version=util.getArg(sourceMap,"version");let sources=util.getArg(sourceMap,"sources");// Sass 3.3 leaves out the 'names' array, so we deviate from the spec (which
  // requires the array) to play nice here.
  const names=util.getArg(sourceMap,"names",[]);let sourceRoot=util.getArg(sourceMap,"sourceRoot",null);const sourcesContent=util.getArg(sourceMap,"sourcesContent",null);const mappings=util.getArg(sourceMap,"mappings");const file=util.getArg(sourceMap,"file",null);// Once again, Sass deviates from the spec and supplies the version as a
  // string rather than a number, so we use loose equality checking here.
  if(version!=that._version){throw new Error("Unsupported version: "+version);}if(sourceRoot){sourceRoot=util.normalize(sourceRoot);}sources=sources.map(String)// Some source maps produce relative source paths like "./foo.js" instead of
  // "foo.js".  Normalize these first so that future comparisons will succeed.
  // See bugzil.la/1090768.
  .map(util.normalize)// Always ensure that absolute sources are internally stored relative to
  // the source root, if the source root is absolute. Not doing this would
  // be particularly problematic when the source root is a prefix of the
  // source (valid, but why??). See github issue #199 and bugzil.la/1188982.
  .map(function(source){return sourceRoot&&util.isAbsolute(sourceRoot)&&util.isAbsolute(source)?util.relative(sourceRoot,source):source;});// Pass `true` below to allow duplicate names and sources. While source maps
  // are intended to be compressed and deduplicated, the TypeScript compiler
  // sometimes generates source maps with duplicates in them. See Github issue
  // #72 and bugzil.la/889492.
  that._names=ArraySet.fromArray(names.map(String),true);that._sources=ArraySet.fromArray(sources,true);that._absoluteSources=that._sources.toArray().map(function(s){return util.computeSourceURL(sourceRoot,s,aSourceMapURL);});that.sourceRoot=sourceRoot;that.sourcesContent=sourcesContent;that._mappings=mappings;that._sourceMapURL=aSourceMapURL;that.file=file;that._computedColumnSpans=false;that._mappingsPtr=0;that._wasm=null;return wasm().then(w=>{that._wasm=w;return that;});});}/**
  	   * Utility function to find the index of a source.  Returns -1 if not
  	   * found.
  	   */_findSourceIndex(aSource){let relativeSource=aSource;if(this.sourceRoot!=null){relativeSource=util.relative(this.sourceRoot,relativeSource);}if(this._sources.has(relativeSource)){return this._sources.indexOf(relativeSource);}// Maybe aSource is an absolute URL as returned by |sources|.  In
  // this case we can't simply undo the transform.
  for(let i=0;i<this._absoluteSources.length;++i){if(this._absoluteSources[i]==aSource){return i;}}return -1;}/**
  	   * Create a BasicSourceMapConsumer from a SourceMapGenerator.
  	   *
  	   * @param SourceMapGenerator aSourceMap
  	   *        The source map that will be consumed.
  	   * @param String aSourceMapURL
  	   *        The URL at which the source map can be found (optional)
  	   * @returns BasicSourceMapConsumer
  	   */static fromSourceMap(aSourceMap,aSourceMapURL){return new BasicSourceMapConsumer(aSourceMap.toString());}get sources(){return this._absoluteSources.slice();}_getMappingsPtr(){if(this._mappingsPtr===0){this._parseMappings(this._mappings,this.sourceRoot);}return this._mappingsPtr;}/**
  	   * Parse the mappings in a string in to a data structure which we can easily
  	   * query (the ordered arrays in the `this.__generatedMappings` and
  	   * `this.__originalMappings` properties).
  	   */_parseMappings(aStr,aSourceRoot){const size=aStr.length;const mappingsBufPtr=this._wasm.exports.allocate_mappings(size);const mappingsBuf=new Uint8Array(this._wasm.exports.memory.buffer,mappingsBufPtr,size);for(let i=0;i<size;i++){mappingsBuf[i]=aStr.charCodeAt(i);}const mappingsPtr=this._wasm.exports.parse_mappings(mappingsBufPtr);if(!mappingsPtr){const error=this._wasm.exports.get_last_error();let msg=`Error parsing mappings (code ${error}): `;// XXX: keep these error codes in sync with `fitzgen/source-map-mappings`.
  switch(error){case 1:msg+="the mappings contained a negative line, column, source index, or name index";break;case 2:msg+="the mappings contained a number larger than 2**32";break;case 3:msg+="reached EOF while in the middle of parsing a VLQ";break;case 4:msg+="invalid base 64 character while parsing a VLQ";break;default:msg+="unknown error code";break;}throw new Error(msg);}this._mappingsPtr=mappingsPtr;}eachMapping(aCallback,aContext,aOrder){const context=aContext||null;const order=aOrder||SourceMapConsumer.GENERATED_ORDER;const sourceRoot=this.sourceRoot;this._wasm.withMappingCallback(mapping=>{if(mapping.source!==null){mapping.source=this._sources.at(mapping.source);mapping.source=util.computeSourceURL(sourceRoot,mapping.source,this._sourceMapURL);if(mapping.name!==null){mapping.name=this._names.at(mapping.name);}}aCallback.call(context,mapping);},()=>{switch(order){case SourceMapConsumer.GENERATED_ORDER:this._wasm.exports.by_generated_location(this._getMappingsPtr());break;case SourceMapConsumer.ORIGINAL_ORDER:this._wasm.exports.by_original_location(this._getMappingsPtr());break;default:throw new Error("Unknown order of iteration.");}});}allGeneratedPositionsFor(aArgs){let source=util.getArg(aArgs,"source");const originalLine=util.getArg(aArgs,"line");const originalColumn=aArgs.column||0;source=this._findSourceIndex(source);if(source<0){return [];}if(originalLine<1){throw new Error("Line numbers must be >= 1");}if(originalColumn<0){throw new Error("Column numbers must be >= 0");}const mappings=[];this._wasm.withMappingCallback(m=>{let lastColumn=m.lastGeneratedColumn;if(this._computedColumnSpans&&lastColumn===null){lastColumn=Infinity;}mappings.push({line:m.generatedLine,column:m.generatedColumn,lastColumn});},()=>{this._wasm.exports.all_generated_locations_for(this._getMappingsPtr(),source,originalLine-1,"column"in aArgs,originalColumn);});return mappings;}destroy(){if(this._mappingsPtr!==0){this._wasm.exports.free_mappings(this._mappingsPtr);this._mappingsPtr=0;}}/**
  	   * Compute the last column for each generated mapping. The last column is
  	   * inclusive.
  	   */computeColumnSpans(){if(this._computedColumnSpans){return;}this._wasm.exports.compute_column_spans(this._getMappingsPtr());this._computedColumnSpans=true;}/**
  	   * Returns the original source, line, and column information for the generated
  	   * source's line and column positions provided. The only argument is an object
  	   * with the following properties:
  	   *
  	   *   - line: The line number in the generated source.  The line number
  	   *     is 1-based.
  	   *   - column: The column number in the generated source.  The column
  	   *     number is 0-based.
  	   *   - bias: Either 'SourceMapConsumer.GREATEST_LOWER_BOUND' or
  	   *     'SourceMapConsumer.LEAST_UPPER_BOUND'. Specifies whether to return the
  	   *     closest element that is smaller than or greater than the one we are
  	   *     searching for, respectively, if the exact element cannot be found.
  	   *     Defaults to 'SourceMapConsumer.GREATEST_LOWER_BOUND'.
  	   *
  	   * and an object is returned with the following properties:
  	   *
  	   *   - source: The original source file, or null.
  	   *   - line: The line number in the original source, or null.  The
  	   *     line number is 1-based.
  	   *   - column: The column number in the original source, or null.  The
  	   *     column number is 0-based.
  	   *   - name: The original identifier, or null.
  	   */originalPositionFor(aArgs){const needle={generatedLine:util.getArg(aArgs,"line"),generatedColumn:util.getArg(aArgs,"column")};if(needle.generatedLine<1){throw new Error("Line numbers must be >= 1");}if(needle.generatedColumn<0){throw new Error("Column numbers must be >= 0");}let bias=util.getArg(aArgs,"bias",SourceMapConsumer.GREATEST_LOWER_BOUND);if(bias==null){bias=SourceMapConsumer.GREATEST_LOWER_BOUND;}let mapping;this._wasm.withMappingCallback(m=>mapping=m,()=>{this._wasm.exports.original_location_for(this._getMappingsPtr(),needle.generatedLine-1,needle.generatedColumn,bias);});if(mapping){if(mapping.generatedLine===needle.generatedLine){let source=util.getArg(mapping,"source",null);if(source!==null){source=this._sources.at(source);source=util.computeSourceURL(this.sourceRoot,source,this._sourceMapURL);}let name=util.getArg(mapping,"name",null);if(name!==null){name=this._names.at(name);}return {source,line:util.getArg(mapping,"originalLine",null),column:util.getArg(mapping,"originalColumn",null),name};}}return {source:null,line:null,column:null,name:null};}/**
  	   * Return true if we have the source content for every source in the source
  	   * map, false otherwise.
  	   */hasContentsOfAllSources(){if(!this.sourcesContent){return false;}return this.sourcesContent.length>=this._sources.size()&&!this.sourcesContent.some(function(sc){return sc==null;});}/**
  	   * Returns the original source content. The only argument is the url of the
  	   * original source file. Returns null if no original source content is
  	   * available.
  	   */sourceContentFor(aSource,nullOnMissing){if(!this.sourcesContent){return null;}const index=this._findSourceIndex(aSource);if(index>=0){return this.sourcesContent[index];}let relativeSource=aSource;if(this.sourceRoot!=null){relativeSource=util.relative(this.sourceRoot,relativeSource);}let url;if(this.sourceRoot!=null&&(url=util.urlParse(this.sourceRoot))){// XXX: file:// URIs and absolute paths lead to unexpected behavior for
  // many users. We can help them out when they expect file:// URIs to
  // behave like it would if they were running a local HTTP server. See
  // https://bugzilla.mozilla.org/show_bug.cgi?id=885597.
  const fileUriAbsPath=relativeSource.replace(/^file:\/\//,"");if(url.scheme=="file"&&this._sources.has(fileUriAbsPath)){return this.sourcesContent[this._sources.indexOf(fileUriAbsPath)];}if((!url.path||url.path=="/")&&this._sources.has("/"+relativeSource)){return this.sourcesContent[this._sources.indexOf("/"+relativeSource)];}}// This function is used recursively from
  // IndexedSourceMapConsumer.prototype.sourceContentFor. In that case, we
  // don't want to throw if we can't find the source - we just want to
  // return null, so we provide a flag to exit gracefully.
  if(nullOnMissing){return null;}throw new Error('"'+relativeSource+'" is not in the SourceMap.');}/**
  	   * Returns the generated line and column information for the original source,
  	   * line, and column positions provided. The only argument is an object with
  	   * the following properties:
  	   *
  	   *   - source: The filename of the original source.
  	   *   - line: The line number in the original source.  The line number
  	   *     is 1-based.
  	   *   - column: The column number in the original source.  The column
  	   *     number is 0-based.
  	   *   - bias: Either 'SourceMapConsumer.GREATEST_LOWER_BOUND' or
  	   *     'SourceMapConsumer.LEAST_UPPER_BOUND'. Specifies whether to return the
  	   *     closest element that is smaller than or greater than the one we are
  	   *     searching for, respectively, if the exact element cannot be found.
  	   *     Defaults to 'SourceMapConsumer.GREATEST_LOWER_BOUND'.
  	   *
  	   * and an object is returned with the following properties:
  	   *
  	   *   - line: The line number in the generated source, or null.  The
  	   *     line number is 1-based.
  	   *   - column: The column number in the generated source, or null.
  	   *     The column number is 0-based.
  	   */generatedPositionFor(aArgs){let source=util.getArg(aArgs,"source");source=this._findSourceIndex(source);if(source<0){return {line:null,column:null,lastColumn:null};}const needle={source,originalLine:util.getArg(aArgs,"line"),originalColumn:util.getArg(aArgs,"column")};if(needle.originalLine<1){throw new Error("Line numbers must be >= 1");}if(needle.originalColumn<0){throw new Error("Column numbers must be >= 0");}let bias=util.getArg(aArgs,"bias",SourceMapConsumer.GREATEST_LOWER_BOUND);if(bias==null){bias=SourceMapConsumer.GREATEST_LOWER_BOUND;}let mapping;this._wasm.withMappingCallback(m=>mapping=m,()=>{this._wasm.exports.generated_location_for(this._getMappingsPtr(),needle.source,needle.originalLine-1,needle.originalColumn,bias);});if(mapping){if(mapping.source===needle.source){let lastColumn=mapping.lastGeneratedColumn;if(this._computedColumnSpans&&lastColumn===null){lastColumn=Infinity;}return {line:util.getArg(mapping,"generatedLine",null),column:util.getArg(mapping,"generatedColumn",null),lastColumn};}}return {line:null,column:null,lastColumn:null};}}BasicSourceMapConsumer.prototype.consumer=SourceMapConsumer;exports.BasicSourceMapConsumer=BasicSourceMapConsumer;/**
  	 * An IndexedSourceMapConsumer instance represents a parsed source map which
  	 * we can query for information. It differs from BasicSourceMapConsumer in
  	 * that it takes "indexed" source maps (i.e. ones with a "sections" field) as
  	 * input.
  	 *
  	 * The first parameter is a raw source map (either as a JSON string, or already
  	 * parsed to an object). According to the spec for indexed source maps, they
  	 * have the following attributes:
  	 *
  	 *   - version: Which version of the source map spec this map is following.
  	 *   - file: Optional. The generated file this source map is associated with.
  	 *   - sections: A list of section definitions.
  	 *
  	 * Each value under the "sections" field has two fields:
  	 *   - offset: The offset into the original specified at which this section
  	 *       begins to apply, defined as an object with a "line" and "column"
  	 *       field.
  	 *   - map: A source map definition. This source map could also be indexed,
  	 *       but doesn't have to be.
  	 *
  	 * Instead of the "map" field, it's also possible to have a "url" field
  	 * specifying a URL to retrieve a source map from, but that's currently
  	 * unsupported.
  	 *
  	 * Here's an example source map, taken from the source map spec[0], but
  	 * modified to omit a section which uses the "url" field.
  	 *
  	 *  {
  	 *    version : 3,
  	 *    file: "app.js",
  	 *    sections: [{
  	 *      offset: {line:100, column:10},
  	 *      map: {
  	 *        version : 3,
  	 *        file: "section.js",
  	 *        sources: ["foo.js", "bar.js"],
  	 *        names: ["src", "maps", "are", "fun"],
  	 *        mappings: "AAAA,E;;ABCDE;"
  	 *      }
  	 *    }],
  	 *  }
  	 *
  	 * The second parameter, if given, is a string whose value is the URL
  	 * at which the source map was found.  This URL is used to compute the
  	 * sources array.
  	 *
  	 * [0]: https://docs.google.com/document/d/1U1RGAehQwRypUTovF1KRlpiOFze0b-_2gc6fAH0KY0k/edit#heading=h.535es3xeprgt
  	 */class IndexedSourceMapConsumer extends SourceMapConsumer{constructor(aSourceMap,aSourceMapURL){return super(INTERNAL).then(that=>{let sourceMap=aSourceMap;if(typeof aSourceMap==="string"){sourceMap=util.parseSourceMapInput(aSourceMap);}const version=util.getArg(sourceMap,"version");const sections=util.getArg(sourceMap,"sections");if(version!=that._version){throw new Error("Unsupported version: "+version);}that._sources=new ArraySet();that._names=new ArraySet();that.__generatedMappings=null;that.__originalMappings=null;that.__generatedMappingsUnsorted=null;that.__originalMappingsUnsorted=null;let lastOffset={line:-1,column:0};return Promise.all(sections.map(s=>{if(s.url){// The url field will require support for asynchronicity.
  // See https://github.com/mozilla/source-map/issues/16
  throw new Error("Support for url field in sections not implemented.");}const offset=util.getArg(s,"offset");const offsetLine=util.getArg(offset,"line");const offsetColumn=util.getArg(offset,"column");if(offsetLine<lastOffset.line||offsetLine===lastOffset.line&&offsetColumn<lastOffset.column){throw new Error("Section offsets must be ordered and non-overlapping.");}lastOffset=offset;const cons=new SourceMapConsumer(util.getArg(s,"map"),aSourceMapURL);return cons.then(consumer=>{return {generatedOffset:{// The offset fields are 0-based, but we use 1-based indices when
  // encoding/decoding from VLQ.
  generatedLine:offsetLine+1,generatedColumn:offsetColumn+1},consumer};});})).then(s=>{that._sections=s;return that;});});}// `__generatedMappings` and `__originalMappings` are arrays that hold the
  // parsed mapping coordinates from the source map's "mappings" attribute. They
  // are lazily instantiated, accessed via the `_generatedMappings` and
  // `_originalMappings` getters respectively, and we only parse the mappings
  // and create these arrays once queried for a source location. We jump through
  // these hoops because there can be many thousands of mappings, and parsing
  // them is expensive, so we only want to do it if we must.
  //
  // Each object in the arrays is of the form:
  //
  //     {
  //       generatedLine: The line number in the generated code,
  //       generatedColumn: The column number in the generated code,
  //       source: The path to the original source file that generated this
  //               chunk of code,
  //       originalLine: The line number in the original source that
  //                     corresponds to this chunk of generated code,
  //       originalColumn: The column number in the original source that
  //                       corresponds to this chunk of generated code,
  //       name: The name of the original symbol which generated this chunk of
  //             code.
  //     }
  //
  // All properties except for `generatedLine` and `generatedColumn` can be
  // `null`.
  //
  // `_generatedMappings` is ordered by the generated positions.
  //
  // `_originalMappings` is ordered by the original positions.
  get _generatedMappings(){if(!this.__generatedMappings){this._sortGeneratedMappings();}return this.__generatedMappings;}get _originalMappings(){if(!this.__originalMappings){this._sortOriginalMappings();}return this.__originalMappings;}get _generatedMappingsUnsorted(){if(!this.__generatedMappingsUnsorted){this._parseMappings(this._mappings,this.sourceRoot);}return this.__generatedMappingsUnsorted;}get _originalMappingsUnsorted(){if(!this.__originalMappingsUnsorted){this._parseMappings(this._mappings,this.sourceRoot);}return this.__originalMappingsUnsorted;}_sortGeneratedMappings(){const mappings=this._generatedMappingsUnsorted;mappings.sort(util.compareByGeneratedPositionsDeflated);this.__generatedMappings=mappings;}_sortOriginalMappings(){const mappings=this._originalMappingsUnsorted;mappings.sort(util.compareByOriginalPositions);this.__originalMappings=mappings;}/**
  	   * The list of original sources.
  	   */get sources(){const sources=[];for(let i=0;i<this._sections.length;i++){for(let j=0;j<this._sections[i].consumer.sources.length;j++){sources.push(this._sections[i].consumer.sources[j]);}}return sources;}/**
  	   * Returns the original source, line, and column information for the generated
  	   * source's line and column positions provided. The only argument is an object
  	   * with the following properties:
  	   *
  	   *   - line: The line number in the generated source.  The line number
  	   *     is 1-based.
  	   *   - column: The column number in the generated source.  The column
  	   *     number is 0-based.
  	   *
  	   * and an object is returned with the following properties:
  	   *
  	   *   - source: The original source file, or null.
  	   *   - line: The line number in the original source, or null.  The
  	   *     line number is 1-based.
  	   *   - column: The column number in the original source, or null.  The
  	   *     column number is 0-based.
  	   *   - name: The original identifier, or null.
  	   */originalPositionFor(aArgs){const needle={generatedLine:util.getArg(aArgs,"line"),generatedColumn:util.getArg(aArgs,"column")};// Find the section containing the generated position we're trying to map
  // to an original position.
  const sectionIndex=binarySearch.search(needle,this._sections,function(aNeedle,section){const cmp=aNeedle.generatedLine-section.generatedOffset.generatedLine;if(cmp){return cmp;}return aNeedle.generatedColumn-section.generatedOffset.generatedColumn;});const section=this._sections[sectionIndex];if(!section){return {source:null,line:null,column:null,name:null};}return section.consumer.originalPositionFor({line:needle.generatedLine-(section.generatedOffset.generatedLine-1),column:needle.generatedColumn-(section.generatedOffset.generatedLine===needle.generatedLine?section.generatedOffset.generatedColumn-1:0),bias:aArgs.bias});}/**
  	   * Return true if we have the source content for every source in the source
  	   * map, false otherwise.
  	   */hasContentsOfAllSources(){return this._sections.every(function(s){return s.consumer.hasContentsOfAllSources();});}/**
  	   * Returns the original source content. The only argument is the url of the
  	   * original source file. Returns null if no original source content is
  	   * available.
  	   */sourceContentFor(aSource,nullOnMissing){for(let i=0;i<this._sections.length;i++){const section=this._sections[i];const content=section.consumer.sourceContentFor(aSource,true);if(content){return content;}}if(nullOnMissing){return null;}throw new Error('"'+aSource+'" is not in the SourceMap.');}/**
  	   * Returns the generated line and column information for the original source,
  	   * line, and column positions provided. The only argument is an object with
  	   * the following properties:
  	   *
  	   *   - source: The filename of the original source.
  	   *   - line: The line number in the original source.  The line number
  	   *     is 1-based.
  	   *   - column: The column number in the original source.  The column
  	   *     number is 0-based.
  	   *
  	   * and an object is returned with the following properties:
  	   *
  	   *   - line: The line number in the generated source, or null.  The
  	   *     line number is 1-based.
  	   *   - column: The column number in the generated source, or null.
  	   *     The column number is 0-based.
  	   */generatedPositionFor(aArgs){for(let i=0;i<this._sections.length;i++){const section=this._sections[i];// Only consider this section if the requested source is in the list of
  // sources of the consumer.
  if(section.consumer._findSourceIndex(util.getArg(aArgs,"source"))===-1){continue;}const generatedPosition=section.consumer.generatedPositionFor(aArgs);if(generatedPosition){const ret={line:generatedPosition.line+(section.generatedOffset.generatedLine-1),column:generatedPosition.column+(section.generatedOffset.generatedLine===generatedPosition.line?section.generatedOffset.generatedColumn-1:0)};return ret;}}return {line:null,column:null};}/**
  	   * Parse the mappings in a string in to a data structure which we can easily
  	   * query (the ordered arrays in the `this.__generatedMappings` and
  	   * `this.__originalMappings` properties).
  	   */_parseMappings(aStr,aSourceRoot){const generatedMappings=this.__generatedMappingsUnsorted=[];const originalMappings=this.__originalMappingsUnsorted=[];for(let i=0;i<this._sections.length;i++){const section=this._sections[i];const sectionMappings=[];section.consumer.eachMapping(m=>sectionMappings.push(m));for(let j=0;j<sectionMappings.length;j++){const mapping=sectionMappings[j];// TODO: test if null is correct here.  The original code used
  // `source`, which would actually have gotten used as null because
  // var's get hoisted.
  // See: https://github.com/mozilla/source-map/issues/333
  let source=util.computeSourceURL(section.consumer.sourceRoot,null,this._sourceMapURL);this._sources.add(source);source=this._sources.indexOf(source);let name=null;if(mapping.name){this._names.add(mapping.name);name=this._names.indexOf(mapping.name);}// The mappings coming from the consumer for the section have
  // generated positions relative to the start of the section, so we
  // need to offset them to be relative to the start of the concatenated
  // generated file.
  const adjustedMapping={source,generatedLine:mapping.generatedLine+(section.generatedOffset.generatedLine-1),generatedColumn:mapping.generatedColumn+(section.generatedOffset.generatedLine===mapping.generatedLine?section.generatedOffset.generatedColumn-1:0),originalLine:mapping.originalLine,originalColumn:mapping.originalColumn,name};generatedMappings.push(adjustedMapping);if(typeof adjustedMapping.originalLine==="number"){originalMappings.push(adjustedMapping);}}}}eachMapping(aCallback,aContext,aOrder){const context=aContext||null;const order=aOrder||SourceMapConsumer.GENERATED_ORDER;let mappings;switch(order){case SourceMapConsumer.GENERATED_ORDER:mappings=this._generatedMappings;break;case SourceMapConsumer.ORIGINAL_ORDER:mappings=this._originalMappings;break;default:throw new Error("Unknown order of iteration.");}const sourceRoot=this.sourceRoot;mappings.map(function(mapping){let source=null;if(mapping.source!==null){source=this._sources.at(mapping.source);source=util.computeSourceURL(sourceRoot,source,this._sourceMapURL);}return {source,generatedLine:mapping.generatedLine,generatedColumn:mapping.generatedColumn,originalLine:mapping.originalLine,originalColumn:mapping.originalColumn,name:mapping.name===null?null:this._names.at(mapping.name)};},this).forEach(aCallback,context);}/**
  	   * Find the mapping that best matches the hypothetical "needle" mapping that
  	   * we are searching for in the given "haystack" of mappings.
  	   */_findMapping(aNeedle,aMappings,aLineName,aColumnName,aComparator,aBias){// To return the position we are searching for, we must first find the
  // mapping for the given position and then return the opposite position it
  // points to. Because the mappings are sorted, we can use binary search to
  // find the best mapping.
  if(aNeedle[aLineName]<=0){throw new TypeError("Line must be greater than or equal to 1, got "+aNeedle[aLineName]);}if(aNeedle[aColumnName]<0){throw new TypeError("Column must be greater than or equal to 0, got "+aNeedle[aColumnName]);}return binarySearch.search(aNeedle,aMappings,aComparator,aBias);}allGeneratedPositionsFor(aArgs){const line=util.getArg(aArgs,"line");// When there is no exact match, BasicSourceMapConsumer.prototype._findMapping
  // returns the index of the closest mapping less than the needle. By
  // setting needle.originalColumn to 0, we thus find the last mapping for
  // the given line, provided such a mapping exists.
  const needle={source:util.getArg(aArgs,"source"),originalLine:line,originalColumn:util.getArg(aArgs,"column",0)};needle.source=this._findSourceIndex(needle.source);if(needle.source<0){return [];}if(needle.originalLine<1){throw new Error("Line numbers must be >= 1");}if(needle.originalColumn<0){throw new Error("Column numbers must be >= 0");}const mappings=[];let index=this._findMapping(needle,this._originalMappings,"originalLine","originalColumn",util.compareByOriginalPositions,binarySearch.LEAST_UPPER_BOUND);if(index>=0){let mapping=this._originalMappings[index];if(aArgs.column===undefined){const originalLine=mapping.originalLine;// Iterate until either we run out of mappings, or we run into
  // a mapping for a different line than the one we found. Since
  // mappings are sorted, this is guaranteed to find all mappings for
  // the line we found.
  while(mapping&&mapping.originalLine===originalLine){let lastColumn=mapping.lastGeneratedColumn;if(this._computedColumnSpans&&lastColumn===null){lastColumn=Infinity;}mappings.push({line:util.getArg(mapping,"generatedLine",null),column:util.getArg(mapping,"generatedColumn",null),lastColumn});mapping=this._originalMappings[++index];}}else{const originalColumn=mapping.originalColumn;// Iterate until either we run out of mappings, or we run into
  // a mapping for a different line than the one we were searching for.
  // Since mappings are sorted, this is guaranteed to find all mappings for
  // the line we are searching for.
  while(mapping&&mapping.originalLine===line&&mapping.originalColumn==originalColumn){let lastColumn=mapping.lastGeneratedColumn;if(this._computedColumnSpans&&lastColumn===null){lastColumn=Infinity;}mappings.push({line:util.getArg(mapping,"generatedLine",null),column:util.getArg(mapping,"generatedColumn",null),lastColumn});mapping=this._originalMappings[++index];}}}return mappings;}destroy(){for(let i=0;i<this._sections.length;i++){this._sections[i].consumer.destroy();}}}exports.IndexedSourceMapConsumer=IndexedSourceMapConsumer;/*
  	 * Cheat to get around inter-twingled classes.  `factory()` can be at the end
  	 * where it has access to non-hoisted classes, but it gets hoisted itself.
  	 */function _factory(aSourceMap,aSourceMapURL){let sourceMap=aSourceMap;if(typeof aSourceMap==="string"){sourceMap=util.parseSourceMapInput(aSourceMap);}const consumer=sourceMap.sections!=null?new IndexedSourceMapConsumer(sourceMap,aSourceMapURL):new BasicSourceMapConsumer(sourceMap,aSourceMapURL);return Promise.resolve(consumer);}function _factoryBSM(aSourceMap,aSourceMapURL){return BasicSourceMapConsumer.fromSourceMap(aSourceMap,aSourceMapURL);}/***/},/* 9 */ /***/function(module,exports){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */exports.GREATEST_LOWER_BOUND=1;exports.LEAST_UPPER_BOUND=2;/**
  	 * Recursive implementation of binary search.
  	 *
  	 * @param aLow Indices here and lower do not contain the needle.
  	 * @param aHigh Indices here and higher do not contain the needle.
  	 * @param aNeedle The element being searched for.
  	 * @param aHaystack The non-empty array being searched.
  	 * @param aCompare Function which takes two elements and returns -1, 0, or 1.
  	 * @param aBias Either 'binarySearch.GREATEST_LOWER_BOUND' or
  	 *     'binarySearch.LEAST_UPPER_BOUND'. Specifies whether to return the
  	 *     closest element that is smaller than or greater than the one we are
  	 *     searching for, respectively, if the exact element cannot be found.
  	 */function recursiveSearch(aLow,aHigh,aNeedle,aHaystack,aCompare,aBias){// This function terminates when one of the following is true:
  //
  //   1. We find the exact element we are looking for.
  //
  //   2. We did not find the exact element, but we can return the index of
  //      the next-closest element.
  //
  //   3. We did not find the exact element, and there is no next-closest
  //      element than the one we are searching for, so we return -1.
  const mid=Math.floor((aHigh-aLow)/2)+aLow;const cmp=aCompare(aNeedle,aHaystack[mid],true);if(cmp===0){// Found the element we are looking for.
  return mid;}else if(cmp>0){// Our needle is greater than aHaystack[mid].
  if(aHigh-mid>1){// The element is in the upper half.
  return recursiveSearch(mid,aHigh,aNeedle,aHaystack,aCompare,aBias);}// The exact needle element was not found in this haystack. Determine if
  // we are in termination case (3) or (2) and return the appropriate thing.
  if(aBias==exports.LEAST_UPPER_BOUND){return aHigh<aHaystack.length?aHigh:-1;}return mid;}// Our needle is less than aHaystack[mid].
  if(mid-aLow>1){// The element is in the lower half.
  return recursiveSearch(aLow,mid,aNeedle,aHaystack,aCompare,aBias);}// we are in termination case (3) or (2) and return the appropriate thing.
  if(aBias==exports.LEAST_UPPER_BOUND){return mid;}return aLow<0?-1:aLow;}/**
  	 * This is an implementation of binary search which will always try and return
  	 * the index of the closest element if there is no exact hit. This is because
  	 * mappings between original and generated line/col pairs are single points,
  	 * and there is an implicit region between each of them, so a miss just means
  	 * that you aren't on the very start of a region.
  	 *
  	 * @param aNeedle The element you are looking for.
  	 * @param aHaystack The array that is being searched.
  	 * @param aCompare A function which takes the needle and an element in the
  	 *     array and returns -1, 0, or 1 depending on whether the needle is less
  	 *     than, equal to, or greater than the element, respectively.
  	 * @param aBias Either 'binarySearch.GREATEST_LOWER_BOUND' or
  	 *     'binarySearch.LEAST_UPPER_BOUND'. Specifies whether to return the
  	 *     closest element that is smaller than or greater than the one we are
  	 *     searching for, respectively, if the exact element cannot be found.
  	 *     Defaults to 'binarySearch.GREATEST_LOWER_BOUND'.
  	 */exports.search=function search(aNeedle,aHaystack,aCompare,aBias){if(aHaystack.length===0){return -1;}let index=recursiveSearch(-1,aHaystack.length,aNeedle,aHaystack,aCompare,aBias||exports.GREATEST_LOWER_BOUND);if(index<0){return -1;}// We have found either the exact element, or the next-closest element than
  // the one we are searching for. However, there may be more than one such
  // element. Make sure we always return the smallest of these.
  while(index-1>=0){if(aCompare(aHaystack[index],aHaystack[index-1],true)!==0){break;}--index;}return index;};/***/},/* 10 */ /***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE_10__;/***/},/* 11 */ /***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE_11__;/***/},/* 12 */ /***/function(module,exports,__webpack_require__){const readWasm=__webpack_require__(4);/**
  	 * Provide the JIT with a nice shape / hidden class.
  	 */function Mapping(){this.generatedLine=0;this.generatedColumn=0;this.lastGeneratedColumn=null;this.source=null;this.originalLine=null;this.originalColumn=null;this.name=null;}let cachedWasm=null;module.exports=function wasm(){if(cachedWasm){return cachedWasm;}const callbackStack=[];cachedWasm=readWasm().then(buffer=>{return WebAssembly.instantiate(buffer,{env:{mapping_callback(generatedLine,generatedColumn,hasLastGeneratedColumn,lastGeneratedColumn,hasOriginal,source,originalLine,originalColumn,hasName,name){const mapping=new Mapping();// JS uses 1-based line numbers, wasm uses 0-based.
  mapping.generatedLine=generatedLine+1;mapping.generatedColumn=generatedColumn;if(hasLastGeneratedColumn){// JS uses inclusive last generated column, wasm uses exclusive.
  mapping.lastGeneratedColumn=lastGeneratedColumn-1;}if(hasOriginal){mapping.source=source;// JS uses 1-based line numbers, wasm uses 0-based.
  mapping.originalLine=originalLine+1;mapping.originalColumn=originalColumn;if(hasName){mapping.name=name;}}callbackStack[callbackStack.length-1](mapping);},start_all_generated_locations_for(){console.time("all_generated_locations_for");},end_all_generated_locations_for(){console.timeEnd("all_generated_locations_for");},start_compute_column_spans(){console.time("compute_column_spans");},end_compute_column_spans(){console.timeEnd("compute_column_spans");},start_generated_location_for(){console.time("generated_location_for");},end_generated_location_for(){console.timeEnd("generated_location_for");},start_original_location_for(){console.time("original_location_for");},end_original_location_for(){console.timeEnd("original_location_for");},start_parse_mappings(){console.time("parse_mappings");},end_parse_mappings(){console.timeEnd("parse_mappings");},start_sort_by_generated_location(){console.time("sort_by_generated_location");},end_sort_by_generated_location(){console.timeEnd("sort_by_generated_location");},start_sort_by_original_location(){console.time("sort_by_original_location");},end_sort_by_original_location(){console.timeEnd("sort_by_original_location");}}});}).then(Wasm=>{return {exports:Wasm.instance.exports,withMappingCallback:(mappingCallback,f)=>{callbackStack.push(mappingCallback);try{f();}finally{callbackStack.pop();}}};}).then(null,e=>{cachedWasm=null;throw e;});return cachedWasm;};/***/},/* 13 */ /***/function(module,exports,__webpack_require__){/* -*- Mode: js; js-indent-level: 2; -*- */ /*
  	 * Copyright 2011 Mozilla Foundation and contributors
  	 * Licensed under the New BSD license. See LICENSE or:
  	 * http://opensource.org/licenses/BSD-3-Clause
  	 */const SourceMapGenerator=__webpack_require__(1).SourceMapGenerator;const util=__webpack_require__(0);// Matches a Windows-style `\r\n` newline or a `\n` newline used by all other
  // operating systems these days (capturing the result).
  const REGEX_NEWLINE=/(\r?\n)/;// Newline character code for charCodeAt() comparisons
  const NEWLINE_CODE=10;// Private symbol for identifying `SourceNode`s when multiple versions of
  // the source-map library are loaded. This MUST NOT CHANGE across
  // versions!
  const isSourceNode="$$$isSourceNode$$$";/**
  	 * SourceNodes provide a way to abstract over interpolating/concatenating
  	 * snippets of generated JavaScript source code while maintaining the line and
  	 * column information associated with the original source code.
  	 *
  	 * @param aLine The original line number.
  	 * @param aColumn The original column number.
  	 * @param aSource The original source's filename.
  	 * @param aChunks Optional. An array of strings which are snippets of
  	 *        generated JS, or other SourceNodes.
  	 * @param aName The original identifier.
  	 */class SourceNode{constructor(aLine,aColumn,aSource,aChunks,aName){this.children=[];this.sourceContents={};this.line=aLine==null?null:aLine;this.column=aColumn==null?null:aColumn;this.source=aSource==null?null:aSource;this.name=aName==null?null:aName;this[isSourceNode]=true;if(aChunks!=null)this.add(aChunks);}/**
  	   * Creates a SourceNode from generated code and a SourceMapConsumer.
  	   *
  	   * @param aGeneratedCode The generated code
  	   * @param aSourceMapConsumer The SourceMap for the generated code
  	   * @param aRelativePath Optional. The path that relative sources in the
  	   *        SourceMapConsumer should be relative to.
  	   */static fromStringWithSourceMap(aGeneratedCode,aSourceMapConsumer,aRelativePath){// The SourceNode we want to fill with the generated code
  // and the SourceMap
  const node=new SourceNode();// All even indices of this array are one line of the generated code,
  // while all odd indices are the newlines between two adjacent lines
  // (since `REGEX_NEWLINE` captures its match).
  // Processed fragments are accessed by calling `shiftNextLine`.
  const remainingLines=aGeneratedCode.split(REGEX_NEWLINE);let remainingLinesIndex=0;const shiftNextLine=function shiftNextLine(){const lineContents=getNextLine();// The last line of a file might not have a newline.
  const newLine=getNextLine()||"";return lineContents+newLine;function getNextLine(){return remainingLinesIndex<remainingLines.length?remainingLines[remainingLinesIndex++]:undefined;}};// We need to remember the position of "remainingLines"
  let lastGeneratedLine=1,lastGeneratedColumn=0;// The generate SourceNodes we need a code range.
  // To extract it current and last mapping is used.
  // Here we store the last mapping.
  let lastMapping=null;let nextLine;aSourceMapConsumer.eachMapping(function(mapping){if(lastMapping!==null){// We add the code from "lastMapping" to "mapping":
  // First check if there is a new line in between.
  if(lastGeneratedLine<mapping.generatedLine){// Associate first line with "lastMapping"
  addMappingWithCode(lastMapping,shiftNextLine());lastGeneratedLine++;lastGeneratedColumn=0;// The remaining code is added without mapping
  }else{// There is no new line in between.
  // Associate the code between "lastGeneratedColumn" and
  // "mapping.generatedColumn" with "lastMapping"
  nextLine=remainingLines[remainingLinesIndex]||"";const code=nextLine.substr(0,mapping.generatedColumn-lastGeneratedColumn);remainingLines[remainingLinesIndex]=nextLine.substr(mapping.generatedColumn-lastGeneratedColumn);lastGeneratedColumn=mapping.generatedColumn;addMappingWithCode(lastMapping,code);// No more remaining code, continue
  lastMapping=mapping;return;}}// We add the generated code until the first mapping
  // to the SourceNode without any mapping.
  // Each line is added as separate string.
  while(lastGeneratedLine<mapping.generatedLine){node.add(shiftNextLine());lastGeneratedLine++;}if(lastGeneratedColumn<mapping.generatedColumn){nextLine=remainingLines[remainingLinesIndex]||"";node.add(nextLine.substr(0,mapping.generatedColumn));remainingLines[remainingLinesIndex]=nextLine.substr(mapping.generatedColumn);lastGeneratedColumn=mapping.generatedColumn;}lastMapping=mapping;},this);// We have processed all mappings.
  if(remainingLinesIndex<remainingLines.length){if(lastMapping){// Associate the remaining code in the current line with "lastMapping"
  addMappingWithCode(lastMapping,shiftNextLine());}// and add the remaining lines without any mapping
  node.add(remainingLines.splice(remainingLinesIndex).join(""));}// Copy sourcesContent into SourceNode
  aSourceMapConsumer.sources.forEach(function(sourceFile){const content=aSourceMapConsumer.sourceContentFor(sourceFile);if(content!=null){if(aRelativePath!=null){sourceFile=util.join(aRelativePath,sourceFile);}node.setSourceContent(sourceFile,content);}});return node;function addMappingWithCode(mapping,code){if(mapping===null||mapping.source===undefined){node.add(code);}else{const source=aRelativePath?util.join(aRelativePath,mapping.source):mapping.source;node.add(new SourceNode(mapping.originalLine,mapping.originalColumn,source,code,mapping.name));}}}/**
  	   * Add a chunk of generated JS to this source node.
  	   *
  	   * @param aChunk A string snippet of generated JS code, another instance of
  	   *        SourceNode, or an array where each member is one of those things.
  	   */add(aChunk){if(Array.isArray(aChunk)){aChunk.forEach(function(chunk){this.add(chunk);},this);}else if(aChunk[isSourceNode]||typeof aChunk==="string"){if(aChunk){this.children.push(aChunk);}}else{throw new TypeError("Expected a SourceNode, string, or an array of SourceNodes and strings. Got "+aChunk);}return this;}/**
  	   * Add a chunk of generated JS to the beginning of this source node.
  	   *
  	   * @param aChunk A string snippet of generated JS code, another instance of
  	   *        SourceNode, or an array where each member is one of those things.
  	   */prepend(aChunk){if(Array.isArray(aChunk)){for(let i=aChunk.length-1;i>=0;i--){this.prepend(aChunk[i]);}}else if(aChunk[isSourceNode]||typeof aChunk==="string"){this.children.unshift(aChunk);}else{throw new TypeError("Expected a SourceNode, string, or an array of SourceNodes and strings. Got "+aChunk);}return this;}/**
  	   * Walk over the tree of JS snippets in this node and its children. The
  	   * walking function is called once for each snippet of JS and is passed that
  	   * snippet and the its original associated source's line/column location.
  	   *
  	   * @param aFn The traversal function.
  	   */walk(aFn){let chunk;for(let i=0,len=this.children.length;i<len;i++){chunk=this.children[i];if(chunk[isSourceNode]){chunk.walk(aFn);}else if(chunk!==""){aFn(chunk,{source:this.source,line:this.line,column:this.column,name:this.name});}}}/**
  	   * Like `String.prototype.join` except for SourceNodes. Inserts `aStr` between
  	   * each of `this.children`.
  	   *
  	   * @param aSep The separator.
  	   */join(aSep){let newChildren;let i;const len=this.children.length;if(len>0){newChildren=[];for(i=0;i<len-1;i++){newChildren.push(this.children[i]);newChildren.push(aSep);}newChildren.push(this.children[i]);this.children=newChildren;}return this;}/**
  	   * Call String.prototype.replace on the very right-most source snippet. Useful
  	   * for trimming whitespace from the end of a source node, etc.
  	   *
  	   * @param aPattern The pattern to replace.
  	   * @param aReplacement The thing to replace the pattern with.
  	   */replaceRight(aPattern,aReplacement){const lastChild=this.children[this.children.length-1];if(lastChild[isSourceNode]){lastChild.replaceRight(aPattern,aReplacement);}else if(typeof lastChild==="string"){this.children[this.children.length-1]=lastChild.replace(aPattern,aReplacement);}else{this.children.push("".replace(aPattern,aReplacement));}return this;}/**
  	   * Set the source content for a source file. This will be added to the SourceMapGenerator
  	   * in the sourcesContent field.
  	   *
  	   * @param aSourceFile The filename of the source file
  	   * @param aSourceContent The content of the source file
  	   */setSourceContent(aSourceFile,aSourceContent){this.sourceContents[util.toSetString(aSourceFile)]=aSourceContent;}/**
  	   * Walk over the tree of SourceNodes. The walking function is called for each
  	   * source file content and is passed the filename and source content.
  	   *
  	   * @param aFn The traversal function.
  	   */walkSourceContents(aFn){for(let i=0,len=this.children.length;i<len;i++){if(this.children[i][isSourceNode]){this.children[i].walkSourceContents(aFn);}}const sources=Object.keys(this.sourceContents);for(let i=0,len=sources.length;i<len;i++){aFn(util.fromSetString(sources[i]),this.sourceContents[sources[i]]);}}/**
  	   * Return the string representation of this source node. Walks over the tree
  	   * and concatenates all the various snippets together to one string.
  	   */toString(){let str="";this.walk(function(chunk){str+=chunk;});return str;}/**
  	   * Returns the string representation of this source node along with a source
  	   * map.
  	   */toStringWithSourceMap(aArgs){const generated={code:"",line:1,column:0};const map=new SourceMapGenerator(aArgs);let sourceMappingActive=false;let lastOriginalSource=null;let lastOriginalLine=null;let lastOriginalColumn=null;let lastOriginalName=null;this.walk(function(chunk,original){generated.code+=chunk;if(original.source!==null&&original.line!==null&&original.column!==null){if(lastOriginalSource!==original.source||lastOriginalLine!==original.line||lastOriginalColumn!==original.column||lastOriginalName!==original.name){map.addMapping({source:original.source,original:{line:original.line,column:original.column},generated:{line:generated.line,column:generated.column},name:original.name});}lastOriginalSource=original.source;lastOriginalLine=original.line;lastOriginalColumn=original.column;lastOriginalName=original.name;sourceMappingActive=true;}else if(sourceMappingActive){map.addMapping({generated:{line:generated.line,column:generated.column}});lastOriginalSource=null;sourceMappingActive=false;}for(let idx=0,length=chunk.length;idx<length;idx++){if(chunk.charCodeAt(idx)===NEWLINE_CODE){generated.line++;generated.column=0;// Mappings end at eol
  if(idx+1===length){lastOriginalSource=null;sourceMappingActive=false;}else if(sourceMappingActive){map.addMapping({source:original.source,original:{line:original.line,column:original.column},generated:{line:generated.line,column:generated.column},name:original.name});}}else{generated.column++;}}});this.walkSourceContents(function(sourceFile,sourceContent){map.setSourceContent(sourceFile,sourceContent);});return {code:generated.code,map};}}exports.SourceNode=SourceNode;/***/}]));});});unwrapExports(sourceMap);var sourceMap_1=sourceMap.SourceMapGenerator;var sourceMap_2=sourceMap.SourceMapConsumer;var util=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};var __importStar=this&&this.__importStar||function(mod){if(mod&&mod.__esModule)return mod;var result={};if(mod!=null)for(var k in mod)if(Object.hasOwnProperty.call(mod,k))result[k]=mod[k];result["default"]=mod;return result;};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var types=__importStar(main);var n=types.namedTypes;var source_map_1=__importDefault(sourceMap);var SourceMapConsumer=source_map_1.default.SourceMapConsumer;var SourceMapGenerator=source_map_1.default.SourceMapGenerator;var hasOwn=Object.prototype.hasOwnProperty;function getOption(options,key,defaultValue){if(options&&hasOwn.call(options,key)){return options[key];}return defaultValue;}exports.getOption=getOption;function getUnionOfKeys(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}var result={};var argc=args.length;for(var i=0;i<argc;++i){var keys=Object.keys(args[i]);var keyCount=keys.length;for(var j=0;j<keyCount;++j){result[keys[j]]=true;}}return result;}exports.getUnionOfKeys=getUnionOfKeys;function comparePos(pos1,pos2){return pos1.line-pos2.line||pos1.column-pos2.column;}exports.comparePos=comparePos;function copyPos(pos){return {line:pos.line,column:pos.column};}exports.copyPos=copyPos;function composeSourceMaps(formerMap,latterMap){if(formerMap){if(!latterMap){return formerMap;}}else{return latterMap||null;}var smcFormer=new SourceMapConsumer(formerMap);var smcLatter=new SourceMapConsumer(latterMap);var smg=new SourceMapGenerator({file:latterMap.file,sourceRoot:latterMap.sourceRoot});var sourcesToContents={};smcLatter.eachMapping(function(mapping){var origPos=smcFormer.originalPositionFor({line:mapping.originalLine,column:mapping.originalColumn});var sourceName=origPos.source;if(sourceName===null){return;}smg.addMapping({source:sourceName,original:copyPos(origPos),generated:{line:mapping.generatedLine,column:mapping.generatedColumn},name:mapping.name});var sourceContent=smcFormer.sourceContentFor(sourceName);if(sourceContent&&!hasOwn.call(sourcesToContents,sourceName)){sourcesToContents[sourceName]=sourceContent;smg.setSourceContent(sourceName,sourceContent);}});return smg.toJSON();}exports.composeSourceMaps=composeSourceMaps;function getTrueLoc(node,lines){// It's possible that node is newly-created (not parsed by Esprima),
  // in which case it probably won't have a .loc property (or an
  // .original property for that matter). That's fine; we'll just
  // pretty-print it as usual.
  if(!node.loc){return null;}var result={start:node.loc.start,end:node.loc.end};function include(node){expandLoc(result,node.loc);}// If the node is an export declaration and its .declaration has any
  // decorators, their locations might contribute to the true start/end
  // positions of the export declaration node.
  if(node.declaration&&node.declaration.decorators&&isExportDeclaration(node)){node.declaration.decorators.forEach(include);}if(comparePos(result.start,result.end)<0){// Trim leading whitespace.
  result.start=copyPos(result.start);lines.skipSpaces(result.start,false,true);if(comparePos(result.start,result.end)<0){// Trim trailing whitespace, if the end location is not already the
  // same as the start location.
  result.end=copyPos(result.end);lines.skipSpaces(result.end,true,true);}}// If the node has any comments, their locations might contribute to
  // the true start/end positions of the node.
  if(node.comments){node.comments.forEach(include);}return result;}exports.getTrueLoc=getTrueLoc;function expandLoc(parentLoc,childLoc){if(parentLoc&&childLoc){if(comparePos(childLoc.start,parentLoc.start)<0){parentLoc.start=childLoc.start;}if(comparePos(parentLoc.end,childLoc.end)<0){parentLoc.end=childLoc.end;}}}function fixFaultyLocations(node,lines){var loc=node.loc;if(loc){if(loc.start.line<1){loc.start.line=1;}if(loc.end.line<1){loc.end.line=1;}}if(node.type==="File"){// Babylon returns File nodes whose .loc.{start,end} do not include
  // leading or trailing whitespace.
  loc.start=lines.firstPos();loc.end=lines.lastPos();}fixForLoopHead(node,lines);fixTemplateLiteral(node,lines);if(loc&&node.decorators){// Expand the .loc of the node responsible for printing the decorators
  // (here, the decorated node) so that it includes node.decorators.
  node.decorators.forEach(function(decorator){expandLoc(loc,decorator.loc);});}else if(node.declaration&&isExportDeclaration(node)){// Nullify .loc information for the child declaration so that we never
  // try to reprint it without also reprinting the export declaration.
  node.declaration.loc=null;// Expand the .loc of the node responsible for printing the decorators
  // (here, the export declaration) so that it includes node.decorators.
  var decorators=node.declaration.decorators;if(decorators){decorators.forEach(function(decorator){expandLoc(loc,decorator.loc);});}}else if(n.MethodDefinition&&n.MethodDefinition.check(node)||n.Property.check(node)&&(node.method||node.shorthand)){// If the node is a MethodDefinition or a .method or .shorthand
  // Property, then the location information stored in
  // node.value.loc is very likely untrustworthy (just the {body}
  // part of a method, or nothing in the case of shorthand
  // properties), so we null out that information to prevent
  // accidental reuse of bogus source code during reprinting.
  node.value.loc=null;if(n.FunctionExpression.check(node.value)){// FunctionExpression method values should be anonymous,
  // because their .id fields are ignored anyway.
  node.value.id=null;}}else if(node.type==="ObjectTypeProperty"){var loc=node.loc;var end=loc&&loc.end;if(end){end=copyPos(end);if(lines.prevPos(end)&&lines.charAt(end)===","){// Some parsers accidentally include trailing commas in the
  // .loc.end information for ObjectTypeProperty nodes.
  if(end=lines.skipSpaces(end,true,true)){loc.end=end;}}}}}exports.fixFaultyLocations=fixFaultyLocations;function fixForLoopHead(node,lines){if(node.type!=="ForStatement"){return;}function fix(child){var loc=child&&child.loc;var start=loc&&loc.start;var end=loc&&copyPos(loc.end);while(start&&end&&comparePos(start,end)<0){lines.prevPos(end);if(lines.charAt(end)===";"){// Update child.loc.end to *exclude* the ';' character.
  loc.end.line=end.line;loc.end.column=end.column;}else{break;}}}fix(node.init);fix(node.test);fix(node.update);}function fixTemplateLiteral(node,lines){if(node.type!=="TemplateLiteral"){return;}if(node.quasis.length===0){// If there are no quasi elements, then there is nothing to fix.
  return;}// node.loc is not present when using export default with a template literal
  if(node.loc){// First we need to exclude the opening ` from the .loc of the first
  // quasi element, in case the parser accidentally decided to include it.
  var afterLeftBackTickPos=copyPos(node.loc.start);assert_1.default.strictEqual(lines.charAt(afterLeftBackTickPos),"`");assert_1.default.ok(lines.nextPos(afterLeftBackTickPos));var firstQuasi=node.quasis[0];if(comparePos(firstQuasi.loc.start,afterLeftBackTickPos)<0){firstQuasi.loc.start=afterLeftBackTickPos;}// Next we need to exclude the closing ` from the .loc of the last quasi
  // element, in case the parser accidentally decided to include it.
  var rightBackTickPos=copyPos(node.loc.end);assert_1.default.ok(lines.prevPos(rightBackTickPos));assert_1.default.strictEqual(lines.charAt(rightBackTickPos),"`");var lastQuasi=node.quasis[node.quasis.length-1];if(comparePos(rightBackTickPos,lastQuasi.loc.end)<0){lastQuasi.loc.end=rightBackTickPos;}}// Now we need to exclude ${ and } characters from the .loc's of all
  // quasi elements, since some parsers accidentally include them.
  node.expressions.forEach(function(expr,i){// Rewind from expr.loc.start over any whitespace and the ${ that
  // precedes the expression. The position of the $ should be the same
  // as the .loc.end of the preceding quasi element, but some parsers
  // accidentally include the ${ in the .loc of the quasi element.
  var dollarCurlyPos=lines.skipSpaces(expr.loc.start,true,false);if(lines.prevPos(dollarCurlyPos)&&lines.charAt(dollarCurlyPos)==="{"&&lines.prevPos(dollarCurlyPos)&&lines.charAt(dollarCurlyPos)==="$"){var quasiBefore=node.quasis[i];if(comparePos(dollarCurlyPos,quasiBefore.loc.end)<0){quasiBefore.loc.end=dollarCurlyPos;}}// Likewise, some parsers accidentally include the } that follows
  // the expression in the .loc of the following quasi element.
  var rightCurlyPos=lines.skipSpaces(expr.loc.end,false,false);if(lines.charAt(rightCurlyPos)==="}"){assert_1.default.ok(lines.nextPos(rightCurlyPos));// Now rightCurlyPos is technically the position just after the }.
  var quasiAfter=node.quasis[i+1];if(comparePos(quasiAfter.loc.start,rightCurlyPos)<0){quasiAfter.loc.start=rightCurlyPos;}}});}function isExportDeclaration(node){if(node)switch(node.type){case"ExportDeclaration":case"ExportDefaultDeclaration":case"ExportDefaultSpecifier":case"DeclareExportDeclaration":case"ExportNamedDeclaration":case"ExportAllDeclaration":return true;}return false;}exports.isExportDeclaration=isExportDeclaration;function getParentExportDeclaration(path){var parentNode=path.getParentNode();if(path.getName()==="declaration"&&isExportDeclaration(parentNode)){return parentNode;}return null;}exports.getParentExportDeclaration=getParentExportDeclaration;function isTrailingCommaEnabled(options,context){var trailingComma=options.trailingComma;if(typeof trailingComma==="object"){return !!trailingComma[context];}return !!trailingComma;}exports.isTrailingCommaEnabled=isTrailingCommaEnabled;});unwrapExports(util);var util_1=util.getOption;var util_2=util.getUnionOfKeys;var util_3=util.comparePos;var util_4=util.copyPos;var util_5=util.composeSourceMaps;var util_6=util.getTrueLoc;var util_7=util.fixFaultyLocations;var util_8=util.isExportDeclaration;var util_9=util.getParentExportDeclaration;var util_10=util.isTrailingCommaEnabled;var esprima$1=createCommonjsModule(function(module,exports){(function webpackUniversalModuleDefinition(root,factory){/* istanbul ignore next */module.exports=factory();})(this,function(){return(/******/function(modules){// webpackBootstrap
  /******/ // The module cache
  /******/var installedModules={};/******/ // The require function
  /******/function __webpack_require__(moduleId){/******/ // Check if module is in cache
  /* istanbul ignore if */ /******/if(installedModules[moduleId])/******/return installedModules[moduleId].exports;/******/ // Create a new module (and put it into the cache)
  /******/var module=installedModules[moduleId]={/******/exports:{},/******/id:moduleId,/******/loaded:false/******/};/******/ // Execute the module function
  /******/modules[moduleId].call(module.exports,module,module.exports,__webpack_require__);/******/ // Flag the module as loaded
  /******/module.loaded=true;/******/ // Return the exports of the module
  /******/return module.exports;/******/}/******/ // expose the modules object (__webpack_modules__)
  /******/__webpack_require__.m=modules;/******/ // expose the module cache
  /******/__webpack_require__.c=installedModules;/******/ // __webpack_public_path__
  /******/__webpack_require__.p="";/******/ // Load entry module and return exports
  /******/return __webpack_require__(0);/******/}(/************************************************************************/ /******/[/* 0 */ /***/function(module,exports,__webpack_require__){/*
  		  Copyright JS Foundation and other contributors, https://js.foundation/

  		  Redistribution and use in source and binary forms, with or without
  		  modification, are permitted provided that the following conditions are met:

  		    * Redistributions of source code must retain the above copyright
  		      notice, this list of conditions and the following disclaimer.
  		    * Redistributions in binary form must reproduce the above copyright
  		      notice, this list of conditions and the following disclaimer in the
  		      documentation and/or other materials provided with the distribution.

  		  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  		  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  		  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  		  ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
  		  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  		  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  		  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  		  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  		  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  		  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  		*/Object.defineProperty(exports,"__esModule",{value:true});var comment_handler_1=__webpack_require__(1);var jsx_parser_1=__webpack_require__(3);var parser_1=__webpack_require__(8);var tokenizer_1=__webpack_require__(15);function parse(code,options,delegate){var commentHandler=null;var proxyDelegate=function proxyDelegate(node,metadata){if(delegate){delegate(node,metadata);}if(commentHandler){commentHandler.visit(node,metadata);}};var parserDelegate=typeof delegate==='function'?proxyDelegate:null;var collectComment=false;if(options){collectComment=typeof options.comment==='boolean'&&options.comment;var attachComment=typeof options.attachComment==='boolean'&&options.attachComment;if(collectComment||attachComment){commentHandler=new comment_handler_1.CommentHandler();commentHandler.attach=attachComment;options.comment=true;parserDelegate=proxyDelegate;}}var isModule=false;if(options&&typeof options.sourceType==='string'){isModule=options.sourceType==='module';}var parser;if(options&&typeof options.jsx==='boolean'&&options.jsx){parser=new jsx_parser_1.JSXParser(code,options,parserDelegate);}else{parser=new parser_1.Parser(code,options,parserDelegate);}var program=isModule?parser.parseModule():parser.parseScript();var ast=program;if(collectComment&&commentHandler){ast.comments=commentHandler.comments;}if(parser.config.tokens){ast.tokens=parser.tokens;}if(parser.config.tolerant){ast.errors=parser.errorHandler.errors;}return ast;}exports.parse=parse;function parseModule(code,options,delegate){var parsingOptions=options||{};parsingOptions.sourceType='module';return parse(code,parsingOptions,delegate);}exports.parseModule=parseModule;function parseScript(code,options,delegate){var parsingOptions=options||{};parsingOptions.sourceType='script';return parse(code,parsingOptions,delegate);}exports.parseScript=parseScript;function tokenize(code,options,delegate){var tokenizer=new tokenizer_1.Tokenizer(code,options);var tokens;tokens=[];try{while(true){var token=tokenizer.getNextToken();if(!token){break;}if(delegate){token=delegate(token);}tokens.push(token);}}catch(e){tokenizer.errorHandler.tolerate(e);}if(tokenizer.errorHandler.tolerant){tokens.errors=tokenizer.errors();}return tokens;}exports.tokenize=tokenize;var syntax_1=__webpack_require__(2);exports.Syntax=syntax_1.Syntax;// Sync with *.json manifests.
  exports.version='4.0.1';/***/},/* 1 */ /***/function(module,exports,__webpack_require__){Object.defineProperty(exports,"__esModule",{value:true});var syntax_1=__webpack_require__(2);var CommentHandler=function(){function CommentHandler(){this.attach=false;this.comments=[];this.stack=[];this.leading=[];this.trailing=[];}CommentHandler.prototype.insertInnerComments=function(node,metadata){//  innnerComments for properties empty block
  //  `function a() {/** comments **\/}`
  if(node.type===syntax_1.Syntax.BlockStatement&&node.body.length===0){var innerComments=[];for(var i=this.leading.length-1;i>=0;--i){var entry=this.leading[i];if(metadata.end.offset>=entry.start){innerComments.unshift(entry.comment);this.leading.splice(i,1);this.trailing.splice(i,1);}}if(innerComments.length){node.innerComments=innerComments;}}};CommentHandler.prototype.findTrailingComments=function(metadata){var trailingComments=[];if(this.trailing.length>0){for(var i=this.trailing.length-1;i>=0;--i){var entry_1=this.trailing[i];if(entry_1.start>=metadata.end.offset){trailingComments.unshift(entry_1.comment);}}this.trailing.length=0;return trailingComments;}var entry=this.stack[this.stack.length-1];if(entry&&entry.node.trailingComments){var firstComment=entry.node.trailingComments[0];if(firstComment&&firstComment.range[0]>=metadata.end.offset){trailingComments=entry.node.trailingComments;delete entry.node.trailingComments;}}return trailingComments;};CommentHandler.prototype.findLeadingComments=function(metadata){var leadingComments=[];var target;while(this.stack.length>0){var entry=this.stack[this.stack.length-1];if(entry&&entry.start>=metadata.start.offset){target=entry.node;this.stack.pop();}else{break;}}if(target){var count=target.leadingComments?target.leadingComments.length:0;for(var i=count-1;i>=0;--i){var comment=target.leadingComments[i];if(comment.range[1]<=metadata.start.offset){leadingComments.unshift(comment);target.leadingComments.splice(i,1);}}if(target.leadingComments&&target.leadingComments.length===0){delete target.leadingComments;}return leadingComments;}for(var i=this.leading.length-1;i>=0;--i){var entry=this.leading[i];if(entry.start<=metadata.start.offset){leadingComments.unshift(entry.comment);this.leading.splice(i,1);}}return leadingComments;};CommentHandler.prototype.visitNode=function(node,metadata){if(node.type===syntax_1.Syntax.Program&&node.body.length>0){return;}this.insertInnerComments(node,metadata);var trailingComments=this.findTrailingComments(metadata);var leadingComments=this.findLeadingComments(metadata);if(leadingComments.length>0){node.leadingComments=leadingComments;}if(trailingComments.length>0){node.trailingComments=trailingComments;}this.stack.push({node:node,start:metadata.start.offset});};CommentHandler.prototype.visitComment=function(node,metadata){var type=node.type[0]==='L'?'Line':'Block';var comment={type:type,value:node.value};if(node.range){comment.range=node.range;}if(node.loc){comment.loc=node.loc;}this.comments.push(comment);if(this.attach){var entry={comment:{type:type,value:node.value,range:[metadata.start.offset,metadata.end.offset]},start:metadata.start.offset};if(node.loc){entry.comment.loc=node.loc;}node.type=type;this.leading.push(entry);this.trailing.push(entry);}};CommentHandler.prototype.visit=function(node,metadata){if(node.type==='LineComment'){this.visitComment(node,metadata);}else if(node.type==='BlockComment'){this.visitComment(node,metadata);}else if(this.attach){this.visitNode(node,metadata);}};return CommentHandler;}();exports.CommentHandler=CommentHandler;/***/},/* 2 */ /***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});exports.Syntax={AssignmentExpression:'AssignmentExpression',AssignmentPattern:'AssignmentPattern',ArrayExpression:'ArrayExpression',ArrayPattern:'ArrayPattern',ArrowFunctionExpression:'ArrowFunctionExpression',AwaitExpression:'AwaitExpression',BlockStatement:'BlockStatement',BinaryExpression:'BinaryExpression',BreakStatement:'BreakStatement',CallExpression:'CallExpression',CatchClause:'CatchClause',ClassBody:'ClassBody',ClassDeclaration:'ClassDeclaration',ClassExpression:'ClassExpression',ConditionalExpression:'ConditionalExpression',ContinueStatement:'ContinueStatement',DoWhileStatement:'DoWhileStatement',DebuggerStatement:'DebuggerStatement',EmptyStatement:'EmptyStatement',ExportAllDeclaration:'ExportAllDeclaration',ExportDefaultDeclaration:'ExportDefaultDeclaration',ExportNamedDeclaration:'ExportNamedDeclaration',ExportSpecifier:'ExportSpecifier',ExpressionStatement:'ExpressionStatement',ForStatement:'ForStatement',ForOfStatement:'ForOfStatement',ForInStatement:'ForInStatement',FunctionDeclaration:'FunctionDeclaration',FunctionExpression:'FunctionExpression',Identifier:'Identifier',IfStatement:'IfStatement',ImportDeclaration:'ImportDeclaration',ImportDefaultSpecifier:'ImportDefaultSpecifier',ImportNamespaceSpecifier:'ImportNamespaceSpecifier',ImportSpecifier:'ImportSpecifier',Literal:'Literal',LabeledStatement:'LabeledStatement',LogicalExpression:'LogicalExpression',MemberExpression:'MemberExpression',MetaProperty:'MetaProperty',MethodDefinition:'MethodDefinition',NewExpression:'NewExpression',ObjectExpression:'ObjectExpression',ObjectPattern:'ObjectPattern',Program:'Program',Property:'Property',RestElement:'RestElement',ReturnStatement:'ReturnStatement',SequenceExpression:'SequenceExpression',SpreadElement:'SpreadElement',Super:'Super',SwitchCase:'SwitchCase',SwitchStatement:'SwitchStatement',TaggedTemplateExpression:'TaggedTemplateExpression',TemplateElement:'TemplateElement',TemplateLiteral:'TemplateLiteral',ThisExpression:'ThisExpression',ThrowStatement:'ThrowStatement',TryStatement:'TryStatement',UnaryExpression:'UnaryExpression',UpdateExpression:'UpdateExpression',VariableDeclaration:'VariableDeclaration',VariableDeclarator:'VariableDeclarator',WhileStatement:'WhileStatement',WithStatement:'WithStatement',YieldExpression:'YieldExpression'};/***/},/* 3 */ /***/function(module,exports,__webpack_require__){/* istanbul ignore next */var __extends=this&&this.__extends||function(){var extendStatics=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(d,b){d.__proto__=b;}||function(d,b){for(var p in b)if(b.hasOwnProperty(p))d[p]=b[p];};return function(d,b){extendStatics(d,b);function __(){this.constructor=d;}d.prototype=b===null?Object.create(b):(__.prototype=b.prototype,new __());};}();Object.defineProperty(exports,"__esModule",{value:true});var character_1=__webpack_require__(4);var JSXNode=__webpack_require__(5);var jsx_syntax_1=__webpack_require__(6);var Node=__webpack_require__(7);var parser_1=__webpack_require__(8);var token_1=__webpack_require__(13);var xhtml_entities_1=__webpack_require__(14);token_1.TokenName[100/* Identifier */]='JSXIdentifier';token_1.TokenName[101/* Text */]='JSXText';// Fully qualified element name, e.g. <svg:path> returns "svg:path"
  function getQualifiedElementName(elementName){var qualifiedName;switch(elementName.type){case jsx_syntax_1.JSXSyntax.JSXIdentifier:var id=elementName;qualifiedName=id.name;break;case jsx_syntax_1.JSXSyntax.JSXNamespacedName:var ns=elementName;qualifiedName=getQualifiedElementName(ns.namespace)+':'+getQualifiedElementName(ns.name);break;case jsx_syntax_1.JSXSyntax.JSXMemberExpression:var expr=elementName;qualifiedName=getQualifiedElementName(expr.object)+'.'+getQualifiedElementName(expr.property);break;/* istanbul ignore next */default:break;}return qualifiedName;}var JSXParser=function(_super){__extends(JSXParser,_super);function JSXParser(code,options,delegate){return _super.call(this,code,options,delegate)||this;}JSXParser.prototype.parsePrimaryExpression=function(){return this.match('<')?this.parseJSXRoot():_super.prototype.parsePrimaryExpression.call(this);};JSXParser.prototype.startJSX=function(){// Unwind the scanner before the lookahead token.
  this.scanner.index=this.startMarker.index;this.scanner.lineNumber=this.startMarker.line;this.scanner.lineStart=this.startMarker.index-this.startMarker.column;};JSXParser.prototype.finishJSX=function(){// Prime the next lookahead.
  this.nextToken();};JSXParser.prototype.reenterJSX=function(){this.startJSX();this.expectJSX('}');// Pop the closing '}' added from the lookahead.
  if(this.config.tokens){this.tokens.pop();}};JSXParser.prototype.createJSXNode=function(){this.collectComments();return {index:this.scanner.index,line:this.scanner.lineNumber,column:this.scanner.index-this.scanner.lineStart};};JSXParser.prototype.createJSXChildNode=function(){return {index:this.scanner.index,line:this.scanner.lineNumber,column:this.scanner.index-this.scanner.lineStart};};JSXParser.prototype.scanXHTMLEntity=function(quote){var result='&';var valid=true;var terminated=false;var numeric=false;var hex=false;while(!this.scanner.eof()&&valid&&!terminated){var ch=this.scanner.source[this.scanner.index];if(ch===quote){break;}terminated=ch===';';result+=ch;++this.scanner.index;if(!terminated){switch(result.length){case 2:// e.g. '&#123;'
  numeric=ch==='#';break;case 3:if(numeric){// e.g. '&#x41;'
  hex=ch==='x';valid=hex||character_1.Character.isDecimalDigit(ch.charCodeAt(0));numeric=numeric&&!hex;}break;default:valid=valid&&!(numeric&&!character_1.Character.isDecimalDigit(ch.charCodeAt(0)));valid=valid&&!(hex&&!character_1.Character.isHexDigit(ch.charCodeAt(0)));break;}}}if(valid&&terminated&&result.length>2){// e.g. '&#x41;' becomes just '#x41'
  var str=result.substr(1,result.length-2);if(numeric&&str.length>1){result=String.fromCharCode(parseInt(str.substr(1),10));}else if(hex&&str.length>2){result=String.fromCharCode(parseInt('0'+str.substr(1),16));}else if(!numeric&&!hex&&xhtml_entities_1.XHTMLEntities[str]){result=xhtml_entities_1.XHTMLEntities[str];}}return result;};// Scan the next JSX token. This replaces Scanner#lex when in JSX mode.
  JSXParser.prototype.lexJSX=function(){var cp=this.scanner.source.charCodeAt(this.scanner.index);// < > / : = { }
  if(cp===60||cp===62||cp===47||cp===58||cp===61||cp===123||cp===125){var value=this.scanner.source[this.scanner.index++];return {type:7/* Punctuator */,value:value,lineNumber:this.scanner.lineNumber,lineStart:this.scanner.lineStart,start:this.scanner.index-1,end:this.scanner.index};}// " '
  if(cp===34||cp===39){var start=this.scanner.index;var quote=this.scanner.source[this.scanner.index++];var str='';while(!this.scanner.eof()){var ch=this.scanner.source[this.scanner.index++];if(ch===quote){break;}else if(ch==='&'){str+=this.scanXHTMLEntity(quote);}else{str+=ch;}}return {type:8/* StringLiteral */,value:str,lineNumber:this.scanner.lineNumber,lineStart:this.scanner.lineStart,start:start,end:this.scanner.index};}// ... or .
  if(cp===46){var n1=this.scanner.source.charCodeAt(this.scanner.index+1);var n2=this.scanner.source.charCodeAt(this.scanner.index+2);var value=n1===46&&n2===46?'...':'.';var start=this.scanner.index;this.scanner.index+=value.length;return {type:7/* Punctuator */,value:value,lineNumber:this.scanner.lineNumber,lineStart:this.scanner.lineStart,start:start,end:this.scanner.index};}// `
  if(cp===96){// Only placeholder, since it will be rescanned as a real assignment expression.
  return {type:10/* Template */,value:'',lineNumber:this.scanner.lineNumber,lineStart:this.scanner.lineStart,start:this.scanner.index,end:this.scanner.index};}// Identifer can not contain backslash (char code 92).
  if(character_1.Character.isIdentifierStart(cp)&&cp!==92){var start=this.scanner.index;++this.scanner.index;while(!this.scanner.eof()){var ch=this.scanner.source.charCodeAt(this.scanner.index);if(character_1.Character.isIdentifierPart(ch)&&ch!==92){++this.scanner.index;}else if(ch===45){// Hyphen (char code 45) can be part of an identifier.
  ++this.scanner.index;}else{break;}}var id=this.scanner.source.slice(start,this.scanner.index);return {type:100/* Identifier */,value:id,lineNumber:this.scanner.lineNumber,lineStart:this.scanner.lineStart,start:start,end:this.scanner.index};}return this.scanner.lex();};JSXParser.prototype.nextJSXToken=function(){this.collectComments();this.startMarker.index=this.scanner.index;this.startMarker.line=this.scanner.lineNumber;this.startMarker.column=this.scanner.index-this.scanner.lineStart;var token=this.lexJSX();this.lastMarker.index=this.scanner.index;this.lastMarker.line=this.scanner.lineNumber;this.lastMarker.column=this.scanner.index-this.scanner.lineStart;if(this.config.tokens){this.tokens.push(this.convertToken(token));}return token;};JSXParser.prototype.nextJSXText=function(){this.startMarker.index=this.scanner.index;this.startMarker.line=this.scanner.lineNumber;this.startMarker.column=this.scanner.index-this.scanner.lineStart;var start=this.scanner.index;var text='';while(!this.scanner.eof()){var ch=this.scanner.source[this.scanner.index];if(ch==='{'||ch==='<'){break;}++this.scanner.index;text+=ch;if(character_1.Character.isLineTerminator(ch.charCodeAt(0))){++this.scanner.lineNumber;if(ch==='\r'&&this.scanner.source[this.scanner.index]==='\n'){++this.scanner.index;}this.scanner.lineStart=this.scanner.index;}}this.lastMarker.index=this.scanner.index;this.lastMarker.line=this.scanner.lineNumber;this.lastMarker.column=this.scanner.index-this.scanner.lineStart;var token={type:101/* Text */,value:text,lineNumber:this.scanner.lineNumber,lineStart:this.scanner.lineStart,start:start,end:this.scanner.index};if(text.length>0&&this.config.tokens){this.tokens.push(this.convertToken(token));}return token;};JSXParser.prototype.peekJSXToken=function(){var state=this.scanner.saveState();this.scanner.scanComments();var next=this.lexJSX();this.scanner.restoreState(state);return next;};// Expect the next JSX token to match the specified punctuator.
  // If not, an exception will be thrown.
  JSXParser.prototype.expectJSX=function(value){var token=this.nextJSXToken();if(token.type!==7/* Punctuator */||token.value!==value){this.throwUnexpectedToken(token);}};// Return true if the next JSX token matches the specified punctuator.
  JSXParser.prototype.matchJSX=function(value){var next=this.peekJSXToken();return next.type===7/* Punctuator */&&next.value===value;};JSXParser.prototype.parseJSXIdentifier=function(){var node=this.createJSXNode();var token=this.nextJSXToken();if(token.type!==100/* Identifier */){this.throwUnexpectedToken(token);}return this.finalize(node,new JSXNode.JSXIdentifier(token.value));};JSXParser.prototype.parseJSXElementName=function(){var node=this.createJSXNode();var elementName=this.parseJSXIdentifier();if(this.matchJSX(':')){var namespace=elementName;this.expectJSX(':');var name_1=this.parseJSXIdentifier();elementName=this.finalize(node,new JSXNode.JSXNamespacedName(namespace,name_1));}else if(this.matchJSX('.')){while(this.matchJSX('.')){var object=elementName;this.expectJSX('.');var property=this.parseJSXIdentifier();elementName=this.finalize(node,new JSXNode.JSXMemberExpression(object,property));}}return elementName;};JSXParser.prototype.parseJSXAttributeName=function(){var node=this.createJSXNode();var attributeName;var identifier=this.parseJSXIdentifier();if(this.matchJSX(':')){var namespace=identifier;this.expectJSX(':');var name_2=this.parseJSXIdentifier();attributeName=this.finalize(node,new JSXNode.JSXNamespacedName(namespace,name_2));}else{attributeName=identifier;}return attributeName;};JSXParser.prototype.parseJSXStringLiteralAttribute=function(){var node=this.createJSXNode();var token=this.nextJSXToken();if(token.type!==8/* StringLiteral */){this.throwUnexpectedToken(token);}var raw=this.getTokenRaw(token);return this.finalize(node,new Node.Literal(token.value,raw));};JSXParser.prototype.parseJSXExpressionAttribute=function(){var node=this.createJSXNode();this.expectJSX('{');this.finishJSX();if(this.match('}')){this.tolerateError('JSX attributes must only be assigned a non-empty expression');}var expression=this.parseAssignmentExpression();this.reenterJSX();return this.finalize(node,new JSXNode.JSXExpressionContainer(expression));};JSXParser.prototype.parseJSXAttributeValue=function(){return this.matchJSX('{')?this.parseJSXExpressionAttribute():this.matchJSX('<')?this.parseJSXElement():this.parseJSXStringLiteralAttribute();};JSXParser.prototype.parseJSXNameValueAttribute=function(){var node=this.createJSXNode();var name=this.parseJSXAttributeName();var value=null;if(this.matchJSX('=')){this.expectJSX('=');value=this.parseJSXAttributeValue();}return this.finalize(node,new JSXNode.JSXAttribute(name,value));};JSXParser.prototype.parseJSXSpreadAttribute=function(){var node=this.createJSXNode();this.expectJSX('{');this.expectJSX('...');this.finishJSX();var argument=this.parseAssignmentExpression();this.reenterJSX();return this.finalize(node,new JSXNode.JSXSpreadAttribute(argument));};JSXParser.prototype.parseJSXAttributes=function(){var attributes=[];while(!this.matchJSX('/')&&!this.matchJSX('>')){var attribute=this.matchJSX('{')?this.parseJSXSpreadAttribute():this.parseJSXNameValueAttribute();attributes.push(attribute);}return attributes;};JSXParser.prototype.parseJSXOpeningElement=function(){var node=this.createJSXNode();this.expectJSX('<');var name=this.parseJSXElementName();var attributes=this.parseJSXAttributes();var selfClosing=this.matchJSX('/');if(selfClosing){this.expectJSX('/');}this.expectJSX('>');return this.finalize(node,new JSXNode.JSXOpeningElement(name,selfClosing,attributes));};JSXParser.prototype.parseJSXBoundaryElement=function(){var node=this.createJSXNode();this.expectJSX('<');if(this.matchJSX('/')){this.expectJSX('/');var name_3=this.parseJSXElementName();this.expectJSX('>');return this.finalize(node,new JSXNode.JSXClosingElement(name_3));}var name=this.parseJSXElementName();var attributes=this.parseJSXAttributes();var selfClosing=this.matchJSX('/');if(selfClosing){this.expectJSX('/');}this.expectJSX('>');return this.finalize(node,new JSXNode.JSXOpeningElement(name,selfClosing,attributes));};JSXParser.prototype.parseJSXEmptyExpression=function(){var node=this.createJSXChildNode();this.collectComments();this.lastMarker.index=this.scanner.index;this.lastMarker.line=this.scanner.lineNumber;this.lastMarker.column=this.scanner.index-this.scanner.lineStart;return this.finalize(node,new JSXNode.JSXEmptyExpression());};JSXParser.prototype.parseJSXExpressionContainer=function(){var node=this.createJSXNode();this.expectJSX('{');var expression;if(this.matchJSX('}')){expression=this.parseJSXEmptyExpression();this.expectJSX('}');}else{this.finishJSX();expression=this.parseAssignmentExpression();this.reenterJSX();}return this.finalize(node,new JSXNode.JSXExpressionContainer(expression));};JSXParser.prototype.parseJSXChildren=function(){var children=[];while(!this.scanner.eof()){var node=this.createJSXChildNode();var token=this.nextJSXText();if(token.start<token.end){var raw=this.getTokenRaw(token);var child=this.finalize(node,new JSXNode.JSXText(token.value,raw));children.push(child);}if(this.scanner.source[this.scanner.index]==='{'){var container=this.parseJSXExpressionContainer();children.push(container);}else{break;}}return children;};JSXParser.prototype.parseComplexJSXElement=function(el){var stack=[];while(!this.scanner.eof()){el.children=el.children.concat(this.parseJSXChildren());var node=this.createJSXChildNode();var element=this.parseJSXBoundaryElement();if(element.type===jsx_syntax_1.JSXSyntax.JSXOpeningElement){var opening=element;if(opening.selfClosing){var child=this.finalize(node,new JSXNode.JSXElement(opening,[],null));el.children.push(child);}else{stack.push(el);el={node:node,opening:opening,closing:null,children:[]};}}if(element.type===jsx_syntax_1.JSXSyntax.JSXClosingElement){el.closing=element;var open_1=getQualifiedElementName(el.opening.name);var close_1=getQualifiedElementName(el.closing.name);if(open_1!==close_1){this.tolerateError('Expected corresponding JSX closing tag for %0',open_1);}if(stack.length>0){var child=this.finalize(el.node,new JSXNode.JSXElement(el.opening,el.children,el.closing));el=stack[stack.length-1];el.children.push(child);stack.pop();}else{break;}}}return el;};JSXParser.prototype.parseJSXElement=function(){var node=this.createJSXNode();var opening=this.parseJSXOpeningElement();var children=[];var closing=null;if(!opening.selfClosing){var el=this.parseComplexJSXElement({node:node,opening:opening,closing:closing,children:children});children=el.children;closing=el.closing;}return this.finalize(node,new JSXNode.JSXElement(opening,children,closing));};JSXParser.prototype.parseJSXRoot=function(){// Pop the opening '<' added from the lookahead.
  if(this.config.tokens){this.tokens.pop();}this.startJSX();var element=this.parseJSXElement();this.finishJSX();return element;};JSXParser.prototype.isStartOfExpression=function(){return _super.prototype.isStartOfExpression.call(this)||this.match('<');};return JSXParser;}(parser_1.Parser);exports.JSXParser=JSXParser;/***/},/* 4 */ /***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});// See also tools/generate-unicode-regex.js.
  var Regex={// Unicode v8.0.0 NonAsciiIdentifierStart:
  NonAsciiIdentifierStart:/[\xAA\xB5\xBA\xC0-\xD6\xD8-\xF6\xF8-\u02C1\u02C6-\u02D1\u02E0-\u02E4\u02EC\u02EE\u0370-\u0374\u0376\u0377\u037A-\u037D\u037F\u0386\u0388-\u038A\u038C\u038E-\u03A1\u03A3-\u03F5\u03F7-\u0481\u048A-\u052F\u0531-\u0556\u0559\u0561-\u0587\u05D0-\u05EA\u05F0-\u05F2\u0620-\u064A\u066E\u066F\u0671-\u06D3\u06D5\u06E5\u06E6\u06EE\u06EF\u06FA-\u06FC\u06FF\u0710\u0712-\u072F\u074D-\u07A5\u07B1\u07CA-\u07EA\u07F4\u07F5\u07FA\u0800-\u0815\u081A\u0824\u0828\u0840-\u0858\u08A0-\u08B4\u0904-\u0939\u093D\u0950\u0958-\u0961\u0971-\u0980\u0985-\u098C\u098F\u0990\u0993-\u09A8\u09AA-\u09B0\u09B2\u09B6-\u09B9\u09BD\u09CE\u09DC\u09DD\u09DF-\u09E1\u09F0\u09F1\u0A05-\u0A0A\u0A0F\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32\u0A33\u0A35\u0A36\u0A38\u0A39\u0A59-\u0A5C\u0A5E\u0A72-\u0A74\u0A85-\u0A8D\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0\u0AB2\u0AB3\u0AB5-\u0AB9\u0ABD\u0AD0\u0AE0\u0AE1\u0AF9\u0B05-\u0B0C\u0B0F\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32\u0B33\u0B35-\u0B39\u0B3D\u0B5C\u0B5D\u0B5F-\u0B61\u0B71\u0B83\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99\u0B9A\u0B9C\u0B9E\u0B9F\u0BA3\u0BA4\u0BA8-\u0BAA\u0BAE-\u0BB9\u0BD0\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C39\u0C3D\u0C58-\u0C5A\u0C60\u0C61\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CBD\u0CDE\u0CE0\u0CE1\u0CF1\u0CF2\u0D05-\u0D0C\u0D0E-\u0D10\u0D12-\u0D3A\u0D3D\u0D4E\u0D5F-\u0D61\u0D7A-\u0D7F\u0D85-\u0D96\u0D9A-\u0DB1\u0DB3-\u0DBB\u0DBD\u0DC0-\u0DC6\u0E01-\u0E30\u0E32\u0E33\u0E40-\u0E46\u0E81\u0E82\u0E84\u0E87\u0E88\u0E8A\u0E8D\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EA5\u0EA7\u0EAA\u0EAB\u0EAD-\u0EB0\u0EB2\u0EB3\u0EBD\u0EC0-\u0EC4\u0EC6\u0EDC-\u0EDF\u0F00\u0F40-\u0F47\u0F49-\u0F6C\u0F88-\u0F8C\u1000-\u102A\u103F\u1050-\u1055\u105A-\u105D\u1061\u1065\u1066\u106E-\u1070\u1075-\u1081\u108E\u10A0-\u10C5\u10C7\u10CD\u10D0-\u10FA\u10FC-\u1248\u124A-\u124D\u1250-\u1256\u1258\u125A-\u125D\u1260-\u1288\u128A-\u128D\u1290-\u12B0\u12B2-\u12B5\u12B8-\u12BE\u12C0\u12C2-\u12C5\u12C8-\u12D6\u12D8-\u1310\u1312-\u1315\u1318-\u135A\u1380-\u138F\u13A0-\u13F5\u13F8-\u13FD\u1401-\u166C\u166F-\u167F\u1681-\u169A\u16A0-\u16EA\u16EE-\u16F8\u1700-\u170C\u170E-\u1711\u1720-\u1731\u1740-\u1751\u1760-\u176C\u176E-\u1770\u1780-\u17B3\u17D7\u17DC\u1820-\u1877\u1880-\u18A8\u18AA\u18B0-\u18F5\u1900-\u191E\u1950-\u196D\u1970-\u1974\u1980-\u19AB\u19B0-\u19C9\u1A00-\u1A16\u1A20-\u1A54\u1AA7\u1B05-\u1B33\u1B45-\u1B4B\u1B83-\u1BA0\u1BAE\u1BAF\u1BBA-\u1BE5\u1C00-\u1C23\u1C4D-\u1C4F\u1C5A-\u1C7D\u1CE9-\u1CEC\u1CEE-\u1CF1\u1CF5\u1CF6\u1D00-\u1DBF\u1E00-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F59\u1F5B\u1F5D\u1F5F-\u1F7D\u1F80-\u1FB4\u1FB6-\u1FBC\u1FBE\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC\u2071\u207F\u2090-\u209C\u2102\u2107\u210A-\u2113\u2115\u2118-\u211D\u2124\u2126\u2128\u212A-\u2139\u213C-\u213F\u2145-\u2149\u214E\u2160-\u2188\u2C00-\u2C2E\u2C30-\u2C5E\u2C60-\u2CE4\u2CEB-\u2CEE\u2CF2\u2CF3\u2D00-\u2D25\u2D27\u2D2D\u2D30-\u2D67\u2D6F\u2D80-\u2D96\u2DA0-\u2DA6\u2DA8-\u2DAE\u2DB0-\u2DB6\u2DB8-\u2DBE\u2DC0-\u2DC6\u2DC8-\u2DCE\u2DD0-\u2DD6\u2DD8-\u2DDE\u3005-\u3007\u3021-\u3029\u3031-\u3035\u3038-\u303C\u3041-\u3096\u309B-\u309F\u30A1-\u30FA\u30FC-\u30FF\u3105-\u312D\u3131-\u318E\u31A0-\u31BA\u31F0-\u31FF\u3400-\u4DB5\u4E00-\u9FD5\uA000-\uA48C\uA4D0-\uA4FD\uA500-\uA60C\uA610-\uA61F\uA62A\uA62B\uA640-\uA66E\uA67F-\uA69D\uA6A0-\uA6EF\uA717-\uA71F\uA722-\uA788\uA78B-\uA7AD\uA7B0-\uA7B7\uA7F7-\uA801\uA803-\uA805\uA807-\uA80A\uA80C-\uA822\uA840-\uA873\uA882-\uA8B3\uA8F2-\uA8F7\uA8FB\uA8FD\uA90A-\uA925\uA930-\uA946\uA960-\uA97C\uA984-\uA9B2\uA9CF\uA9E0-\uA9E4\uA9E6-\uA9EF\uA9FA-\uA9FE\uAA00-\uAA28\uAA40-\uAA42\uAA44-\uAA4B\uAA60-\uAA76\uAA7A\uAA7E-\uAAAF\uAAB1\uAAB5\uAAB6\uAAB9-\uAABD\uAAC0\uAAC2\uAADB-\uAADD\uAAE0-\uAAEA\uAAF2-\uAAF4\uAB01-\uAB06\uAB09-\uAB0E\uAB11-\uAB16\uAB20-\uAB26\uAB28-\uAB2E\uAB30-\uAB5A\uAB5C-\uAB65\uAB70-\uABE2\uAC00-\uD7A3\uD7B0-\uD7C6\uD7CB-\uD7FB\uF900-\uFA6D\uFA70-\uFAD9\uFB00-\uFB06\uFB13-\uFB17\uFB1D\uFB1F-\uFB28\uFB2A-\uFB36\uFB38-\uFB3C\uFB3E\uFB40\uFB41\uFB43\uFB44\uFB46-\uFBB1\uFBD3-\uFD3D\uFD50-\uFD8F\uFD92-\uFDC7\uFDF0-\uFDFB\uFE70-\uFE74\uFE76-\uFEFC\uFF21-\uFF3A\uFF41-\uFF5A\uFF66-\uFFBE\uFFC2-\uFFC7\uFFCA-\uFFCF\uFFD2-\uFFD7\uFFDA-\uFFDC]|\uD800[\uDC00-\uDC0B\uDC0D-\uDC26\uDC28-\uDC3A\uDC3C\uDC3D\uDC3F-\uDC4D\uDC50-\uDC5D\uDC80-\uDCFA\uDD40-\uDD74\uDE80-\uDE9C\uDEA0-\uDED0\uDF00-\uDF1F\uDF30-\uDF4A\uDF50-\uDF75\uDF80-\uDF9D\uDFA0-\uDFC3\uDFC8-\uDFCF\uDFD1-\uDFD5]|\uD801[\uDC00-\uDC9D\uDD00-\uDD27\uDD30-\uDD63\uDE00-\uDF36\uDF40-\uDF55\uDF60-\uDF67]|\uD802[\uDC00-\uDC05\uDC08\uDC0A-\uDC35\uDC37\uDC38\uDC3C\uDC3F-\uDC55\uDC60-\uDC76\uDC80-\uDC9E\uDCE0-\uDCF2\uDCF4\uDCF5\uDD00-\uDD15\uDD20-\uDD39\uDD80-\uDDB7\uDDBE\uDDBF\uDE00\uDE10-\uDE13\uDE15-\uDE17\uDE19-\uDE33\uDE60-\uDE7C\uDE80-\uDE9C\uDEC0-\uDEC7\uDEC9-\uDEE4\uDF00-\uDF35\uDF40-\uDF55\uDF60-\uDF72\uDF80-\uDF91]|\uD803[\uDC00-\uDC48\uDC80-\uDCB2\uDCC0-\uDCF2]|\uD804[\uDC03-\uDC37\uDC83-\uDCAF\uDCD0-\uDCE8\uDD03-\uDD26\uDD50-\uDD72\uDD76\uDD83-\uDDB2\uDDC1-\uDDC4\uDDDA\uDDDC\uDE00-\uDE11\uDE13-\uDE2B\uDE80-\uDE86\uDE88\uDE8A-\uDE8D\uDE8F-\uDE9D\uDE9F-\uDEA8\uDEB0-\uDEDE\uDF05-\uDF0C\uDF0F\uDF10\uDF13-\uDF28\uDF2A-\uDF30\uDF32\uDF33\uDF35-\uDF39\uDF3D\uDF50\uDF5D-\uDF61]|\uD805[\uDC80-\uDCAF\uDCC4\uDCC5\uDCC7\uDD80-\uDDAE\uDDD8-\uDDDB\uDE00-\uDE2F\uDE44\uDE80-\uDEAA\uDF00-\uDF19]|\uD806[\uDCA0-\uDCDF\uDCFF\uDEC0-\uDEF8]|\uD808[\uDC00-\uDF99]|\uD809[\uDC00-\uDC6E\uDC80-\uDD43]|[\uD80C\uD840-\uD868\uD86A-\uD86C\uD86F-\uD872][\uDC00-\uDFFF]|\uD80D[\uDC00-\uDC2E]|\uD811[\uDC00-\uDE46]|\uD81A[\uDC00-\uDE38\uDE40-\uDE5E\uDED0-\uDEED\uDF00-\uDF2F\uDF40-\uDF43\uDF63-\uDF77\uDF7D-\uDF8F]|\uD81B[\uDF00-\uDF44\uDF50\uDF93-\uDF9F]|\uD82C[\uDC00\uDC01]|\uD82F[\uDC00-\uDC6A\uDC70-\uDC7C\uDC80-\uDC88\uDC90-\uDC99]|\uD835[\uDC00-\uDC54\uDC56-\uDC9C\uDC9E\uDC9F\uDCA2\uDCA5\uDCA6\uDCA9-\uDCAC\uDCAE-\uDCB9\uDCBB\uDCBD-\uDCC3\uDCC5-\uDD05\uDD07-\uDD0A\uDD0D-\uDD14\uDD16-\uDD1C\uDD1E-\uDD39\uDD3B-\uDD3E\uDD40-\uDD44\uDD46\uDD4A-\uDD50\uDD52-\uDEA5\uDEA8-\uDEC0\uDEC2-\uDEDA\uDEDC-\uDEFA\uDEFC-\uDF14\uDF16-\uDF34\uDF36-\uDF4E\uDF50-\uDF6E\uDF70-\uDF88\uDF8A-\uDFA8\uDFAA-\uDFC2\uDFC4-\uDFCB]|\uD83A[\uDC00-\uDCC4]|\uD83B[\uDE00-\uDE03\uDE05-\uDE1F\uDE21\uDE22\uDE24\uDE27\uDE29-\uDE32\uDE34-\uDE37\uDE39\uDE3B\uDE42\uDE47\uDE49\uDE4B\uDE4D-\uDE4F\uDE51\uDE52\uDE54\uDE57\uDE59\uDE5B\uDE5D\uDE5F\uDE61\uDE62\uDE64\uDE67-\uDE6A\uDE6C-\uDE72\uDE74-\uDE77\uDE79-\uDE7C\uDE7E\uDE80-\uDE89\uDE8B-\uDE9B\uDEA1-\uDEA3\uDEA5-\uDEA9\uDEAB-\uDEBB]|\uD869[\uDC00-\uDED6\uDF00-\uDFFF]|\uD86D[\uDC00-\uDF34\uDF40-\uDFFF]|\uD86E[\uDC00-\uDC1D\uDC20-\uDFFF]|\uD873[\uDC00-\uDEA1]|\uD87E[\uDC00-\uDE1D]/,// Unicode v8.0.0 NonAsciiIdentifierPart:
  NonAsciiIdentifierPart:/[\xAA\xB5\xB7\xBA\xC0-\xD6\xD8-\xF6\xF8-\u02C1\u02C6-\u02D1\u02E0-\u02E4\u02EC\u02EE\u0300-\u0374\u0376\u0377\u037A-\u037D\u037F\u0386-\u038A\u038C\u038E-\u03A1\u03A3-\u03F5\u03F7-\u0481\u0483-\u0487\u048A-\u052F\u0531-\u0556\u0559\u0561-\u0587\u0591-\u05BD\u05BF\u05C1\u05C2\u05C4\u05C5\u05C7\u05D0-\u05EA\u05F0-\u05F2\u0610-\u061A\u0620-\u0669\u066E-\u06D3\u06D5-\u06DC\u06DF-\u06E8\u06EA-\u06FC\u06FF\u0710-\u074A\u074D-\u07B1\u07C0-\u07F5\u07FA\u0800-\u082D\u0840-\u085B\u08A0-\u08B4\u08E3-\u0963\u0966-\u096F\u0971-\u0983\u0985-\u098C\u098F\u0990\u0993-\u09A8\u09AA-\u09B0\u09B2\u09B6-\u09B9\u09BC-\u09C4\u09C7\u09C8\u09CB-\u09CE\u09D7\u09DC\u09DD\u09DF-\u09E3\u09E6-\u09F1\u0A01-\u0A03\u0A05-\u0A0A\u0A0F\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32\u0A33\u0A35\u0A36\u0A38\u0A39\u0A3C\u0A3E-\u0A42\u0A47\u0A48\u0A4B-\u0A4D\u0A51\u0A59-\u0A5C\u0A5E\u0A66-\u0A75\u0A81-\u0A83\u0A85-\u0A8D\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0\u0AB2\u0AB3\u0AB5-\u0AB9\u0ABC-\u0AC5\u0AC7-\u0AC9\u0ACB-\u0ACD\u0AD0\u0AE0-\u0AE3\u0AE6-\u0AEF\u0AF9\u0B01-\u0B03\u0B05-\u0B0C\u0B0F\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32\u0B33\u0B35-\u0B39\u0B3C-\u0B44\u0B47\u0B48\u0B4B-\u0B4D\u0B56\u0B57\u0B5C\u0B5D\u0B5F-\u0B63\u0B66-\u0B6F\u0B71\u0B82\u0B83\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99\u0B9A\u0B9C\u0B9E\u0B9F\u0BA3\u0BA4\u0BA8-\u0BAA\u0BAE-\u0BB9\u0BBE-\u0BC2\u0BC6-\u0BC8\u0BCA-\u0BCD\u0BD0\u0BD7\u0BE6-\u0BEF\u0C00-\u0C03\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C39\u0C3D-\u0C44\u0C46-\u0C48\u0C4A-\u0C4D\u0C55\u0C56\u0C58-\u0C5A\u0C60-\u0C63\u0C66-\u0C6F\u0C81-\u0C83\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CBC-\u0CC4\u0CC6-\u0CC8\u0CCA-\u0CCD\u0CD5\u0CD6\u0CDE\u0CE0-\u0CE3\u0CE6-\u0CEF\u0CF1\u0CF2\u0D01-\u0D03\u0D05-\u0D0C\u0D0E-\u0D10\u0D12-\u0D3A\u0D3D-\u0D44\u0D46-\u0D48\u0D4A-\u0D4E\u0D57\u0D5F-\u0D63\u0D66-\u0D6F\u0D7A-\u0D7F\u0D82\u0D83\u0D85-\u0D96\u0D9A-\u0DB1\u0DB3-\u0DBB\u0DBD\u0DC0-\u0DC6\u0DCA\u0DCF-\u0DD4\u0DD6\u0DD8-\u0DDF\u0DE6-\u0DEF\u0DF2\u0DF3\u0E01-\u0E3A\u0E40-\u0E4E\u0E50-\u0E59\u0E81\u0E82\u0E84\u0E87\u0E88\u0E8A\u0E8D\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EA5\u0EA7\u0EAA\u0EAB\u0EAD-\u0EB9\u0EBB-\u0EBD\u0EC0-\u0EC4\u0EC6\u0EC8-\u0ECD\u0ED0-\u0ED9\u0EDC-\u0EDF\u0F00\u0F18\u0F19\u0F20-\u0F29\u0F35\u0F37\u0F39\u0F3E-\u0F47\u0F49-\u0F6C\u0F71-\u0F84\u0F86-\u0F97\u0F99-\u0FBC\u0FC6\u1000-\u1049\u1050-\u109D\u10A0-\u10C5\u10C7\u10CD\u10D0-\u10FA\u10FC-\u1248\u124A-\u124D\u1250-\u1256\u1258\u125A-\u125D\u1260-\u1288\u128A-\u128D\u1290-\u12B0\u12B2-\u12B5\u12B8-\u12BE\u12C0\u12C2-\u12C5\u12C8-\u12D6\u12D8-\u1310\u1312-\u1315\u1318-\u135A\u135D-\u135F\u1369-\u1371\u1380-\u138F\u13A0-\u13F5\u13F8-\u13FD\u1401-\u166C\u166F-\u167F\u1681-\u169A\u16A0-\u16EA\u16EE-\u16F8\u1700-\u170C\u170E-\u1714\u1720-\u1734\u1740-\u1753\u1760-\u176C\u176E-\u1770\u1772\u1773\u1780-\u17D3\u17D7\u17DC\u17DD\u17E0-\u17E9\u180B-\u180D\u1810-\u1819\u1820-\u1877\u1880-\u18AA\u18B0-\u18F5\u1900-\u191E\u1920-\u192B\u1930-\u193B\u1946-\u196D\u1970-\u1974\u1980-\u19AB\u19B0-\u19C9\u19D0-\u19DA\u1A00-\u1A1B\u1A20-\u1A5E\u1A60-\u1A7C\u1A7F-\u1A89\u1A90-\u1A99\u1AA7\u1AB0-\u1ABD\u1B00-\u1B4B\u1B50-\u1B59\u1B6B-\u1B73\u1B80-\u1BF3\u1C00-\u1C37\u1C40-\u1C49\u1C4D-\u1C7D\u1CD0-\u1CD2\u1CD4-\u1CF6\u1CF8\u1CF9\u1D00-\u1DF5\u1DFC-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F59\u1F5B\u1F5D\u1F5F-\u1F7D\u1F80-\u1FB4\u1FB6-\u1FBC\u1FBE\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC\u200C\u200D\u203F\u2040\u2054\u2071\u207F\u2090-\u209C\u20D0-\u20DC\u20E1\u20E5-\u20F0\u2102\u2107\u210A-\u2113\u2115\u2118-\u211D\u2124\u2126\u2128\u212A-\u2139\u213C-\u213F\u2145-\u2149\u214E\u2160-\u2188\u2C00-\u2C2E\u2C30-\u2C5E\u2C60-\u2CE4\u2CEB-\u2CF3\u2D00-\u2D25\u2D27\u2D2D\u2D30-\u2D67\u2D6F\u2D7F-\u2D96\u2DA0-\u2DA6\u2DA8-\u2DAE\u2DB0-\u2DB6\u2DB8-\u2DBE\u2DC0-\u2DC6\u2DC8-\u2DCE\u2DD0-\u2DD6\u2DD8-\u2DDE\u2DE0-\u2DFF\u3005-\u3007\u3021-\u302F\u3031-\u3035\u3038-\u303C\u3041-\u3096\u3099-\u309F\u30A1-\u30FA\u30FC-\u30FF\u3105-\u312D\u3131-\u318E\u31A0-\u31BA\u31F0-\u31FF\u3400-\u4DB5\u4E00-\u9FD5\uA000-\uA48C\uA4D0-\uA4FD\uA500-\uA60C\uA610-\uA62B\uA640-\uA66F\uA674-\uA67D\uA67F-\uA6F1\uA717-\uA71F\uA722-\uA788\uA78B-\uA7AD\uA7B0-\uA7B7\uA7F7-\uA827\uA840-\uA873\uA880-\uA8C4\uA8D0-\uA8D9\uA8E0-\uA8F7\uA8FB\uA8FD\uA900-\uA92D\uA930-\uA953\uA960-\uA97C\uA980-\uA9C0\uA9CF-\uA9D9\uA9E0-\uA9FE\uAA00-\uAA36\uAA40-\uAA4D\uAA50-\uAA59\uAA60-\uAA76\uAA7A-\uAAC2\uAADB-\uAADD\uAAE0-\uAAEF\uAAF2-\uAAF6\uAB01-\uAB06\uAB09-\uAB0E\uAB11-\uAB16\uAB20-\uAB26\uAB28-\uAB2E\uAB30-\uAB5A\uAB5C-\uAB65\uAB70-\uABEA\uABEC\uABED\uABF0-\uABF9\uAC00-\uD7A3\uD7B0-\uD7C6\uD7CB-\uD7FB\uF900-\uFA6D\uFA70-\uFAD9\uFB00-\uFB06\uFB13-\uFB17\uFB1D-\uFB28\uFB2A-\uFB36\uFB38-\uFB3C\uFB3E\uFB40\uFB41\uFB43\uFB44\uFB46-\uFBB1\uFBD3-\uFD3D\uFD50-\uFD8F\uFD92-\uFDC7\uFDF0-\uFDFB\uFE00-\uFE0F\uFE20-\uFE2F\uFE33\uFE34\uFE4D-\uFE4F\uFE70-\uFE74\uFE76-\uFEFC\uFF10-\uFF19\uFF21-\uFF3A\uFF3F\uFF41-\uFF5A\uFF66-\uFFBE\uFFC2-\uFFC7\uFFCA-\uFFCF\uFFD2-\uFFD7\uFFDA-\uFFDC]|\uD800[\uDC00-\uDC0B\uDC0D-\uDC26\uDC28-\uDC3A\uDC3C\uDC3D\uDC3F-\uDC4D\uDC50-\uDC5D\uDC80-\uDCFA\uDD40-\uDD74\uDDFD\uDE80-\uDE9C\uDEA0-\uDED0\uDEE0\uDF00-\uDF1F\uDF30-\uDF4A\uDF50-\uDF7A\uDF80-\uDF9D\uDFA0-\uDFC3\uDFC8-\uDFCF\uDFD1-\uDFD5]|\uD801[\uDC00-\uDC9D\uDCA0-\uDCA9\uDD00-\uDD27\uDD30-\uDD63\uDE00-\uDF36\uDF40-\uDF55\uDF60-\uDF67]|\uD802[\uDC00-\uDC05\uDC08\uDC0A-\uDC35\uDC37\uDC38\uDC3C\uDC3F-\uDC55\uDC60-\uDC76\uDC80-\uDC9E\uDCE0-\uDCF2\uDCF4\uDCF5\uDD00-\uDD15\uDD20-\uDD39\uDD80-\uDDB7\uDDBE\uDDBF\uDE00-\uDE03\uDE05\uDE06\uDE0C-\uDE13\uDE15-\uDE17\uDE19-\uDE33\uDE38-\uDE3A\uDE3F\uDE60-\uDE7C\uDE80-\uDE9C\uDEC0-\uDEC7\uDEC9-\uDEE6\uDF00-\uDF35\uDF40-\uDF55\uDF60-\uDF72\uDF80-\uDF91]|\uD803[\uDC00-\uDC48\uDC80-\uDCB2\uDCC0-\uDCF2]|\uD804[\uDC00-\uDC46\uDC66-\uDC6F\uDC7F-\uDCBA\uDCD0-\uDCE8\uDCF0-\uDCF9\uDD00-\uDD34\uDD36-\uDD3F\uDD50-\uDD73\uDD76\uDD80-\uDDC4\uDDCA-\uDDCC\uDDD0-\uDDDA\uDDDC\uDE00-\uDE11\uDE13-\uDE37\uDE80-\uDE86\uDE88\uDE8A-\uDE8D\uDE8F-\uDE9D\uDE9F-\uDEA8\uDEB0-\uDEEA\uDEF0-\uDEF9\uDF00-\uDF03\uDF05-\uDF0C\uDF0F\uDF10\uDF13-\uDF28\uDF2A-\uDF30\uDF32\uDF33\uDF35-\uDF39\uDF3C-\uDF44\uDF47\uDF48\uDF4B-\uDF4D\uDF50\uDF57\uDF5D-\uDF63\uDF66-\uDF6C\uDF70-\uDF74]|\uD805[\uDC80-\uDCC5\uDCC7\uDCD0-\uDCD9\uDD80-\uDDB5\uDDB8-\uDDC0\uDDD8-\uDDDD\uDE00-\uDE40\uDE44\uDE50-\uDE59\uDE80-\uDEB7\uDEC0-\uDEC9\uDF00-\uDF19\uDF1D-\uDF2B\uDF30-\uDF39]|\uD806[\uDCA0-\uDCE9\uDCFF\uDEC0-\uDEF8]|\uD808[\uDC00-\uDF99]|\uD809[\uDC00-\uDC6E\uDC80-\uDD43]|[\uD80C\uD840-\uD868\uD86A-\uD86C\uD86F-\uD872][\uDC00-\uDFFF]|\uD80D[\uDC00-\uDC2E]|\uD811[\uDC00-\uDE46]|\uD81A[\uDC00-\uDE38\uDE40-\uDE5E\uDE60-\uDE69\uDED0-\uDEED\uDEF0-\uDEF4\uDF00-\uDF36\uDF40-\uDF43\uDF50-\uDF59\uDF63-\uDF77\uDF7D-\uDF8F]|\uD81B[\uDF00-\uDF44\uDF50-\uDF7E\uDF8F-\uDF9F]|\uD82C[\uDC00\uDC01]|\uD82F[\uDC00-\uDC6A\uDC70-\uDC7C\uDC80-\uDC88\uDC90-\uDC99\uDC9D\uDC9E]|\uD834[\uDD65-\uDD69\uDD6D-\uDD72\uDD7B-\uDD82\uDD85-\uDD8B\uDDAA-\uDDAD\uDE42-\uDE44]|\uD835[\uDC00-\uDC54\uDC56-\uDC9C\uDC9E\uDC9F\uDCA2\uDCA5\uDCA6\uDCA9-\uDCAC\uDCAE-\uDCB9\uDCBB\uDCBD-\uDCC3\uDCC5-\uDD05\uDD07-\uDD0A\uDD0D-\uDD14\uDD16-\uDD1C\uDD1E-\uDD39\uDD3B-\uDD3E\uDD40-\uDD44\uDD46\uDD4A-\uDD50\uDD52-\uDEA5\uDEA8-\uDEC0\uDEC2-\uDEDA\uDEDC-\uDEFA\uDEFC-\uDF14\uDF16-\uDF34\uDF36-\uDF4E\uDF50-\uDF6E\uDF70-\uDF88\uDF8A-\uDFA8\uDFAA-\uDFC2\uDFC4-\uDFCB\uDFCE-\uDFFF]|\uD836[\uDE00-\uDE36\uDE3B-\uDE6C\uDE75\uDE84\uDE9B-\uDE9F\uDEA1-\uDEAF]|\uD83A[\uDC00-\uDCC4\uDCD0-\uDCD6]|\uD83B[\uDE00-\uDE03\uDE05-\uDE1F\uDE21\uDE22\uDE24\uDE27\uDE29-\uDE32\uDE34-\uDE37\uDE39\uDE3B\uDE42\uDE47\uDE49\uDE4B\uDE4D-\uDE4F\uDE51\uDE52\uDE54\uDE57\uDE59\uDE5B\uDE5D\uDE5F\uDE61\uDE62\uDE64\uDE67-\uDE6A\uDE6C-\uDE72\uDE74-\uDE77\uDE79-\uDE7C\uDE7E\uDE80-\uDE89\uDE8B-\uDE9B\uDEA1-\uDEA3\uDEA5-\uDEA9\uDEAB-\uDEBB]|\uD869[\uDC00-\uDED6\uDF00-\uDFFF]|\uD86D[\uDC00-\uDF34\uDF40-\uDFFF]|\uD86E[\uDC00-\uDC1D\uDC20-\uDFFF]|\uD873[\uDC00-\uDEA1]|\uD87E[\uDC00-\uDE1D]|\uDB40[\uDD00-\uDDEF]/};exports.Character={/* tslint:disable:no-bitwise */fromCodePoint:function fromCodePoint(cp){return cp<0x10000?String.fromCharCode(cp):String.fromCharCode(0xD800+(cp-0x10000>>10))+String.fromCharCode(0xDC00+(cp-0x10000&1023));},// https://tc39.github.io/ecma262/#sec-white-space
  isWhiteSpace:function isWhiteSpace(cp){return cp===0x20||cp===0x09||cp===0x0B||cp===0x0C||cp===0xA0||cp>=0x1680&&[0x1680,0x2000,0x2001,0x2002,0x2003,0x2004,0x2005,0x2006,0x2007,0x2008,0x2009,0x200A,0x202F,0x205F,0x3000,0xFEFF].indexOf(cp)>=0;},// https://tc39.github.io/ecma262/#sec-line-terminators
  isLineTerminator:function isLineTerminator(cp){return cp===0x0A||cp===0x0D||cp===0x2028||cp===0x2029;},// https://tc39.github.io/ecma262/#sec-names-and-keywords
  isIdentifierStart:function isIdentifierStart(cp){return cp===0x24||cp===0x5F||cp>=0x41&&cp<=0x5A||cp>=0x61&&cp<=0x7A||cp===0x5C||cp>=0x80&&Regex.NonAsciiIdentifierStart.test(exports.Character.fromCodePoint(cp));},isIdentifierPart:function isIdentifierPart(cp){return cp===0x24||cp===0x5F||cp>=0x41&&cp<=0x5A||cp>=0x61&&cp<=0x7A||cp>=0x30&&cp<=0x39||cp===0x5C||cp>=0x80&&Regex.NonAsciiIdentifierPart.test(exports.Character.fromCodePoint(cp));},// https://tc39.github.io/ecma262/#sec-literals-numeric-literals
  isDecimalDigit:function isDecimalDigit(cp){return cp>=0x30&&cp<=0x39;// 0..9
  },isHexDigit:function isHexDigit(cp){return cp>=0x30&&cp<=0x39||cp>=0x41&&cp<=0x46||cp>=0x61&&cp<=0x66;// a..f
  },isOctalDigit:function isOctalDigit(cp){return cp>=0x30&&cp<=0x37;// 0..7
  }};/***/},/* 5 */ /***/function(module,exports,__webpack_require__){Object.defineProperty(exports,"__esModule",{value:true});var jsx_syntax_1=__webpack_require__(6);/* tslint:disable:max-classes-per-file */var JSXClosingElement=function(){function JSXClosingElement(name){this.type=jsx_syntax_1.JSXSyntax.JSXClosingElement;this.name=name;}return JSXClosingElement;}();exports.JSXClosingElement=JSXClosingElement;var JSXElement=function(){function JSXElement(openingElement,children,closingElement){this.type=jsx_syntax_1.JSXSyntax.JSXElement;this.openingElement=openingElement;this.children=children;this.closingElement=closingElement;}return JSXElement;}();exports.JSXElement=JSXElement;var JSXEmptyExpression=function(){function JSXEmptyExpression(){this.type=jsx_syntax_1.JSXSyntax.JSXEmptyExpression;}return JSXEmptyExpression;}();exports.JSXEmptyExpression=JSXEmptyExpression;var JSXExpressionContainer=function(){function JSXExpressionContainer(expression){this.type=jsx_syntax_1.JSXSyntax.JSXExpressionContainer;this.expression=expression;}return JSXExpressionContainer;}();exports.JSXExpressionContainer=JSXExpressionContainer;var JSXIdentifier=function(){function JSXIdentifier(name){this.type=jsx_syntax_1.JSXSyntax.JSXIdentifier;this.name=name;}return JSXIdentifier;}();exports.JSXIdentifier=JSXIdentifier;var JSXMemberExpression=function(){function JSXMemberExpression(object,property){this.type=jsx_syntax_1.JSXSyntax.JSXMemberExpression;this.object=object;this.property=property;}return JSXMemberExpression;}();exports.JSXMemberExpression=JSXMemberExpression;var JSXAttribute=function(){function JSXAttribute(name,value){this.type=jsx_syntax_1.JSXSyntax.JSXAttribute;this.name=name;this.value=value;}return JSXAttribute;}();exports.JSXAttribute=JSXAttribute;var JSXNamespacedName=function(){function JSXNamespacedName(namespace,name){this.type=jsx_syntax_1.JSXSyntax.JSXNamespacedName;this.namespace=namespace;this.name=name;}return JSXNamespacedName;}();exports.JSXNamespacedName=JSXNamespacedName;var JSXOpeningElement=function(){function JSXOpeningElement(name,selfClosing,attributes){this.type=jsx_syntax_1.JSXSyntax.JSXOpeningElement;this.name=name;this.selfClosing=selfClosing;this.attributes=attributes;}return JSXOpeningElement;}();exports.JSXOpeningElement=JSXOpeningElement;var JSXSpreadAttribute=function(){function JSXSpreadAttribute(argument){this.type=jsx_syntax_1.JSXSyntax.JSXSpreadAttribute;this.argument=argument;}return JSXSpreadAttribute;}();exports.JSXSpreadAttribute=JSXSpreadAttribute;var JSXText=function(){function JSXText(value,raw){this.type=jsx_syntax_1.JSXSyntax.JSXText;this.value=value;this.raw=raw;}return JSXText;}();exports.JSXText=JSXText;/***/},/* 6 */ /***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});exports.JSXSyntax={JSXAttribute:'JSXAttribute',JSXClosingElement:'JSXClosingElement',JSXElement:'JSXElement',JSXEmptyExpression:'JSXEmptyExpression',JSXExpressionContainer:'JSXExpressionContainer',JSXIdentifier:'JSXIdentifier',JSXMemberExpression:'JSXMemberExpression',JSXNamespacedName:'JSXNamespacedName',JSXOpeningElement:'JSXOpeningElement',JSXSpreadAttribute:'JSXSpreadAttribute',JSXText:'JSXText'};/***/},/* 7 */ /***/function(module,exports,__webpack_require__){Object.defineProperty(exports,"__esModule",{value:true});var syntax_1=__webpack_require__(2);/* tslint:disable:max-classes-per-file */var ArrayExpression=function(){function ArrayExpression(elements){this.type=syntax_1.Syntax.ArrayExpression;this.elements=elements;}return ArrayExpression;}();exports.ArrayExpression=ArrayExpression;var ArrayPattern=function(){function ArrayPattern(elements){this.type=syntax_1.Syntax.ArrayPattern;this.elements=elements;}return ArrayPattern;}();exports.ArrayPattern=ArrayPattern;var ArrowFunctionExpression=function(){function ArrowFunctionExpression(params,body,expression){this.type=syntax_1.Syntax.ArrowFunctionExpression;this.id=null;this.params=params;this.body=body;this.generator=false;this.expression=expression;this.async=false;}return ArrowFunctionExpression;}();exports.ArrowFunctionExpression=ArrowFunctionExpression;var AssignmentExpression=function(){function AssignmentExpression(operator,left,right){this.type=syntax_1.Syntax.AssignmentExpression;this.operator=operator;this.left=left;this.right=right;}return AssignmentExpression;}();exports.AssignmentExpression=AssignmentExpression;var AssignmentPattern=function(){function AssignmentPattern(left,right){this.type=syntax_1.Syntax.AssignmentPattern;this.left=left;this.right=right;}return AssignmentPattern;}();exports.AssignmentPattern=AssignmentPattern;var AsyncArrowFunctionExpression=function(){function AsyncArrowFunctionExpression(params,body,expression){this.type=syntax_1.Syntax.ArrowFunctionExpression;this.id=null;this.params=params;this.body=body;this.generator=false;this.expression=expression;this.async=true;}return AsyncArrowFunctionExpression;}();exports.AsyncArrowFunctionExpression=AsyncArrowFunctionExpression;var AsyncFunctionDeclaration=function(){function AsyncFunctionDeclaration(id,params,body){this.type=syntax_1.Syntax.FunctionDeclaration;this.id=id;this.params=params;this.body=body;this.generator=false;this.expression=false;this.async=true;}return AsyncFunctionDeclaration;}();exports.AsyncFunctionDeclaration=AsyncFunctionDeclaration;var AsyncFunctionExpression=function(){function AsyncFunctionExpression(id,params,body){this.type=syntax_1.Syntax.FunctionExpression;this.id=id;this.params=params;this.body=body;this.generator=false;this.expression=false;this.async=true;}return AsyncFunctionExpression;}();exports.AsyncFunctionExpression=AsyncFunctionExpression;var AwaitExpression=function(){function AwaitExpression(argument){this.type=syntax_1.Syntax.AwaitExpression;this.argument=argument;}return AwaitExpression;}();exports.AwaitExpression=AwaitExpression;var BinaryExpression=function(){function BinaryExpression(operator,left,right){var logical=operator==='||'||operator==='&&';this.type=logical?syntax_1.Syntax.LogicalExpression:syntax_1.Syntax.BinaryExpression;this.operator=operator;this.left=left;this.right=right;}return BinaryExpression;}();exports.BinaryExpression=BinaryExpression;var BlockStatement=function(){function BlockStatement(body){this.type=syntax_1.Syntax.BlockStatement;this.body=body;}return BlockStatement;}();exports.BlockStatement=BlockStatement;var BreakStatement=function(){function BreakStatement(label){this.type=syntax_1.Syntax.BreakStatement;this.label=label;}return BreakStatement;}();exports.BreakStatement=BreakStatement;var CallExpression=function(){function CallExpression(callee,args){this.type=syntax_1.Syntax.CallExpression;this.callee=callee;this.arguments=args;}return CallExpression;}();exports.CallExpression=CallExpression;var CatchClause=function(){function CatchClause(param,body){this.type=syntax_1.Syntax.CatchClause;this.param=param;this.body=body;}return CatchClause;}();exports.CatchClause=CatchClause;var ClassBody=function(){function ClassBody(body){this.type=syntax_1.Syntax.ClassBody;this.body=body;}return ClassBody;}();exports.ClassBody=ClassBody;var ClassDeclaration=function(){function ClassDeclaration(id,superClass,body){this.type=syntax_1.Syntax.ClassDeclaration;this.id=id;this.superClass=superClass;this.body=body;}return ClassDeclaration;}();exports.ClassDeclaration=ClassDeclaration;var ClassExpression=function(){function ClassExpression(id,superClass,body){this.type=syntax_1.Syntax.ClassExpression;this.id=id;this.superClass=superClass;this.body=body;}return ClassExpression;}();exports.ClassExpression=ClassExpression;var ComputedMemberExpression=function(){function ComputedMemberExpression(object,property){this.type=syntax_1.Syntax.MemberExpression;this.computed=true;this.object=object;this.property=property;}return ComputedMemberExpression;}();exports.ComputedMemberExpression=ComputedMemberExpression;var ConditionalExpression=function(){function ConditionalExpression(test,consequent,alternate){this.type=syntax_1.Syntax.ConditionalExpression;this.test=test;this.consequent=consequent;this.alternate=alternate;}return ConditionalExpression;}();exports.ConditionalExpression=ConditionalExpression;var ContinueStatement=function(){function ContinueStatement(label){this.type=syntax_1.Syntax.ContinueStatement;this.label=label;}return ContinueStatement;}();exports.ContinueStatement=ContinueStatement;var DebuggerStatement=function(){function DebuggerStatement(){this.type=syntax_1.Syntax.DebuggerStatement;}return DebuggerStatement;}();exports.DebuggerStatement=DebuggerStatement;var Directive=function(){function Directive(expression,directive){this.type=syntax_1.Syntax.ExpressionStatement;this.expression=expression;this.directive=directive;}return Directive;}();exports.Directive=Directive;var DoWhileStatement=function(){function DoWhileStatement(body,test){this.type=syntax_1.Syntax.DoWhileStatement;this.body=body;this.test=test;}return DoWhileStatement;}();exports.DoWhileStatement=DoWhileStatement;var EmptyStatement=function(){function EmptyStatement(){this.type=syntax_1.Syntax.EmptyStatement;}return EmptyStatement;}();exports.EmptyStatement=EmptyStatement;var ExportAllDeclaration=function(){function ExportAllDeclaration(source){this.type=syntax_1.Syntax.ExportAllDeclaration;this.source=source;}return ExportAllDeclaration;}();exports.ExportAllDeclaration=ExportAllDeclaration;var ExportDefaultDeclaration=function(){function ExportDefaultDeclaration(declaration){this.type=syntax_1.Syntax.ExportDefaultDeclaration;this.declaration=declaration;}return ExportDefaultDeclaration;}();exports.ExportDefaultDeclaration=ExportDefaultDeclaration;var ExportNamedDeclaration=function(){function ExportNamedDeclaration(declaration,specifiers,source){this.type=syntax_1.Syntax.ExportNamedDeclaration;this.declaration=declaration;this.specifiers=specifiers;this.source=source;}return ExportNamedDeclaration;}();exports.ExportNamedDeclaration=ExportNamedDeclaration;var ExportSpecifier=function(){function ExportSpecifier(local,exported){this.type=syntax_1.Syntax.ExportSpecifier;this.exported=exported;this.local=local;}return ExportSpecifier;}();exports.ExportSpecifier=ExportSpecifier;var ExpressionStatement=function(){function ExpressionStatement(expression){this.type=syntax_1.Syntax.ExpressionStatement;this.expression=expression;}return ExpressionStatement;}();exports.ExpressionStatement=ExpressionStatement;var ForInStatement=function(){function ForInStatement(left,right,body){this.type=syntax_1.Syntax.ForInStatement;this.left=left;this.right=right;this.body=body;this.each=false;}return ForInStatement;}();exports.ForInStatement=ForInStatement;var ForOfStatement=function(){function ForOfStatement(left,right,body){this.type=syntax_1.Syntax.ForOfStatement;this.left=left;this.right=right;this.body=body;}return ForOfStatement;}();exports.ForOfStatement=ForOfStatement;var ForStatement=function(){function ForStatement(init,test,update,body){this.type=syntax_1.Syntax.ForStatement;this.init=init;this.test=test;this.update=update;this.body=body;}return ForStatement;}();exports.ForStatement=ForStatement;var FunctionDeclaration=function(){function FunctionDeclaration(id,params,body,generator){this.type=syntax_1.Syntax.FunctionDeclaration;this.id=id;this.params=params;this.body=body;this.generator=generator;this.expression=false;this.async=false;}return FunctionDeclaration;}();exports.FunctionDeclaration=FunctionDeclaration;var FunctionExpression=function(){function FunctionExpression(id,params,body,generator){this.type=syntax_1.Syntax.FunctionExpression;this.id=id;this.params=params;this.body=body;this.generator=generator;this.expression=false;this.async=false;}return FunctionExpression;}();exports.FunctionExpression=FunctionExpression;var Identifier=function(){function Identifier(name){this.type=syntax_1.Syntax.Identifier;this.name=name;}return Identifier;}();exports.Identifier=Identifier;var IfStatement=function(){function IfStatement(test,consequent,alternate){this.type=syntax_1.Syntax.IfStatement;this.test=test;this.consequent=consequent;this.alternate=alternate;}return IfStatement;}();exports.IfStatement=IfStatement;var ImportDeclaration=function(){function ImportDeclaration(specifiers,source){this.type=syntax_1.Syntax.ImportDeclaration;this.specifiers=specifiers;this.source=source;}return ImportDeclaration;}();exports.ImportDeclaration=ImportDeclaration;var ImportDefaultSpecifier=function(){function ImportDefaultSpecifier(local){this.type=syntax_1.Syntax.ImportDefaultSpecifier;this.local=local;}return ImportDefaultSpecifier;}();exports.ImportDefaultSpecifier=ImportDefaultSpecifier;var ImportNamespaceSpecifier=function(){function ImportNamespaceSpecifier(local){this.type=syntax_1.Syntax.ImportNamespaceSpecifier;this.local=local;}return ImportNamespaceSpecifier;}();exports.ImportNamespaceSpecifier=ImportNamespaceSpecifier;var ImportSpecifier=function(){function ImportSpecifier(local,imported){this.type=syntax_1.Syntax.ImportSpecifier;this.local=local;this.imported=imported;}return ImportSpecifier;}();exports.ImportSpecifier=ImportSpecifier;var LabeledStatement=function(){function LabeledStatement(label,body){this.type=syntax_1.Syntax.LabeledStatement;this.label=label;this.body=body;}return LabeledStatement;}();exports.LabeledStatement=LabeledStatement;var Literal=function(){function Literal(value,raw){this.type=syntax_1.Syntax.Literal;this.value=value;this.raw=raw;}return Literal;}();exports.Literal=Literal;var MetaProperty=function(){function MetaProperty(meta,property){this.type=syntax_1.Syntax.MetaProperty;this.meta=meta;this.property=property;}return MetaProperty;}();exports.MetaProperty=MetaProperty;var MethodDefinition=function(){function MethodDefinition(key,computed,value,kind,isStatic){this.type=syntax_1.Syntax.MethodDefinition;this.key=key;this.computed=computed;this.value=value;this.kind=kind;this.static=isStatic;}return MethodDefinition;}();exports.MethodDefinition=MethodDefinition;var Module=function(){function Module(body){this.type=syntax_1.Syntax.Program;this.body=body;this.sourceType='module';}return Module;}();exports.Module=Module;var NewExpression=function(){function NewExpression(callee,args){this.type=syntax_1.Syntax.NewExpression;this.callee=callee;this.arguments=args;}return NewExpression;}();exports.NewExpression=NewExpression;var ObjectExpression=function(){function ObjectExpression(properties){this.type=syntax_1.Syntax.ObjectExpression;this.properties=properties;}return ObjectExpression;}();exports.ObjectExpression=ObjectExpression;var ObjectPattern=function(){function ObjectPattern(properties){this.type=syntax_1.Syntax.ObjectPattern;this.properties=properties;}return ObjectPattern;}();exports.ObjectPattern=ObjectPattern;var Property=function(){function Property(kind,key,computed,value,method,shorthand){this.type=syntax_1.Syntax.Property;this.key=key;this.computed=computed;this.value=value;this.kind=kind;this.method=method;this.shorthand=shorthand;}return Property;}();exports.Property=Property;var RegexLiteral=function(){function RegexLiteral(value,raw,pattern,flags){this.type=syntax_1.Syntax.Literal;this.value=value;this.raw=raw;this.regex={pattern:pattern,flags:flags};}return RegexLiteral;}();exports.RegexLiteral=RegexLiteral;var RestElement=function(){function RestElement(argument){this.type=syntax_1.Syntax.RestElement;this.argument=argument;}return RestElement;}();exports.RestElement=RestElement;var ReturnStatement=function(){function ReturnStatement(argument){this.type=syntax_1.Syntax.ReturnStatement;this.argument=argument;}return ReturnStatement;}();exports.ReturnStatement=ReturnStatement;var Script=function(){function Script(body){this.type=syntax_1.Syntax.Program;this.body=body;this.sourceType='script';}return Script;}();exports.Script=Script;var SequenceExpression=function(){function SequenceExpression(expressions){this.type=syntax_1.Syntax.SequenceExpression;this.expressions=expressions;}return SequenceExpression;}();exports.SequenceExpression=SequenceExpression;var SpreadElement=function(){function SpreadElement(argument){this.type=syntax_1.Syntax.SpreadElement;this.argument=argument;}return SpreadElement;}();exports.SpreadElement=SpreadElement;var StaticMemberExpression=function(){function StaticMemberExpression(object,property){this.type=syntax_1.Syntax.MemberExpression;this.computed=false;this.object=object;this.property=property;}return StaticMemberExpression;}();exports.StaticMemberExpression=StaticMemberExpression;var Super=function(){function Super(){this.type=syntax_1.Syntax.Super;}return Super;}();exports.Super=Super;var SwitchCase=function(){function SwitchCase(test,consequent){this.type=syntax_1.Syntax.SwitchCase;this.test=test;this.consequent=consequent;}return SwitchCase;}();exports.SwitchCase=SwitchCase;var SwitchStatement=function(){function SwitchStatement(discriminant,cases){this.type=syntax_1.Syntax.SwitchStatement;this.discriminant=discriminant;this.cases=cases;}return SwitchStatement;}();exports.SwitchStatement=SwitchStatement;var TaggedTemplateExpression=function(){function TaggedTemplateExpression(tag,quasi){this.type=syntax_1.Syntax.TaggedTemplateExpression;this.tag=tag;this.quasi=quasi;}return TaggedTemplateExpression;}();exports.TaggedTemplateExpression=TaggedTemplateExpression;var TemplateElement=function(){function TemplateElement(value,tail){this.type=syntax_1.Syntax.TemplateElement;this.value=value;this.tail=tail;}return TemplateElement;}();exports.TemplateElement=TemplateElement;var TemplateLiteral=function(){function TemplateLiteral(quasis,expressions){this.type=syntax_1.Syntax.TemplateLiteral;this.quasis=quasis;this.expressions=expressions;}return TemplateLiteral;}();exports.TemplateLiteral=TemplateLiteral;var ThisExpression=function(){function ThisExpression(){this.type=syntax_1.Syntax.ThisExpression;}return ThisExpression;}();exports.ThisExpression=ThisExpression;var ThrowStatement=function(){function ThrowStatement(argument){this.type=syntax_1.Syntax.ThrowStatement;this.argument=argument;}return ThrowStatement;}();exports.ThrowStatement=ThrowStatement;var TryStatement=function(){function TryStatement(block,handler,finalizer){this.type=syntax_1.Syntax.TryStatement;this.block=block;this.handler=handler;this.finalizer=finalizer;}return TryStatement;}();exports.TryStatement=TryStatement;var UnaryExpression=function(){function UnaryExpression(operator,argument){this.type=syntax_1.Syntax.UnaryExpression;this.operator=operator;this.argument=argument;this.prefix=true;}return UnaryExpression;}();exports.UnaryExpression=UnaryExpression;var UpdateExpression=function(){function UpdateExpression(operator,argument,prefix){this.type=syntax_1.Syntax.UpdateExpression;this.operator=operator;this.argument=argument;this.prefix=prefix;}return UpdateExpression;}();exports.UpdateExpression=UpdateExpression;var VariableDeclaration=function(){function VariableDeclaration(declarations,kind){this.type=syntax_1.Syntax.VariableDeclaration;this.declarations=declarations;this.kind=kind;}return VariableDeclaration;}();exports.VariableDeclaration=VariableDeclaration;var VariableDeclarator=function(){function VariableDeclarator(id,init){this.type=syntax_1.Syntax.VariableDeclarator;this.id=id;this.init=init;}return VariableDeclarator;}();exports.VariableDeclarator=VariableDeclarator;var WhileStatement=function(){function WhileStatement(test,body){this.type=syntax_1.Syntax.WhileStatement;this.test=test;this.body=body;}return WhileStatement;}();exports.WhileStatement=WhileStatement;var WithStatement=function(){function WithStatement(object,body){this.type=syntax_1.Syntax.WithStatement;this.object=object;this.body=body;}return WithStatement;}();exports.WithStatement=WithStatement;var YieldExpression=function(){function YieldExpression(argument,delegate){this.type=syntax_1.Syntax.YieldExpression;this.argument=argument;this.delegate=delegate;}return YieldExpression;}();exports.YieldExpression=YieldExpression;/***/},/* 8 */ /***/function(module,exports,__webpack_require__){Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__webpack_require__(9);var error_handler_1=__webpack_require__(10);var messages_1=__webpack_require__(11);var Node=__webpack_require__(7);var scanner_1=__webpack_require__(12);var syntax_1=__webpack_require__(2);var token_1=__webpack_require__(13);var ArrowParameterPlaceHolder='ArrowParameterPlaceHolder';var Parser=function(){function Parser(code,options,delegate){if(options===void 0){options={};}this.config={range:typeof options.range==='boolean'&&options.range,loc:typeof options.loc==='boolean'&&options.loc,source:null,tokens:typeof options.tokens==='boolean'&&options.tokens,comment:typeof options.comment==='boolean'&&options.comment,tolerant:typeof options.tolerant==='boolean'&&options.tolerant};if(this.config.loc&&options.source&&options.source!==null){this.config.source=String(options.source);}this.delegate=delegate;this.errorHandler=new error_handler_1.ErrorHandler();this.errorHandler.tolerant=this.config.tolerant;this.scanner=new scanner_1.Scanner(code,this.errorHandler);this.scanner.trackComment=this.config.comment;this.operatorPrecedence={')':0,';':0,',':0,'=':0,']':0,'||':1,'&&':2,'|':3,'^':4,'&':5,'==':6,'!=':6,'===':6,'!==':6,'<':7,'>':7,'<=':7,'>=':7,'<<':8,'>>':8,'>>>':8,'+':9,'-':9,'*':11,'/':11,'%':11};this.lookahead={type:2/* EOF */,value:'',lineNumber:this.scanner.lineNumber,lineStart:0,start:0,end:0};this.hasLineTerminator=false;this.context={isModule:false,await:false,allowIn:true,allowStrictDirective:true,allowYield:true,firstCoverInitializedNameError:null,isAssignmentTarget:false,isBindingElement:false,inFunctionBody:false,inIteration:false,inSwitch:false,labelSet:{},strict:false};this.tokens=[];this.startMarker={index:0,line:this.scanner.lineNumber,column:0};this.lastMarker={index:0,line:this.scanner.lineNumber,column:0};this.nextToken();this.lastMarker={index:this.scanner.index,line:this.scanner.lineNumber,column:this.scanner.index-this.scanner.lineStart};}Parser.prototype.throwError=function(messageFormat){var values=[];for(var _i=1;_i<arguments.length;_i++){values[_i-1]=arguments[_i];}var args=Array.prototype.slice.call(arguments,1);var msg=messageFormat.replace(/%(\d)/g,function(whole,idx){assert_1.assert(idx<args.length,'Message reference must be in range');return args[idx];});var index=this.lastMarker.index;var line=this.lastMarker.line;var column=this.lastMarker.column+1;throw this.errorHandler.createError(index,line,column,msg);};Parser.prototype.tolerateError=function(messageFormat){var values=[];for(var _i=1;_i<arguments.length;_i++){values[_i-1]=arguments[_i];}var args=Array.prototype.slice.call(arguments,1);var msg=messageFormat.replace(/%(\d)/g,function(whole,idx){assert_1.assert(idx<args.length,'Message reference must be in range');return args[idx];});var index=this.lastMarker.index;var line=this.scanner.lineNumber;var column=this.lastMarker.column+1;this.errorHandler.tolerateError(index,line,column,msg);};// Throw an exception because of the token.
  Parser.prototype.unexpectedTokenError=function(token,message){var msg=message||messages_1.Messages.UnexpectedToken;var value;if(token){if(!message){msg=token.type===2/* EOF */?messages_1.Messages.UnexpectedEOS:token.type===3/* Identifier */?messages_1.Messages.UnexpectedIdentifier:token.type===6/* NumericLiteral */?messages_1.Messages.UnexpectedNumber:token.type===8/* StringLiteral */?messages_1.Messages.UnexpectedString:token.type===10/* Template */?messages_1.Messages.UnexpectedTemplate:messages_1.Messages.UnexpectedToken;if(token.type===4/* Keyword */){if(this.scanner.isFutureReservedWord(token.value)){msg=messages_1.Messages.UnexpectedReserved;}else if(this.context.strict&&this.scanner.isStrictModeReservedWord(token.value)){msg=messages_1.Messages.StrictReservedWord;}}}value=token.value;}else{value='ILLEGAL';}msg=msg.replace('%0',value);if(token&&typeof token.lineNumber==='number'){var index=token.start;var line=token.lineNumber;var lastMarkerLineStart=this.lastMarker.index-this.lastMarker.column;var column=token.start-lastMarkerLineStart+1;return this.errorHandler.createError(index,line,column,msg);}else{var index=this.lastMarker.index;var line=this.lastMarker.line;var column=this.lastMarker.column+1;return this.errorHandler.createError(index,line,column,msg);}};Parser.prototype.throwUnexpectedToken=function(token,message){throw this.unexpectedTokenError(token,message);};Parser.prototype.tolerateUnexpectedToken=function(token,message){this.errorHandler.tolerate(this.unexpectedTokenError(token,message));};Parser.prototype.collectComments=function(){if(!this.config.comment){this.scanner.scanComments();}else{var comments=this.scanner.scanComments();if(comments.length>0&&this.delegate){for(var i=0;i<comments.length;++i){var e=comments[i];var node=void 0;node={type:e.multiLine?'BlockComment':'LineComment',value:this.scanner.source.slice(e.slice[0],e.slice[1])};if(this.config.range){node.range=e.range;}if(this.config.loc){node.loc=e.loc;}var metadata={start:{line:e.loc.start.line,column:e.loc.start.column,offset:e.range[0]},end:{line:e.loc.end.line,column:e.loc.end.column,offset:e.range[1]}};this.delegate(node,metadata);}}}};// From internal representation to an external structure
  Parser.prototype.getTokenRaw=function(token){return this.scanner.source.slice(token.start,token.end);};Parser.prototype.convertToken=function(token){var t={type:token_1.TokenName[token.type],value:this.getTokenRaw(token)};if(this.config.range){t.range=[token.start,token.end];}if(this.config.loc){t.loc={start:{line:this.startMarker.line,column:this.startMarker.column},end:{line:this.scanner.lineNumber,column:this.scanner.index-this.scanner.lineStart}};}if(token.type===9/* RegularExpression */){var pattern=token.pattern;var flags=token.flags;t.regex={pattern:pattern,flags:flags};}return t;};Parser.prototype.nextToken=function(){var token=this.lookahead;this.lastMarker.index=this.scanner.index;this.lastMarker.line=this.scanner.lineNumber;this.lastMarker.column=this.scanner.index-this.scanner.lineStart;this.collectComments();if(this.scanner.index!==this.startMarker.index){this.startMarker.index=this.scanner.index;this.startMarker.line=this.scanner.lineNumber;this.startMarker.column=this.scanner.index-this.scanner.lineStart;}var next=this.scanner.lex();this.hasLineTerminator=token.lineNumber!==next.lineNumber;if(next&&this.context.strict&&next.type===3/* Identifier */){if(this.scanner.isStrictModeReservedWord(next.value)){next.type=4/* Keyword */;}}this.lookahead=next;if(this.config.tokens&&next.type!==2/* EOF */){this.tokens.push(this.convertToken(next));}return token;};Parser.prototype.nextRegexToken=function(){this.collectComments();var token=this.scanner.scanRegExp();if(this.config.tokens){// Pop the previous token, '/' or '/='
  // This is added from the lookahead token.
  this.tokens.pop();this.tokens.push(this.convertToken(token));}// Prime the next lookahead.
  this.lookahead=token;this.nextToken();return token;};Parser.prototype.createNode=function(){return {index:this.startMarker.index,line:this.startMarker.line,column:this.startMarker.column};};Parser.prototype.startNode=function(token,lastLineStart){if(lastLineStart===void 0){lastLineStart=0;}var column=token.start-token.lineStart;var line=token.lineNumber;if(column<0){column+=lastLineStart;line--;}return {index:token.start,line:line,column:column};};Parser.prototype.finalize=function(marker,node){if(this.config.range){node.range=[marker.index,this.lastMarker.index];}if(this.config.loc){node.loc={start:{line:marker.line,column:marker.column},end:{line:this.lastMarker.line,column:this.lastMarker.column}};if(this.config.source){node.loc.source=this.config.source;}}if(this.delegate){var metadata={start:{line:marker.line,column:marker.column,offset:marker.index},end:{line:this.lastMarker.line,column:this.lastMarker.column,offset:this.lastMarker.index}};this.delegate(node,metadata);}return node;};// Expect the next token to match the specified punctuator.
  // If not, an exception will be thrown.
  Parser.prototype.expect=function(value){var token=this.nextToken();if(token.type!==7/* Punctuator */||token.value!==value){this.throwUnexpectedToken(token);}};// Quietly expect a comma when in tolerant mode, otherwise delegates to expect().
  Parser.prototype.expectCommaSeparator=function(){if(this.config.tolerant){var token=this.lookahead;if(token.type===7/* Punctuator */&&token.value===','){this.nextToken();}else if(token.type===7/* Punctuator */&&token.value===';'){this.nextToken();this.tolerateUnexpectedToken(token);}else{this.tolerateUnexpectedToken(token,messages_1.Messages.UnexpectedToken);}}else{this.expect(',');}};// Expect the next token to match the specified keyword.
  // If not, an exception will be thrown.
  Parser.prototype.expectKeyword=function(keyword){var token=this.nextToken();if(token.type!==4/* Keyword */||token.value!==keyword){this.throwUnexpectedToken(token);}};// Return true if the next token matches the specified punctuator.
  Parser.prototype.match=function(value){return this.lookahead.type===7/* Punctuator */&&this.lookahead.value===value;};// Return true if the next token matches the specified keyword
  Parser.prototype.matchKeyword=function(keyword){return this.lookahead.type===4/* Keyword */&&this.lookahead.value===keyword;};// Return true if the next token matches the specified contextual keyword
  // (where an identifier is sometimes a keyword depending on the context)
  Parser.prototype.matchContextualKeyword=function(keyword){return this.lookahead.type===3/* Identifier */&&this.lookahead.value===keyword;};// Return true if the next token is an assignment operator
  Parser.prototype.matchAssign=function(){if(this.lookahead.type!==7/* Punctuator */){return false;}var op=this.lookahead.value;return op==='='||op==='*='||op==='**='||op==='/='||op==='%='||op==='+='||op==='-='||op==='<<='||op==='>>='||op==='>>>='||op==='&='||op==='^='||op==='|=';};// Cover grammar support.
  //
  // When an assignment expression position starts with an left parenthesis, the determination of the type
  // of the syntax is to be deferred arbitrarily long until the end of the parentheses pair (plus a lookahead)
  // or the first comma. This situation also defers the determination of all the expressions nested in the pair.
  //
  // There are three productions that can be parsed in a parentheses pair that needs to be determined
  // after the outermost pair is closed. They are:
  //
  //   1. AssignmentExpression
  //   2. BindingElements
  //   3. AssignmentTargets
  //
  // In order to avoid exponential backtracking, we use two flags to denote if the production can be
  // binding element or assignment target.
  //
  // The three productions have the relationship:
  //
  //   BindingElements ⊆ AssignmentTargets ⊆ AssignmentExpression
  //
  // with a single exception that CoverInitializedName when used directly in an Expression, generates
  // an early error. Therefore, we need the third state, firstCoverInitializedNameError, to track the
  // first usage of CoverInitializedName and report it when we reached the end of the parentheses pair.
  //
  // isolateCoverGrammar function runs the given parser function with a new cover grammar context, and it does not
  // effect the current flags. This means the production the parser parses is only used as an expression. Therefore
  // the CoverInitializedName check is conducted.
  //
  // inheritCoverGrammar function runs the given parse function with a new cover grammar context, and it propagates
  // the flags outside of the parser. This means the production the parser parses is used as a part of a potential
  // pattern. The CoverInitializedName check is deferred.
  Parser.prototype.isolateCoverGrammar=function(parseFunction){var previousIsBindingElement=this.context.isBindingElement;var previousIsAssignmentTarget=this.context.isAssignmentTarget;var previousFirstCoverInitializedNameError=this.context.firstCoverInitializedNameError;this.context.isBindingElement=true;this.context.isAssignmentTarget=true;this.context.firstCoverInitializedNameError=null;var result=parseFunction.call(this);if(this.context.firstCoverInitializedNameError!==null){this.throwUnexpectedToken(this.context.firstCoverInitializedNameError);}this.context.isBindingElement=previousIsBindingElement;this.context.isAssignmentTarget=previousIsAssignmentTarget;this.context.firstCoverInitializedNameError=previousFirstCoverInitializedNameError;return result;};Parser.prototype.inheritCoverGrammar=function(parseFunction){var previousIsBindingElement=this.context.isBindingElement;var previousIsAssignmentTarget=this.context.isAssignmentTarget;var previousFirstCoverInitializedNameError=this.context.firstCoverInitializedNameError;this.context.isBindingElement=true;this.context.isAssignmentTarget=true;this.context.firstCoverInitializedNameError=null;var result=parseFunction.call(this);this.context.isBindingElement=this.context.isBindingElement&&previousIsBindingElement;this.context.isAssignmentTarget=this.context.isAssignmentTarget&&previousIsAssignmentTarget;this.context.firstCoverInitializedNameError=previousFirstCoverInitializedNameError||this.context.firstCoverInitializedNameError;return result;};Parser.prototype.consumeSemicolon=function(){if(this.match(';')){this.nextToken();}else if(!this.hasLineTerminator){if(this.lookahead.type!==2/* EOF */&&!this.match('}')){this.throwUnexpectedToken(this.lookahead);}this.lastMarker.index=this.startMarker.index;this.lastMarker.line=this.startMarker.line;this.lastMarker.column=this.startMarker.column;}};// https://tc39.github.io/ecma262/#sec-primary-expression
  Parser.prototype.parsePrimaryExpression=function(){var node=this.createNode();var expr;var token,raw;switch(this.lookahead.type){case 3/* Identifier */:if((this.context.isModule||this.context.await)&&this.lookahead.value==='await'){this.tolerateUnexpectedToken(this.lookahead);}expr=this.matchAsyncFunction()?this.parseFunctionExpression():this.finalize(node,new Node.Identifier(this.nextToken().value));break;case 6/* NumericLiteral */:case 8/* StringLiteral */:if(this.context.strict&&this.lookahead.octal){this.tolerateUnexpectedToken(this.lookahead,messages_1.Messages.StrictOctalLiteral);}this.context.isAssignmentTarget=false;this.context.isBindingElement=false;token=this.nextToken();raw=this.getTokenRaw(token);expr=this.finalize(node,new Node.Literal(token.value,raw));break;case 1/* BooleanLiteral */:this.context.isAssignmentTarget=false;this.context.isBindingElement=false;token=this.nextToken();raw=this.getTokenRaw(token);expr=this.finalize(node,new Node.Literal(token.value==='true',raw));break;case 5/* NullLiteral */:this.context.isAssignmentTarget=false;this.context.isBindingElement=false;token=this.nextToken();raw=this.getTokenRaw(token);expr=this.finalize(node,new Node.Literal(null,raw));break;case 10/* Template */:expr=this.parseTemplateLiteral();break;case 7/* Punctuator */:switch(this.lookahead.value){case'(':this.context.isBindingElement=false;expr=this.inheritCoverGrammar(this.parseGroupExpression);break;case'[':expr=this.inheritCoverGrammar(this.parseArrayInitializer);break;case'{':expr=this.inheritCoverGrammar(this.parseObjectInitializer);break;case'/':case'/=':this.context.isAssignmentTarget=false;this.context.isBindingElement=false;this.scanner.index=this.startMarker.index;token=this.nextRegexToken();raw=this.getTokenRaw(token);expr=this.finalize(node,new Node.RegexLiteral(token.regex,raw,token.pattern,token.flags));break;default:expr=this.throwUnexpectedToken(this.nextToken());}break;case 4/* Keyword */:if(!this.context.strict&&this.context.allowYield&&this.matchKeyword('yield')){expr=this.parseIdentifierName();}else if(!this.context.strict&&this.matchKeyword('let')){expr=this.finalize(node,new Node.Identifier(this.nextToken().value));}else{this.context.isAssignmentTarget=false;this.context.isBindingElement=false;if(this.matchKeyword('function')){expr=this.parseFunctionExpression();}else if(this.matchKeyword('this')){this.nextToken();expr=this.finalize(node,new Node.ThisExpression());}else if(this.matchKeyword('class')){expr=this.parseClassExpression();}else{expr=this.throwUnexpectedToken(this.nextToken());}}break;default:expr=this.throwUnexpectedToken(this.nextToken());}return expr;};// https://tc39.github.io/ecma262/#sec-array-initializer
  Parser.prototype.parseSpreadElement=function(){var node=this.createNode();this.expect('...');var arg=this.inheritCoverGrammar(this.parseAssignmentExpression);return this.finalize(node,new Node.SpreadElement(arg));};Parser.prototype.parseArrayInitializer=function(){var node=this.createNode();var elements=[];this.expect('[');while(!this.match(']')){if(this.match(',')){this.nextToken();elements.push(null);}else if(this.match('...')){var element=this.parseSpreadElement();if(!this.match(']')){this.context.isAssignmentTarget=false;this.context.isBindingElement=false;this.expect(',');}elements.push(element);}else{elements.push(this.inheritCoverGrammar(this.parseAssignmentExpression));if(!this.match(']')){this.expect(',');}}}this.expect(']');return this.finalize(node,new Node.ArrayExpression(elements));};// https://tc39.github.io/ecma262/#sec-object-initializer
  Parser.prototype.parsePropertyMethod=function(params){this.context.isAssignmentTarget=false;this.context.isBindingElement=false;var previousStrict=this.context.strict;var previousAllowStrictDirective=this.context.allowStrictDirective;this.context.allowStrictDirective=params.simple;var body=this.isolateCoverGrammar(this.parseFunctionSourceElements);if(this.context.strict&&params.firstRestricted){this.tolerateUnexpectedToken(params.firstRestricted,params.message);}if(this.context.strict&&params.stricted){this.tolerateUnexpectedToken(params.stricted,params.message);}this.context.strict=previousStrict;this.context.allowStrictDirective=previousAllowStrictDirective;return body;};Parser.prototype.parsePropertyMethodFunction=function(){var isGenerator=false;var node=this.createNode();var previousAllowYield=this.context.allowYield;this.context.allowYield=true;var params=this.parseFormalParameters();var method=this.parsePropertyMethod(params);this.context.allowYield=previousAllowYield;return this.finalize(node,new Node.FunctionExpression(null,params.params,method,isGenerator));};Parser.prototype.parsePropertyMethodAsyncFunction=function(){var node=this.createNode();var previousAllowYield=this.context.allowYield;var previousAwait=this.context.await;this.context.allowYield=false;this.context.await=true;var params=this.parseFormalParameters();var method=this.parsePropertyMethod(params);this.context.allowYield=previousAllowYield;this.context.await=previousAwait;return this.finalize(node,new Node.AsyncFunctionExpression(null,params.params,method));};Parser.prototype.parseObjectPropertyKey=function(){var node=this.createNode();var token=this.nextToken();var key;switch(token.type){case 8/* StringLiteral */:case 6/* NumericLiteral */:if(this.context.strict&&token.octal){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictOctalLiteral);}var raw=this.getTokenRaw(token);key=this.finalize(node,new Node.Literal(token.value,raw));break;case 3/* Identifier */:case 1/* BooleanLiteral */:case 5/* NullLiteral */:case 4/* Keyword */:key=this.finalize(node,new Node.Identifier(token.value));break;case 7/* Punctuator */:if(token.value==='['){key=this.isolateCoverGrammar(this.parseAssignmentExpression);this.expect(']');}else{key=this.throwUnexpectedToken(token);}break;default:key=this.throwUnexpectedToken(token);}return key;};Parser.prototype.isPropertyKey=function(key,value){return key.type===syntax_1.Syntax.Identifier&&key.name===value||key.type===syntax_1.Syntax.Literal&&key.value===value;};Parser.prototype.parseObjectProperty=function(hasProto){var node=this.createNode();var token=this.lookahead;var kind;var key=null;var value=null;var computed=false;var method=false;var shorthand=false;var isAsync=false;if(token.type===3/* Identifier */){var id=token.value;this.nextToken();computed=this.match('[');isAsync=!this.hasLineTerminator&&id==='async'&&!this.match(':')&&!this.match('(')&&!this.match('*')&&!this.match(',');key=isAsync?this.parseObjectPropertyKey():this.finalize(node,new Node.Identifier(id));}else if(this.match('*')){this.nextToken();}else{computed=this.match('[');key=this.parseObjectPropertyKey();}var lookaheadPropertyKey=this.qualifiedPropertyName(this.lookahead);if(token.type===3/* Identifier */&&!isAsync&&token.value==='get'&&lookaheadPropertyKey){kind='get';computed=this.match('[');key=this.parseObjectPropertyKey();this.context.allowYield=false;value=this.parseGetterMethod();}else if(token.type===3/* Identifier */&&!isAsync&&token.value==='set'&&lookaheadPropertyKey){kind='set';computed=this.match('[');key=this.parseObjectPropertyKey();value=this.parseSetterMethod();}else if(token.type===7/* Punctuator */&&token.value==='*'&&lookaheadPropertyKey){kind='init';computed=this.match('[');key=this.parseObjectPropertyKey();value=this.parseGeneratorMethod();method=true;}else{if(!key){this.throwUnexpectedToken(this.lookahead);}kind='init';if(this.match(':')&&!isAsync){if(!computed&&this.isPropertyKey(key,'__proto__')){if(hasProto.value){this.tolerateError(messages_1.Messages.DuplicateProtoProperty);}hasProto.value=true;}this.nextToken();value=this.inheritCoverGrammar(this.parseAssignmentExpression);}else if(this.match('(')){value=isAsync?this.parsePropertyMethodAsyncFunction():this.parsePropertyMethodFunction();method=true;}else if(token.type===3/* Identifier */){var id=this.finalize(node,new Node.Identifier(token.value));if(this.match('=')){this.context.firstCoverInitializedNameError=this.lookahead;this.nextToken();shorthand=true;var init=this.isolateCoverGrammar(this.parseAssignmentExpression);value=this.finalize(node,new Node.AssignmentPattern(id,init));}else{shorthand=true;value=id;}}else{this.throwUnexpectedToken(this.nextToken());}}return this.finalize(node,new Node.Property(kind,key,computed,value,method,shorthand));};Parser.prototype.parseObjectInitializer=function(){var node=this.createNode();this.expect('{');var properties=[];var hasProto={value:false};while(!this.match('}')){properties.push(this.parseObjectProperty(hasProto));if(!this.match('}')){this.expectCommaSeparator();}}this.expect('}');return this.finalize(node,new Node.ObjectExpression(properties));};// https://tc39.github.io/ecma262/#sec-template-literals
  Parser.prototype.parseTemplateHead=function(){assert_1.assert(this.lookahead.head,'Template literal must start with a template head');var node=this.createNode();var token=this.nextToken();var raw=token.value;var cooked=token.cooked;return this.finalize(node,new Node.TemplateElement({raw:raw,cooked:cooked},token.tail));};Parser.prototype.parseTemplateElement=function(){if(this.lookahead.type!==10/* Template */){this.throwUnexpectedToken();}var node=this.createNode();var token=this.nextToken();var raw=token.value;var cooked=token.cooked;return this.finalize(node,new Node.TemplateElement({raw:raw,cooked:cooked},token.tail));};Parser.prototype.parseTemplateLiteral=function(){var node=this.createNode();var expressions=[];var quasis=[];var quasi=this.parseTemplateHead();quasis.push(quasi);while(!quasi.tail){expressions.push(this.parseExpression());quasi=this.parseTemplateElement();quasis.push(quasi);}return this.finalize(node,new Node.TemplateLiteral(quasis,expressions));};// https://tc39.github.io/ecma262/#sec-grouping-operator
  Parser.prototype.reinterpretExpressionAsPattern=function(expr){switch(expr.type){case syntax_1.Syntax.Identifier:case syntax_1.Syntax.MemberExpression:case syntax_1.Syntax.RestElement:case syntax_1.Syntax.AssignmentPattern:break;case syntax_1.Syntax.SpreadElement:expr.type=syntax_1.Syntax.RestElement;this.reinterpretExpressionAsPattern(expr.argument);break;case syntax_1.Syntax.ArrayExpression:expr.type=syntax_1.Syntax.ArrayPattern;for(var i=0;i<expr.elements.length;i++){if(expr.elements[i]!==null){this.reinterpretExpressionAsPattern(expr.elements[i]);}}break;case syntax_1.Syntax.ObjectExpression:expr.type=syntax_1.Syntax.ObjectPattern;for(var i=0;i<expr.properties.length;i++){this.reinterpretExpressionAsPattern(expr.properties[i].value);}break;case syntax_1.Syntax.AssignmentExpression:expr.type=syntax_1.Syntax.AssignmentPattern;delete expr.operator;this.reinterpretExpressionAsPattern(expr.left);break;default:// Allow other node type for tolerant parsing.
  break;}};Parser.prototype.parseGroupExpression=function(){var expr;this.expect('(');if(this.match(')')){this.nextToken();if(!this.match('=>')){this.expect('=>');}expr={type:ArrowParameterPlaceHolder,params:[],async:false};}else{var startToken=this.lookahead;var params=[];if(this.match('...')){expr=this.parseRestElement(params);this.expect(')');if(!this.match('=>')){this.expect('=>');}expr={type:ArrowParameterPlaceHolder,params:[expr],async:false};}else{var arrow=false;this.context.isBindingElement=true;expr=this.inheritCoverGrammar(this.parseAssignmentExpression);if(this.match(',')){var expressions=[];this.context.isAssignmentTarget=false;expressions.push(expr);while(this.lookahead.type!==2/* EOF */){if(!this.match(',')){break;}this.nextToken();if(this.match(')')){this.nextToken();for(var i=0;i<expressions.length;i++){this.reinterpretExpressionAsPattern(expressions[i]);}arrow=true;expr={type:ArrowParameterPlaceHolder,params:expressions,async:false};}else if(this.match('...')){if(!this.context.isBindingElement){this.throwUnexpectedToken(this.lookahead);}expressions.push(this.parseRestElement(params));this.expect(')');if(!this.match('=>')){this.expect('=>');}this.context.isBindingElement=false;for(var i=0;i<expressions.length;i++){this.reinterpretExpressionAsPattern(expressions[i]);}arrow=true;expr={type:ArrowParameterPlaceHolder,params:expressions,async:false};}else{expressions.push(this.inheritCoverGrammar(this.parseAssignmentExpression));}if(arrow){break;}}if(!arrow){expr=this.finalize(this.startNode(startToken),new Node.SequenceExpression(expressions));}}if(!arrow){this.expect(')');if(this.match('=>')){if(expr.type===syntax_1.Syntax.Identifier&&expr.name==='yield'){arrow=true;expr={type:ArrowParameterPlaceHolder,params:[expr],async:false};}if(!arrow){if(!this.context.isBindingElement){this.throwUnexpectedToken(this.lookahead);}if(expr.type===syntax_1.Syntax.SequenceExpression){for(var i=0;i<expr.expressions.length;i++){this.reinterpretExpressionAsPattern(expr.expressions[i]);}}else{this.reinterpretExpressionAsPattern(expr);}var parameters=expr.type===syntax_1.Syntax.SequenceExpression?expr.expressions:[expr];expr={type:ArrowParameterPlaceHolder,params:parameters,async:false};}}this.context.isBindingElement=false;}}}return expr;};// https://tc39.github.io/ecma262/#sec-left-hand-side-expressions
  Parser.prototype.parseArguments=function(){this.expect('(');var args=[];if(!this.match(')')){while(true){var expr=this.match('...')?this.parseSpreadElement():this.isolateCoverGrammar(this.parseAssignmentExpression);args.push(expr);if(this.match(')')){break;}this.expectCommaSeparator();if(this.match(')')){break;}}}this.expect(')');return args;};Parser.prototype.isIdentifierName=function(token){return token.type===3/* Identifier */||token.type===4/* Keyword */||token.type===1/* BooleanLiteral */||token.type===5/* NullLiteral */;};Parser.prototype.parseIdentifierName=function(){var node=this.createNode();var token=this.nextToken();if(!this.isIdentifierName(token)){this.throwUnexpectedToken(token);}return this.finalize(node,new Node.Identifier(token.value));};Parser.prototype.parseNewExpression=function(){var node=this.createNode();var id=this.parseIdentifierName();assert_1.assert(id.name==='new','New expression must start with `new`');var expr;if(this.match('.')){this.nextToken();if(this.lookahead.type===3/* Identifier */&&this.context.inFunctionBody&&this.lookahead.value==='target'){var property=this.parseIdentifierName();expr=new Node.MetaProperty(id,property);}else{this.throwUnexpectedToken(this.lookahead);}}else{var callee=this.isolateCoverGrammar(this.parseLeftHandSideExpression);var args=this.match('(')?this.parseArguments():[];expr=new Node.NewExpression(callee,args);this.context.isAssignmentTarget=false;this.context.isBindingElement=false;}return this.finalize(node,expr);};Parser.prototype.parseAsyncArgument=function(){var arg=this.parseAssignmentExpression();this.context.firstCoverInitializedNameError=null;return arg;};Parser.prototype.parseAsyncArguments=function(){this.expect('(');var args=[];if(!this.match(')')){while(true){var expr=this.match('...')?this.parseSpreadElement():this.isolateCoverGrammar(this.parseAsyncArgument);args.push(expr);if(this.match(')')){break;}this.expectCommaSeparator();if(this.match(')')){break;}}}this.expect(')');return args;};Parser.prototype.parseLeftHandSideExpressionAllowCall=function(){var startToken=this.lookahead;var maybeAsync=this.matchContextualKeyword('async');var previousAllowIn=this.context.allowIn;this.context.allowIn=true;var expr;if(this.matchKeyword('super')&&this.context.inFunctionBody){expr=this.createNode();this.nextToken();expr=this.finalize(expr,new Node.Super());if(!this.match('(')&&!this.match('.')&&!this.match('[')){this.throwUnexpectedToken(this.lookahead);}}else{expr=this.inheritCoverGrammar(this.matchKeyword('new')?this.parseNewExpression:this.parsePrimaryExpression);}while(true){if(this.match('.')){this.context.isBindingElement=false;this.context.isAssignmentTarget=true;this.expect('.');var property=this.parseIdentifierName();expr=this.finalize(this.startNode(startToken),new Node.StaticMemberExpression(expr,property));}else if(this.match('(')){var asyncArrow=maybeAsync&&startToken.lineNumber===this.lookahead.lineNumber;this.context.isBindingElement=false;this.context.isAssignmentTarget=false;var args=asyncArrow?this.parseAsyncArguments():this.parseArguments();expr=this.finalize(this.startNode(startToken),new Node.CallExpression(expr,args));if(asyncArrow&&this.match('=>')){for(var i=0;i<args.length;++i){this.reinterpretExpressionAsPattern(args[i]);}expr={type:ArrowParameterPlaceHolder,params:args,async:true};}}else if(this.match('[')){this.context.isBindingElement=false;this.context.isAssignmentTarget=true;this.expect('[');var property=this.isolateCoverGrammar(this.parseExpression);this.expect(']');expr=this.finalize(this.startNode(startToken),new Node.ComputedMemberExpression(expr,property));}else if(this.lookahead.type===10/* Template */&&this.lookahead.head){var quasi=this.parseTemplateLiteral();expr=this.finalize(this.startNode(startToken),new Node.TaggedTemplateExpression(expr,quasi));}else{break;}}this.context.allowIn=previousAllowIn;return expr;};Parser.prototype.parseSuper=function(){var node=this.createNode();this.expectKeyword('super');if(!this.match('[')&&!this.match('.')){this.throwUnexpectedToken(this.lookahead);}return this.finalize(node,new Node.Super());};Parser.prototype.parseLeftHandSideExpression=function(){assert_1.assert(this.context.allowIn,'callee of new expression always allow in keyword.');var node=this.startNode(this.lookahead);var expr=this.matchKeyword('super')&&this.context.inFunctionBody?this.parseSuper():this.inheritCoverGrammar(this.matchKeyword('new')?this.parseNewExpression:this.parsePrimaryExpression);while(true){if(this.match('[')){this.context.isBindingElement=false;this.context.isAssignmentTarget=true;this.expect('[');var property=this.isolateCoverGrammar(this.parseExpression);this.expect(']');expr=this.finalize(node,new Node.ComputedMemberExpression(expr,property));}else if(this.match('.')){this.context.isBindingElement=false;this.context.isAssignmentTarget=true;this.expect('.');var property=this.parseIdentifierName();expr=this.finalize(node,new Node.StaticMemberExpression(expr,property));}else if(this.lookahead.type===10/* Template */&&this.lookahead.head){var quasi=this.parseTemplateLiteral();expr=this.finalize(node,new Node.TaggedTemplateExpression(expr,quasi));}else{break;}}return expr;};// https://tc39.github.io/ecma262/#sec-update-expressions
  Parser.prototype.parseUpdateExpression=function(){var expr;var startToken=this.lookahead;if(this.match('++')||this.match('--')){var node=this.startNode(startToken);var token=this.nextToken();expr=this.inheritCoverGrammar(this.parseUnaryExpression);if(this.context.strict&&expr.type===syntax_1.Syntax.Identifier&&this.scanner.isRestrictedWord(expr.name)){this.tolerateError(messages_1.Messages.StrictLHSPrefix);}if(!this.context.isAssignmentTarget){this.tolerateError(messages_1.Messages.InvalidLHSInAssignment);}var prefix=true;expr=this.finalize(node,new Node.UpdateExpression(token.value,expr,prefix));this.context.isAssignmentTarget=false;this.context.isBindingElement=false;}else{expr=this.inheritCoverGrammar(this.parseLeftHandSideExpressionAllowCall);if(!this.hasLineTerminator&&this.lookahead.type===7/* Punctuator */){if(this.match('++')||this.match('--')){if(this.context.strict&&expr.type===syntax_1.Syntax.Identifier&&this.scanner.isRestrictedWord(expr.name)){this.tolerateError(messages_1.Messages.StrictLHSPostfix);}if(!this.context.isAssignmentTarget){this.tolerateError(messages_1.Messages.InvalidLHSInAssignment);}this.context.isAssignmentTarget=false;this.context.isBindingElement=false;var operator=this.nextToken().value;var prefix=false;expr=this.finalize(this.startNode(startToken),new Node.UpdateExpression(operator,expr,prefix));}}}return expr;};// https://tc39.github.io/ecma262/#sec-unary-operators
  Parser.prototype.parseAwaitExpression=function(){var node=this.createNode();this.nextToken();var argument=this.parseUnaryExpression();return this.finalize(node,new Node.AwaitExpression(argument));};Parser.prototype.parseUnaryExpression=function(){var expr;if(this.match('+')||this.match('-')||this.match('~')||this.match('!')||this.matchKeyword('delete')||this.matchKeyword('void')||this.matchKeyword('typeof')){var node=this.startNode(this.lookahead);var token=this.nextToken();expr=this.inheritCoverGrammar(this.parseUnaryExpression);expr=this.finalize(node,new Node.UnaryExpression(token.value,expr));if(this.context.strict&&expr.operator==='delete'&&expr.argument.type===syntax_1.Syntax.Identifier){this.tolerateError(messages_1.Messages.StrictDelete);}this.context.isAssignmentTarget=false;this.context.isBindingElement=false;}else if(this.context.await&&this.matchContextualKeyword('await')){expr=this.parseAwaitExpression();}else{expr=this.parseUpdateExpression();}return expr;};Parser.prototype.parseExponentiationExpression=function(){var startToken=this.lookahead;var expr=this.inheritCoverGrammar(this.parseUnaryExpression);if(expr.type!==syntax_1.Syntax.UnaryExpression&&this.match('**')){this.nextToken();this.context.isAssignmentTarget=false;this.context.isBindingElement=false;var left=expr;var right=this.isolateCoverGrammar(this.parseExponentiationExpression);expr=this.finalize(this.startNode(startToken),new Node.BinaryExpression('**',left,right));}return expr;};// https://tc39.github.io/ecma262/#sec-exp-operator
  // https://tc39.github.io/ecma262/#sec-multiplicative-operators
  // https://tc39.github.io/ecma262/#sec-additive-operators
  // https://tc39.github.io/ecma262/#sec-bitwise-shift-operators
  // https://tc39.github.io/ecma262/#sec-relational-operators
  // https://tc39.github.io/ecma262/#sec-equality-operators
  // https://tc39.github.io/ecma262/#sec-binary-bitwise-operators
  // https://tc39.github.io/ecma262/#sec-binary-logical-operators
  Parser.prototype.binaryPrecedence=function(token){var op=token.value;var precedence;if(token.type===7/* Punctuator */){precedence=this.operatorPrecedence[op]||0;}else if(token.type===4/* Keyword */){precedence=op==='instanceof'||this.context.allowIn&&op==='in'?7:0;}else{precedence=0;}return precedence;};Parser.prototype.parseBinaryExpression=function(){var startToken=this.lookahead;var expr=this.inheritCoverGrammar(this.parseExponentiationExpression);var token=this.lookahead;var prec=this.binaryPrecedence(token);if(prec>0){this.nextToken();this.context.isAssignmentTarget=false;this.context.isBindingElement=false;var markers=[startToken,this.lookahead];var left=expr;var right=this.isolateCoverGrammar(this.parseExponentiationExpression);var stack=[left,token.value,right];var precedences=[prec];while(true){prec=this.binaryPrecedence(this.lookahead);if(prec<=0){break;}// Reduce: make a binary expression from the three topmost entries.
  while(stack.length>2&&prec<=precedences[precedences.length-1]){right=stack.pop();var operator=stack.pop();precedences.pop();left=stack.pop();markers.pop();var node=this.startNode(markers[markers.length-1]);stack.push(this.finalize(node,new Node.BinaryExpression(operator,left,right)));}// Shift.
  stack.push(this.nextToken().value);precedences.push(prec);markers.push(this.lookahead);stack.push(this.isolateCoverGrammar(this.parseExponentiationExpression));}// Final reduce to clean-up the stack.
  var i=stack.length-1;expr=stack[i];var lastMarker=markers.pop();while(i>1){var marker=markers.pop();var lastLineStart=lastMarker&&lastMarker.lineStart;var node=this.startNode(marker,lastLineStart);var operator=stack[i-1];expr=this.finalize(node,new Node.BinaryExpression(operator,stack[i-2],expr));i-=2;lastMarker=marker;}}return expr;};// https://tc39.github.io/ecma262/#sec-conditional-operator
  Parser.prototype.parseConditionalExpression=function(){var startToken=this.lookahead;var expr=this.inheritCoverGrammar(this.parseBinaryExpression);if(this.match('?')){this.nextToken();var previousAllowIn=this.context.allowIn;this.context.allowIn=true;var consequent=this.isolateCoverGrammar(this.parseAssignmentExpression);this.context.allowIn=previousAllowIn;this.expect(':');var alternate=this.isolateCoverGrammar(this.parseAssignmentExpression);expr=this.finalize(this.startNode(startToken),new Node.ConditionalExpression(expr,consequent,alternate));this.context.isAssignmentTarget=false;this.context.isBindingElement=false;}return expr;};// https://tc39.github.io/ecma262/#sec-assignment-operators
  Parser.prototype.checkPatternParam=function(options,param){switch(param.type){case syntax_1.Syntax.Identifier:this.validateParam(options,param,param.name);break;case syntax_1.Syntax.RestElement:this.checkPatternParam(options,param.argument);break;case syntax_1.Syntax.AssignmentPattern:this.checkPatternParam(options,param.left);break;case syntax_1.Syntax.ArrayPattern:for(var i=0;i<param.elements.length;i++){if(param.elements[i]!==null){this.checkPatternParam(options,param.elements[i]);}}break;case syntax_1.Syntax.ObjectPattern:for(var i=0;i<param.properties.length;i++){this.checkPatternParam(options,param.properties[i].value);}break;default:break;}options.simple=options.simple&&param instanceof Node.Identifier;};Parser.prototype.reinterpretAsCoverFormalsList=function(expr){var params=[expr];var options;var asyncArrow=false;switch(expr.type){case syntax_1.Syntax.Identifier:break;case ArrowParameterPlaceHolder:params=expr.params;asyncArrow=expr.async;break;default:return null;}options={simple:true,paramSet:{}};for(var i=0;i<params.length;++i){var param=params[i];if(param.type===syntax_1.Syntax.AssignmentPattern){if(param.right.type===syntax_1.Syntax.YieldExpression){if(param.right.argument){this.throwUnexpectedToken(this.lookahead);}param.right.type=syntax_1.Syntax.Identifier;param.right.name='yield';delete param.right.argument;delete param.right.delegate;}}else if(asyncArrow&&param.type===syntax_1.Syntax.Identifier&&param.name==='await'){this.throwUnexpectedToken(this.lookahead);}this.checkPatternParam(options,param);params[i]=param;}if(this.context.strict||!this.context.allowYield){for(var i=0;i<params.length;++i){var param=params[i];if(param.type===syntax_1.Syntax.YieldExpression){this.throwUnexpectedToken(this.lookahead);}}}if(options.message===messages_1.Messages.StrictParamDupe){var token=this.context.strict?options.stricted:options.firstRestricted;this.throwUnexpectedToken(token,options.message);}return {simple:options.simple,params:params,stricted:options.stricted,firstRestricted:options.firstRestricted,message:options.message};};Parser.prototype.parseAssignmentExpression=function(){var expr;if(!this.context.allowYield&&this.matchKeyword('yield')){expr=this.parseYieldExpression();}else{var startToken=this.lookahead;var token=startToken;expr=this.parseConditionalExpression();if(token.type===3/* Identifier */&&token.lineNumber===this.lookahead.lineNumber&&token.value==='async'){if(this.lookahead.type===3/* Identifier */||this.matchKeyword('yield')){var arg=this.parsePrimaryExpression();this.reinterpretExpressionAsPattern(arg);expr={type:ArrowParameterPlaceHolder,params:[arg],async:true};}}if(expr.type===ArrowParameterPlaceHolder||this.match('=>')){// https://tc39.github.io/ecma262/#sec-arrow-function-definitions
  this.context.isAssignmentTarget=false;this.context.isBindingElement=false;var isAsync=expr.async;var list=this.reinterpretAsCoverFormalsList(expr);if(list){if(this.hasLineTerminator){this.tolerateUnexpectedToken(this.lookahead);}this.context.firstCoverInitializedNameError=null;var previousStrict=this.context.strict;var previousAllowStrictDirective=this.context.allowStrictDirective;this.context.allowStrictDirective=list.simple;var previousAllowYield=this.context.allowYield;var previousAwait=this.context.await;this.context.allowYield=true;this.context.await=isAsync;var node=this.startNode(startToken);this.expect('=>');var body=void 0;if(this.match('{')){var previousAllowIn=this.context.allowIn;this.context.allowIn=true;body=this.parseFunctionSourceElements();this.context.allowIn=previousAllowIn;}else{body=this.isolateCoverGrammar(this.parseAssignmentExpression);}var expression=body.type!==syntax_1.Syntax.BlockStatement;if(this.context.strict&&list.firstRestricted){this.throwUnexpectedToken(list.firstRestricted,list.message);}if(this.context.strict&&list.stricted){this.tolerateUnexpectedToken(list.stricted,list.message);}expr=isAsync?this.finalize(node,new Node.AsyncArrowFunctionExpression(list.params,body,expression)):this.finalize(node,new Node.ArrowFunctionExpression(list.params,body,expression));this.context.strict=previousStrict;this.context.allowStrictDirective=previousAllowStrictDirective;this.context.allowYield=previousAllowYield;this.context.await=previousAwait;}}else{if(this.matchAssign()){if(!this.context.isAssignmentTarget){this.tolerateError(messages_1.Messages.InvalidLHSInAssignment);}if(this.context.strict&&expr.type===syntax_1.Syntax.Identifier){var id=expr;if(this.scanner.isRestrictedWord(id.name)){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictLHSAssignment);}if(this.scanner.isStrictModeReservedWord(id.name)){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictReservedWord);}}if(!this.match('=')){this.context.isAssignmentTarget=false;this.context.isBindingElement=false;}else{this.reinterpretExpressionAsPattern(expr);}token=this.nextToken();var operator=token.value;var right=this.isolateCoverGrammar(this.parseAssignmentExpression);expr=this.finalize(this.startNode(startToken),new Node.AssignmentExpression(operator,expr,right));this.context.firstCoverInitializedNameError=null;}}}return expr;};// https://tc39.github.io/ecma262/#sec-comma-operator
  Parser.prototype.parseExpression=function(){var startToken=this.lookahead;var expr=this.isolateCoverGrammar(this.parseAssignmentExpression);if(this.match(',')){var expressions=[];expressions.push(expr);while(this.lookahead.type!==2/* EOF */){if(!this.match(',')){break;}this.nextToken();expressions.push(this.isolateCoverGrammar(this.parseAssignmentExpression));}expr=this.finalize(this.startNode(startToken),new Node.SequenceExpression(expressions));}return expr;};// https://tc39.github.io/ecma262/#sec-block
  Parser.prototype.parseStatementListItem=function(){var statement;this.context.isAssignmentTarget=true;this.context.isBindingElement=true;if(this.lookahead.type===4/* Keyword */){switch(this.lookahead.value){case'export':if(!this.context.isModule){this.tolerateUnexpectedToken(this.lookahead,messages_1.Messages.IllegalExportDeclaration);}statement=this.parseExportDeclaration();break;case'import':if(!this.context.isModule){this.tolerateUnexpectedToken(this.lookahead,messages_1.Messages.IllegalImportDeclaration);}statement=this.parseImportDeclaration();break;case'const':statement=this.parseLexicalDeclaration({inFor:false});break;case'function':statement=this.parseFunctionDeclaration();break;case'class':statement=this.parseClassDeclaration();break;case'let':statement=this.isLexicalDeclaration()?this.parseLexicalDeclaration({inFor:false}):this.parseStatement();break;default:statement=this.parseStatement();break;}}else{statement=this.parseStatement();}return statement;};Parser.prototype.parseBlock=function(){var node=this.createNode();this.expect('{');var block=[];while(true){if(this.match('}')){break;}block.push(this.parseStatementListItem());}this.expect('}');return this.finalize(node,new Node.BlockStatement(block));};// https://tc39.github.io/ecma262/#sec-let-and-const-declarations
  Parser.prototype.parseLexicalBinding=function(kind,options){var node=this.createNode();var params=[];var id=this.parsePattern(params,kind);if(this.context.strict&&id.type===syntax_1.Syntax.Identifier){if(this.scanner.isRestrictedWord(id.name)){this.tolerateError(messages_1.Messages.StrictVarName);}}var init=null;if(kind==='const'){if(!this.matchKeyword('in')&&!this.matchContextualKeyword('of')){if(this.match('=')){this.nextToken();init=this.isolateCoverGrammar(this.parseAssignmentExpression);}else{this.throwError(messages_1.Messages.DeclarationMissingInitializer,'const');}}}else if(!options.inFor&&id.type!==syntax_1.Syntax.Identifier||this.match('=')){this.expect('=');init=this.isolateCoverGrammar(this.parseAssignmentExpression);}return this.finalize(node,new Node.VariableDeclarator(id,init));};Parser.prototype.parseBindingList=function(kind,options){var list=[this.parseLexicalBinding(kind,options)];while(this.match(',')){this.nextToken();list.push(this.parseLexicalBinding(kind,options));}return list;};Parser.prototype.isLexicalDeclaration=function(){var state=this.scanner.saveState();this.scanner.scanComments();var next=this.scanner.lex();this.scanner.restoreState(state);return next.type===3/* Identifier */||next.type===7/* Punctuator */&&next.value==='['||next.type===7/* Punctuator */&&next.value==='{'||next.type===4/* Keyword */&&next.value==='let'||next.type===4/* Keyword */&&next.value==='yield';};Parser.prototype.parseLexicalDeclaration=function(options){var node=this.createNode();var kind=this.nextToken().value;assert_1.assert(kind==='let'||kind==='const','Lexical declaration must be either let or const');var declarations=this.parseBindingList(kind,options);this.consumeSemicolon();return this.finalize(node,new Node.VariableDeclaration(declarations,kind));};// https://tc39.github.io/ecma262/#sec-destructuring-binding-patterns
  Parser.prototype.parseBindingRestElement=function(params,kind){var node=this.createNode();this.expect('...');var arg=this.parsePattern(params,kind);return this.finalize(node,new Node.RestElement(arg));};Parser.prototype.parseArrayPattern=function(params,kind){var node=this.createNode();this.expect('[');var elements=[];while(!this.match(']')){if(this.match(',')){this.nextToken();elements.push(null);}else{if(this.match('...')){elements.push(this.parseBindingRestElement(params,kind));break;}else{elements.push(this.parsePatternWithDefault(params,kind));}if(!this.match(']')){this.expect(',');}}}this.expect(']');return this.finalize(node,new Node.ArrayPattern(elements));};Parser.prototype.parsePropertyPattern=function(params,kind){var node=this.createNode();var computed=false;var shorthand=false;var method=false;var key;var value;if(this.lookahead.type===3/* Identifier */){var keyToken=this.lookahead;key=this.parseVariableIdentifier();var init=this.finalize(node,new Node.Identifier(keyToken.value));if(this.match('=')){params.push(keyToken);shorthand=true;this.nextToken();var expr=this.parseAssignmentExpression();value=this.finalize(this.startNode(keyToken),new Node.AssignmentPattern(init,expr));}else if(!this.match(':')){params.push(keyToken);shorthand=true;value=init;}else{this.expect(':');value=this.parsePatternWithDefault(params,kind);}}else{computed=this.match('[');key=this.parseObjectPropertyKey();this.expect(':');value=this.parsePatternWithDefault(params,kind);}return this.finalize(node,new Node.Property('init',key,computed,value,method,shorthand));};Parser.prototype.parseObjectPattern=function(params,kind){var node=this.createNode();var properties=[];this.expect('{');while(!this.match('}')){properties.push(this.parsePropertyPattern(params,kind));if(!this.match('}')){this.expect(',');}}this.expect('}');return this.finalize(node,new Node.ObjectPattern(properties));};Parser.prototype.parsePattern=function(params,kind){var pattern;if(this.match('[')){pattern=this.parseArrayPattern(params,kind);}else if(this.match('{')){pattern=this.parseObjectPattern(params,kind);}else{if(this.matchKeyword('let')&&(kind==='const'||kind==='let')){this.tolerateUnexpectedToken(this.lookahead,messages_1.Messages.LetInLexicalBinding);}params.push(this.lookahead);pattern=this.parseVariableIdentifier(kind);}return pattern;};Parser.prototype.parsePatternWithDefault=function(params,kind){var startToken=this.lookahead;var pattern=this.parsePattern(params,kind);if(this.match('=')){this.nextToken();var previousAllowYield=this.context.allowYield;this.context.allowYield=true;var right=this.isolateCoverGrammar(this.parseAssignmentExpression);this.context.allowYield=previousAllowYield;pattern=this.finalize(this.startNode(startToken),new Node.AssignmentPattern(pattern,right));}return pattern;};// https://tc39.github.io/ecma262/#sec-variable-statement
  Parser.prototype.parseVariableIdentifier=function(kind){var node=this.createNode();var token=this.nextToken();if(token.type===4/* Keyword */&&token.value==='yield'){if(this.context.strict){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictReservedWord);}else if(!this.context.allowYield){this.throwUnexpectedToken(token);}}else if(token.type!==3/* Identifier */){if(this.context.strict&&token.type===4/* Keyword */&&this.scanner.isStrictModeReservedWord(token.value)){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictReservedWord);}else{if(this.context.strict||token.value!=='let'||kind!=='var'){this.throwUnexpectedToken(token);}}}else if((this.context.isModule||this.context.await)&&token.type===3/* Identifier */&&token.value==='await'){this.tolerateUnexpectedToken(token);}return this.finalize(node,new Node.Identifier(token.value));};Parser.prototype.parseVariableDeclaration=function(options){var node=this.createNode();var params=[];var id=this.parsePattern(params,'var');if(this.context.strict&&id.type===syntax_1.Syntax.Identifier){if(this.scanner.isRestrictedWord(id.name)){this.tolerateError(messages_1.Messages.StrictVarName);}}var init=null;if(this.match('=')){this.nextToken();init=this.isolateCoverGrammar(this.parseAssignmentExpression);}else if(id.type!==syntax_1.Syntax.Identifier&&!options.inFor){this.expect('=');}return this.finalize(node,new Node.VariableDeclarator(id,init));};Parser.prototype.parseVariableDeclarationList=function(options){var opt={inFor:options.inFor};var list=[];list.push(this.parseVariableDeclaration(opt));while(this.match(',')){this.nextToken();list.push(this.parseVariableDeclaration(opt));}return list;};Parser.prototype.parseVariableStatement=function(){var node=this.createNode();this.expectKeyword('var');var declarations=this.parseVariableDeclarationList({inFor:false});this.consumeSemicolon();return this.finalize(node,new Node.VariableDeclaration(declarations,'var'));};// https://tc39.github.io/ecma262/#sec-empty-statement
  Parser.prototype.parseEmptyStatement=function(){var node=this.createNode();this.expect(';');return this.finalize(node,new Node.EmptyStatement());};// https://tc39.github.io/ecma262/#sec-expression-statement
  Parser.prototype.parseExpressionStatement=function(){var node=this.createNode();var expr=this.parseExpression();this.consumeSemicolon();return this.finalize(node,new Node.ExpressionStatement(expr));};// https://tc39.github.io/ecma262/#sec-if-statement
  Parser.prototype.parseIfClause=function(){if(this.context.strict&&this.matchKeyword('function')){this.tolerateError(messages_1.Messages.StrictFunction);}return this.parseStatement();};Parser.prototype.parseIfStatement=function(){var node=this.createNode();var consequent;var alternate=null;this.expectKeyword('if');this.expect('(');var test=this.parseExpression();if(!this.match(')')&&this.config.tolerant){this.tolerateUnexpectedToken(this.nextToken());consequent=this.finalize(this.createNode(),new Node.EmptyStatement());}else{this.expect(')');consequent=this.parseIfClause();if(this.matchKeyword('else')){this.nextToken();alternate=this.parseIfClause();}}return this.finalize(node,new Node.IfStatement(test,consequent,alternate));};// https://tc39.github.io/ecma262/#sec-do-while-statement
  Parser.prototype.parseDoWhileStatement=function(){var node=this.createNode();this.expectKeyword('do');var previousInIteration=this.context.inIteration;this.context.inIteration=true;var body=this.parseStatement();this.context.inIteration=previousInIteration;this.expectKeyword('while');this.expect('(');var test=this.parseExpression();if(!this.match(')')&&this.config.tolerant){this.tolerateUnexpectedToken(this.nextToken());}else{this.expect(')');if(this.match(';')){this.nextToken();}}return this.finalize(node,new Node.DoWhileStatement(body,test));};// https://tc39.github.io/ecma262/#sec-while-statement
  Parser.prototype.parseWhileStatement=function(){var node=this.createNode();var body;this.expectKeyword('while');this.expect('(');var test=this.parseExpression();if(!this.match(')')&&this.config.tolerant){this.tolerateUnexpectedToken(this.nextToken());body=this.finalize(this.createNode(),new Node.EmptyStatement());}else{this.expect(')');var previousInIteration=this.context.inIteration;this.context.inIteration=true;body=this.parseStatement();this.context.inIteration=previousInIteration;}return this.finalize(node,new Node.WhileStatement(test,body));};// https://tc39.github.io/ecma262/#sec-for-statement
  // https://tc39.github.io/ecma262/#sec-for-in-and-for-of-statements
  Parser.prototype.parseForStatement=function(){var init=null;var test=null;var update=null;var forIn=true;var left,right;var node=this.createNode();this.expectKeyword('for');this.expect('(');if(this.match(';')){this.nextToken();}else{if(this.matchKeyword('var')){init=this.createNode();this.nextToken();var previousAllowIn=this.context.allowIn;this.context.allowIn=false;var declarations=this.parseVariableDeclarationList({inFor:true});this.context.allowIn=previousAllowIn;if(declarations.length===1&&this.matchKeyword('in')){var decl=declarations[0];if(decl.init&&(decl.id.type===syntax_1.Syntax.ArrayPattern||decl.id.type===syntax_1.Syntax.ObjectPattern||this.context.strict)){this.tolerateError(messages_1.Messages.ForInOfLoopInitializer,'for-in');}init=this.finalize(init,new Node.VariableDeclaration(declarations,'var'));this.nextToken();left=init;right=this.parseExpression();init=null;}else if(declarations.length===1&&declarations[0].init===null&&this.matchContextualKeyword('of')){init=this.finalize(init,new Node.VariableDeclaration(declarations,'var'));this.nextToken();left=init;right=this.parseAssignmentExpression();init=null;forIn=false;}else{init=this.finalize(init,new Node.VariableDeclaration(declarations,'var'));this.expect(';');}}else if(this.matchKeyword('const')||this.matchKeyword('let')){init=this.createNode();var kind=this.nextToken().value;if(!this.context.strict&&this.lookahead.value==='in'){init=this.finalize(init,new Node.Identifier(kind));this.nextToken();left=init;right=this.parseExpression();init=null;}else{var previousAllowIn=this.context.allowIn;this.context.allowIn=false;var declarations=this.parseBindingList(kind,{inFor:true});this.context.allowIn=previousAllowIn;if(declarations.length===1&&declarations[0].init===null&&this.matchKeyword('in')){init=this.finalize(init,new Node.VariableDeclaration(declarations,kind));this.nextToken();left=init;right=this.parseExpression();init=null;}else if(declarations.length===1&&declarations[0].init===null&&this.matchContextualKeyword('of')){init=this.finalize(init,new Node.VariableDeclaration(declarations,kind));this.nextToken();left=init;right=this.parseAssignmentExpression();init=null;forIn=false;}else{this.consumeSemicolon();init=this.finalize(init,new Node.VariableDeclaration(declarations,kind));}}}else{var initStartToken=this.lookahead;var previousAllowIn=this.context.allowIn;this.context.allowIn=false;init=this.inheritCoverGrammar(this.parseAssignmentExpression);this.context.allowIn=previousAllowIn;if(this.matchKeyword('in')){if(!this.context.isAssignmentTarget||init.type===syntax_1.Syntax.AssignmentExpression){this.tolerateError(messages_1.Messages.InvalidLHSInForIn);}this.nextToken();this.reinterpretExpressionAsPattern(init);left=init;right=this.parseExpression();init=null;}else if(this.matchContextualKeyword('of')){if(!this.context.isAssignmentTarget||init.type===syntax_1.Syntax.AssignmentExpression){this.tolerateError(messages_1.Messages.InvalidLHSInForLoop);}this.nextToken();this.reinterpretExpressionAsPattern(init);left=init;right=this.parseAssignmentExpression();init=null;forIn=false;}else{if(this.match(',')){var initSeq=[init];while(this.match(',')){this.nextToken();initSeq.push(this.isolateCoverGrammar(this.parseAssignmentExpression));}init=this.finalize(this.startNode(initStartToken),new Node.SequenceExpression(initSeq));}this.expect(';');}}}if(typeof left==='undefined'){if(!this.match(';')){test=this.parseExpression();}this.expect(';');if(!this.match(')')){update=this.parseExpression();}}var body;if(!this.match(')')&&this.config.tolerant){this.tolerateUnexpectedToken(this.nextToken());body=this.finalize(this.createNode(),new Node.EmptyStatement());}else{this.expect(')');var previousInIteration=this.context.inIteration;this.context.inIteration=true;body=this.isolateCoverGrammar(this.parseStatement);this.context.inIteration=previousInIteration;}return typeof left==='undefined'?this.finalize(node,new Node.ForStatement(init,test,update,body)):forIn?this.finalize(node,new Node.ForInStatement(left,right,body)):this.finalize(node,new Node.ForOfStatement(left,right,body));};// https://tc39.github.io/ecma262/#sec-continue-statement
  Parser.prototype.parseContinueStatement=function(){var node=this.createNode();this.expectKeyword('continue');var label=null;if(this.lookahead.type===3/* Identifier */&&!this.hasLineTerminator){var id=this.parseVariableIdentifier();label=id;var key='$'+id.name;if(!Object.prototype.hasOwnProperty.call(this.context.labelSet,key)){this.throwError(messages_1.Messages.UnknownLabel,id.name);}}this.consumeSemicolon();if(label===null&&!this.context.inIteration){this.throwError(messages_1.Messages.IllegalContinue);}return this.finalize(node,new Node.ContinueStatement(label));};// https://tc39.github.io/ecma262/#sec-break-statement
  Parser.prototype.parseBreakStatement=function(){var node=this.createNode();this.expectKeyword('break');var label=null;if(this.lookahead.type===3/* Identifier */&&!this.hasLineTerminator){var id=this.parseVariableIdentifier();var key='$'+id.name;if(!Object.prototype.hasOwnProperty.call(this.context.labelSet,key)){this.throwError(messages_1.Messages.UnknownLabel,id.name);}label=id;}this.consumeSemicolon();if(label===null&&!this.context.inIteration&&!this.context.inSwitch){this.throwError(messages_1.Messages.IllegalBreak);}return this.finalize(node,new Node.BreakStatement(label));};// https://tc39.github.io/ecma262/#sec-return-statement
  Parser.prototype.parseReturnStatement=function(){if(!this.context.inFunctionBody){this.tolerateError(messages_1.Messages.IllegalReturn);}var node=this.createNode();this.expectKeyword('return');var hasArgument=!this.match(';')&&!this.match('}')&&!this.hasLineTerminator&&this.lookahead.type!==2/* EOF */||this.lookahead.type===8/* StringLiteral */||this.lookahead.type===10/* Template */;var argument=hasArgument?this.parseExpression():null;this.consumeSemicolon();return this.finalize(node,new Node.ReturnStatement(argument));};// https://tc39.github.io/ecma262/#sec-with-statement
  Parser.prototype.parseWithStatement=function(){if(this.context.strict){this.tolerateError(messages_1.Messages.StrictModeWith);}var node=this.createNode();var body;this.expectKeyword('with');this.expect('(');var object=this.parseExpression();if(!this.match(')')&&this.config.tolerant){this.tolerateUnexpectedToken(this.nextToken());body=this.finalize(this.createNode(),new Node.EmptyStatement());}else{this.expect(')');body=this.parseStatement();}return this.finalize(node,new Node.WithStatement(object,body));};// https://tc39.github.io/ecma262/#sec-switch-statement
  Parser.prototype.parseSwitchCase=function(){var node=this.createNode();var test;if(this.matchKeyword('default')){this.nextToken();test=null;}else{this.expectKeyword('case');test=this.parseExpression();}this.expect(':');var consequent=[];while(true){if(this.match('}')||this.matchKeyword('default')||this.matchKeyword('case')){break;}consequent.push(this.parseStatementListItem());}return this.finalize(node,new Node.SwitchCase(test,consequent));};Parser.prototype.parseSwitchStatement=function(){var node=this.createNode();this.expectKeyword('switch');this.expect('(');var discriminant=this.parseExpression();this.expect(')');var previousInSwitch=this.context.inSwitch;this.context.inSwitch=true;var cases=[];var defaultFound=false;this.expect('{');while(true){if(this.match('}')){break;}var clause=this.parseSwitchCase();if(clause.test===null){if(defaultFound){this.throwError(messages_1.Messages.MultipleDefaultsInSwitch);}defaultFound=true;}cases.push(clause);}this.expect('}');this.context.inSwitch=previousInSwitch;return this.finalize(node,new Node.SwitchStatement(discriminant,cases));};// https://tc39.github.io/ecma262/#sec-labelled-statements
  Parser.prototype.parseLabelledStatement=function(){var node=this.createNode();var expr=this.parseExpression();var statement;if(expr.type===syntax_1.Syntax.Identifier&&this.match(':')){this.nextToken();var id=expr;var key='$'+id.name;if(Object.prototype.hasOwnProperty.call(this.context.labelSet,key)){this.throwError(messages_1.Messages.Redeclaration,'Label',id.name);}this.context.labelSet[key]=true;var body=void 0;if(this.matchKeyword('class')){this.tolerateUnexpectedToken(this.lookahead);body=this.parseClassDeclaration();}else if(this.matchKeyword('function')){var token=this.lookahead;var declaration=this.parseFunctionDeclaration();if(this.context.strict){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictFunction);}else if(declaration.generator){this.tolerateUnexpectedToken(token,messages_1.Messages.GeneratorInLegacyContext);}body=declaration;}else{body=this.parseStatement();}delete this.context.labelSet[key];statement=new Node.LabeledStatement(id,body);}else{this.consumeSemicolon();statement=new Node.ExpressionStatement(expr);}return this.finalize(node,statement);};// https://tc39.github.io/ecma262/#sec-throw-statement
  Parser.prototype.parseThrowStatement=function(){var node=this.createNode();this.expectKeyword('throw');if(this.hasLineTerminator){this.throwError(messages_1.Messages.NewlineAfterThrow);}var argument=this.parseExpression();this.consumeSemicolon();return this.finalize(node,new Node.ThrowStatement(argument));};// https://tc39.github.io/ecma262/#sec-try-statement
  Parser.prototype.parseCatchClause=function(){var node=this.createNode();this.expectKeyword('catch');this.expect('(');if(this.match(')')){this.throwUnexpectedToken(this.lookahead);}var params=[];var param=this.parsePattern(params);var paramMap={};for(var i=0;i<params.length;i++){var key='$'+params[i].value;if(Object.prototype.hasOwnProperty.call(paramMap,key)){this.tolerateError(messages_1.Messages.DuplicateBinding,params[i].value);}paramMap[key]=true;}if(this.context.strict&&param.type===syntax_1.Syntax.Identifier){if(this.scanner.isRestrictedWord(param.name)){this.tolerateError(messages_1.Messages.StrictCatchVariable);}}this.expect(')');var body=this.parseBlock();return this.finalize(node,new Node.CatchClause(param,body));};Parser.prototype.parseFinallyClause=function(){this.expectKeyword('finally');return this.parseBlock();};Parser.prototype.parseTryStatement=function(){var node=this.createNode();this.expectKeyword('try');var block=this.parseBlock();var handler=this.matchKeyword('catch')?this.parseCatchClause():null;var finalizer=this.matchKeyword('finally')?this.parseFinallyClause():null;if(!handler&&!finalizer){this.throwError(messages_1.Messages.NoCatchOrFinally);}return this.finalize(node,new Node.TryStatement(block,handler,finalizer));};// https://tc39.github.io/ecma262/#sec-debugger-statement
  Parser.prototype.parseDebuggerStatement=function(){var node=this.createNode();this.expectKeyword('debugger');this.consumeSemicolon();return this.finalize(node,new Node.DebuggerStatement());};// https://tc39.github.io/ecma262/#sec-ecmascript-language-statements-and-declarations
  Parser.prototype.parseStatement=function(){var statement;switch(this.lookahead.type){case 1/* BooleanLiteral */:case 5/* NullLiteral */:case 6/* NumericLiteral */:case 8/* StringLiteral */:case 10/* Template */:case 9/* RegularExpression */:statement=this.parseExpressionStatement();break;case 7/* Punctuator */:var value=this.lookahead.value;if(value==='{'){statement=this.parseBlock();}else if(value==='('){statement=this.parseExpressionStatement();}else if(value===';'){statement=this.parseEmptyStatement();}else{statement=this.parseExpressionStatement();}break;case 3/* Identifier */:statement=this.matchAsyncFunction()?this.parseFunctionDeclaration():this.parseLabelledStatement();break;case 4/* Keyword */:switch(this.lookahead.value){case'break':statement=this.parseBreakStatement();break;case'continue':statement=this.parseContinueStatement();break;case'debugger':statement=this.parseDebuggerStatement();break;case'do':statement=this.parseDoWhileStatement();break;case'for':statement=this.parseForStatement();break;case'function':statement=this.parseFunctionDeclaration();break;case'if':statement=this.parseIfStatement();break;case'return':statement=this.parseReturnStatement();break;case'switch':statement=this.parseSwitchStatement();break;case'throw':statement=this.parseThrowStatement();break;case'try':statement=this.parseTryStatement();break;case'var':statement=this.parseVariableStatement();break;case'while':statement=this.parseWhileStatement();break;case'with':statement=this.parseWithStatement();break;default:statement=this.parseExpressionStatement();break;}break;default:statement=this.throwUnexpectedToken(this.lookahead);}return statement;};// https://tc39.github.io/ecma262/#sec-function-definitions
  Parser.prototype.parseFunctionSourceElements=function(){var node=this.createNode();this.expect('{');var body=this.parseDirectivePrologues();var previousLabelSet=this.context.labelSet;var previousInIteration=this.context.inIteration;var previousInSwitch=this.context.inSwitch;var previousInFunctionBody=this.context.inFunctionBody;this.context.labelSet={};this.context.inIteration=false;this.context.inSwitch=false;this.context.inFunctionBody=true;while(this.lookahead.type!==2/* EOF */){if(this.match('}')){break;}body.push(this.parseStatementListItem());}this.expect('}');this.context.labelSet=previousLabelSet;this.context.inIteration=previousInIteration;this.context.inSwitch=previousInSwitch;this.context.inFunctionBody=previousInFunctionBody;return this.finalize(node,new Node.BlockStatement(body));};Parser.prototype.validateParam=function(options,param,name){var key='$'+name;if(this.context.strict){if(this.scanner.isRestrictedWord(name)){options.stricted=param;options.message=messages_1.Messages.StrictParamName;}if(Object.prototype.hasOwnProperty.call(options.paramSet,key)){options.stricted=param;options.message=messages_1.Messages.StrictParamDupe;}}else if(!options.firstRestricted){if(this.scanner.isRestrictedWord(name)){options.firstRestricted=param;options.message=messages_1.Messages.StrictParamName;}else if(this.scanner.isStrictModeReservedWord(name)){options.firstRestricted=param;options.message=messages_1.Messages.StrictReservedWord;}else if(Object.prototype.hasOwnProperty.call(options.paramSet,key)){options.stricted=param;options.message=messages_1.Messages.StrictParamDupe;}}/* istanbul ignore next */if(typeof Object.defineProperty==='function'){Object.defineProperty(options.paramSet,key,{value:true,enumerable:true,writable:true,configurable:true});}else{options.paramSet[key]=true;}};Parser.prototype.parseRestElement=function(params){var node=this.createNode();this.expect('...');var arg=this.parsePattern(params);if(this.match('=')){this.throwError(messages_1.Messages.DefaultRestParameter);}if(!this.match(')')){this.throwError(messages_1.Messages.ParameterAfterRestParameter);}return this.finalize(node,new Node.RestElement(arg));};Parser.prototype.parseFormalParameter=function(options){var params=[];var param=this.match('...')?this.parseRestElement(params):this.parsePatternWithDefault(params);for(var i=0;i<params.length;i++){this.validateParam(options,params[i],params[i].value);}options.simple=options.simple&&param instanceof Node.Identifier;options.params.push(param);};Parser.prototype.parseFormalParameters=function(firstRestricted){var options;options={simple:true,params:[],firstRestricted:firstRestricted};this.expect('(');if(!this.match(')')){options.paramSet={};while(this.lookahead.type!==2/* EOF */){this.parseFormalParameter(options);if(this.match(')')){break;}this.expect(',');if(this.match(')')){break;}}}this.expect(')');return {simple:options.simple,params:options.params,stricted:options.stricted,firstRestricted:options.firstRestricted,message:options.message};};Parser.prototype.matchAsyncFunction=function(){var match=this.matchContextualKeyword('async');if(match){var state=this.scanner.saveState();this.scanner.scanComments();var next=this.scanner.lex();this.scanner.restoreState(state);match=state.lineNumber===next.lineNumber&&next.type===4/* Keyword */&&next.value==='function';}return match;};Parser.prototype.parseFunctionDeclaration=function(identifierIsOptional){var node=this.createNode();var isAsync=this.matchContextualKeyword('async');if(isAsync){this.nextToken();}this.expectKeyword('function');var isGenerator=isAsync?false:this.match('*');if(isGenerator){this.nextToken();}var message;var id=null;var firstRestricted=null;if(!identifierIsOptional||!this.match('(')){var token=this.lookahead;id=this.parseVariableIdentifier();if(this.context.strict){if(this.scanner.isRestrictedWord(token.value)){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictFunctionName);}}else{if(this.scanner.isRestrictedWord(token.value)){firstRestricted=token;message=messages_1.Messages.StrictFunctionName;}else if(this.scanner.isStrictModeReservedWord(token.value)){firstRestricted=token;message=messages_1.Messages.StrictReservedWord;}}}var previousAllowAwait=this.context.await;var previousAllowYield=this.context.allowYield;this.context.await=isAsync;this.context.allowYield=!isGenerator;var formalParameters=this.parseFormalParameters(firstRestricted);var params=formalParameters.params;var stricted=formalParameters.stricted;firstRestricted=formalParameters.firstRestricted;if(formalParameters.message){message=formalParameters.message;}var previousStrict=this.context.strict;var previousAllowStrictDirective=this.context.allowStrictDirective;this.context.allowStrictDirective=formalParameters.simple;var body=this.parseFunctionSourceElements();if(this.context.strict&&firstRestricted){this.throwUnexpectedToken(firstRestricted,message);}if(this.context.strict&&stricted){this.tolerateUnexpectedToken(stricted,message);}this.context.strict=previousStrict;this.context.allowStrictDirective=previousAllowStrictDirective;this.context.await=previousAllowAwait;this.context.allowYield=previousAllowYield;return isAsync?this.finalize(node,new Node.AsyncFunctionDeclaration(id,params,body)):this.finalize(node,new Node.FunctionDeclaration(id,params,body,isGenerator));};Parser.prototype.parseFunctionExpression=function(){var node=this.createNode();var isAsync=this.matchContextualKeyword('async');if(isAsync){this.nextToken();}this.expectKeyword('function');var isGenerator=isAsync?false:this.match('*');if(isGenerator){this.nextToken();}var message;var id=null;var firstRestricted;var previousAllowAwait=this.context.await;var previousAllowYield=this.context.allowYield;this.context.await=isAsync;this.context.allowYield=!isGenerator;if(!this.match('(')){var token=this.lookahead;id=!this.context.strict&&!isGenerator&&this.matchKeyword('yield')?this.parseIdentifierName():this.parseVariableIdentifier();if(this.context.strict){if(this.scanner.isRestrictedWord(token.value)){this.tolerateUnexpectedToken(token,messages_1.Messages.StrictFunctionName);}}else{if(this.scanner.isRestrictedWord(token.value)){firstRestricted=token;message=messages_1.Messages.StrictFunctionName;}else if(this.scanner.isStrictModeReservedWord(token.value)){firstRestricted=token;message=messages_1.Messages.StrictReservedWord;}}}var formalParameters=this.parseFormalParameters(firstRestricted);var params=formalParameters.params;var stricted=formalParameters.stricted;firstRestricted=formalParameters.firstRestricted;if(formalParameters.message){message=formalParameters.message;}var previousStrict=this.context.strict;var previousAllowStrictDirective=this.context.allowStrictDirective;this.context.allowStrictDirective=formalParameters.simple;var body=this.parseFunctionSourceElements();if(this.context.strict&&firstRestricted){this.throwUnexpectedToken(firstRestricted,message);}if(this.context.strict&&stricted){this.tolerateUnexpectedToken(stricted,message);}this.context.strict=previousStrict;this.context.allowStrictDirective=previousAllowStrictDirective;this.context.await=previousAllowAwait;this.context.allowYield=previousAllowYield;return isAsync?this.finalize(node,new Node.AsyncFunctionExpression(id,params,body)):this.finalize(node,new Node.FunctionExpression(id,params,body,isGenerator));};// https://tc39.github.io/ecma262/#sec-directive-prologues-and-the-use-strict-directive
  Parser.prototype.parseDirective=function(){var token=this.lookahead;var node=this.createNode();var expr=this.parseExpression();var directive=expr.type===syntax_1.Syntax.Literal?this.getTokenRaw(token).slice(1,-1):null;this.consumeSemicolon();return this.finalize(node,directive?new Node.Directive(expr,directive):new Node.ExpressionStatement(expr));};Parser.prototype.parseDirectivePrologues=function(){var firstRestricted=null;var body=[];while(true){var token=this.lookahead;if(token.type!==8/* StringLiteral */){break;}var statement=this.parseDirective();body.push(statement);var directive=statement.directive;if(typeof directive!=='string'){break;}if(directive==='use strict'){this.context.strict=true;if(firstRestricted){this.tolerateUnexpectedToken(firstRestricted,messages_1.Messages.StrictOctalLiteral);}if(!this.context.allowStrictDirective){this.tolerateUnexpectedToken(token,messages_1.Messages.IllegalLanguageModeDirective);}}else{if(!firstRestricted&&token.octal){firstRestricted=token;}}}return body;};// https://tc39.github.io/ecma262/#sec-method-definitions
  Parser.prototype.qualifiedPropertyName=function(token){switch(token.type){case 3/* Identifier */:case 8/* StringLiteral */:case 1/* BooleanLiteral */:case 5/* NullLiteral */:case 6/* NumericLiteral */:case 4/* Keyword */:return true;case 7/* Punctuator */:return token.value==='[';default:break;}return false;};Parser.prototype.parseGetterMethod=function(){var node=this.createNode();var isGenerator=false;var previousAllowYield=this.context.allowYield;this.context.allowYield=!isGenerator;var formalParameters=this.parseFormalParameters();if(formalParameters.params.length>0){this.tolerateError(messages_1.Messages.BadGetterArity);}var method=this.parsePropertyMethod(formalParameters);this.context.allowYield=previousAllowYield;return this.finalize(node,new Node.FunctionExpression(null,formalParameters.params,method,isGenerator));};Parser.prototype.parseSetterMethod=function(){var node=this.createNode();var isGenerator=false;var previousAllowYield=this.context.allowYield;this.context.allowYield=!isGenerator;var formalParameters=this.parseFormalParameters();if(formalParameters.params.length!==1){this.tolerateError(messages_1.Messages.BadSetterArity);}else if(formalParameters.params[0]instanceof Node.RestElement){this.tolerateError(messages_1.Messages.BadSetterRestParameter);}var method=this.parsePropertyMethod(formalParameters);this.context.allowYield=previousAllowYield;return this.finalize(node,new Node.FunctionExpression(null,formalParameters.params,method,isGenerator));};Parser.prototype.parseGeneratorMethod=function(){var node=this.createNode();var isGenerator=true;var previousAllowYield=this.context.allowYield;this.context.allowYield=true;var params=this.parseFormalParameters();this.context.allowYield=false;var method=this.parsePropertyMethod(params);this.context.allowYield=previousAllowYield;return this.finalize(node,new Node.FunctionExpression(null,params.params,method,isGenerator));};// https://tc39.github.io/ecma262/#sec-generator-function-definitions
  Parser.prototype.isStartOfExpression=function(){var start=true;var value=this.lookahead.value;switch(this.lookahead.type){case 7/* Punctuator */:start=value==='['||value==='('||value==='{'||value==='+'||value==='-'||value==='!'||value==='~'||value==='++'||value==='--'||value==='/'||value==='/=';// regular expression literal
  break;case 4/* Keyword */:start=value==='class'||value==='delete'||value==='function'||value==='let'||value==='new'||value==='super'||value==='this'||value==='typeof'||value==='void'||value==='yield';break;default:break;}return start;};Parser.prototype.parseYieldExpression=function(){var node=this.createNode();this.expectKeyword('yield');var argument=null;var delegate=false;if(!this.hasLineTerminator){var previousAllowYield=this.context.allowYield;this.context.allowYield=false;delegate=this.match('*');if(delegate){this.nextToken();argument=this.parseAssignmentExpression();}else if(this.isStartOfExpression()){argument=this.parseAssignmentExpression();}this.context.allowYield=previousAllowYield;}return this.finalize(node,new Node.YieldExpression(argument,delegate));};// https://tc39.github.io/ecma262/#sec-class-definitions
  Parser.prototype.parseClassElement=function(hasConstructor){var token=this.lookahead;var node=this.createNode();var kind='';var key=null;var value=null;var computed=false;var method=false;var isStatic=false;var isAsync=false;if(this.match('*')){this.nextToken();}else{computed=this.match('[');key=this.parseObjectPropertyKey();var id=key;if(id.name==='static'&&(this.qualifiedPropertyName(this.lookahead)||this.match('*'))){token=this.lookahead;isStatic=true;computed=this.match('[');if(this.match('*')){this.nextToken();}else{key=this.parseObjectPropertyKey();}}if(token.type===3/* Identifier */&&!this.hasLineTerminator&&token.value==='async'){var punctuator=this.lookahead.value;if(punctuator!==':'&&punctuator!=='('&&punctuator!=='*'){isAsync=true;token=this.lookahead;key=this.parseObjectPropertyKey();if(token.type===3/* Identifier */&&token.value==='constructor'){this.tolerateUnexpectedToken(token,messages_1.Messages.ConstructorIsAsync);}}}}var lookaheadPropertyKey=this.qualifiedPropertyName(this.lookahead);if(token.type===3/* Identifier */){if(token.value==='get'&&lookaheadPropertyKey){kind='get';computed=this.match('[');key=this.parseObjectPropertyKey();this.context.allowYield=false;value=this.parseGetterMethod();}else if(token.value==='set'&&lookaheadPropertyKey){kind='set';computed=this.match('[');key=this.parseObjectPropertyKey();value=this.parseSetterMethod();}}else if(token.type===7/* Punctuator */&&token.value==='*'&&lookaheadPropertyKey){kind='init';computed=this.match('[');key=this.parseObjectPropertyKey();value=this.parseGeneratorMethod();method=true;}if(!kind&&key&&this.match('(')){kind='init';value=isAsync?this.parsePropertyMethodAsyncFunction():this.parsePropertyMethodFunction();method=true;}if(!kind){this.throwUnexpectedToken(this.lookahead);}if(kind==='init'){kind='method';}if(!computed){if(isStatic&&this.isPropertyKey(key,'prototype')){this.throwUnexpectedToken(token,messages_1.Messages.StaticPrototype);}if(!isStatic&&this.isPropertyKey(key,'constructor')){if(kind!=='method'||!method||value&&value.generator){this.throwUnexpectedToken(token,messages_1.Messages.ConstructorSpecialMethod);}if(hasConstructor.value){this.throwUnexpectedToken(token,messages_1.Messages.DuplicateConstructor);}else{hasConstructor.value=true;}kind='constructor';}}return this.finalize(node,new Node.MethodDefinition(key,computed,value,kind,isStatic));};Parser.prototype.parseClassElementList=function(){var body=[];var hasConstructor={value:false};this.expect('{');while(!this.match('}')){if(this.match(';')){this.nextToken();}else{body.push(this.parseClassElement(hasConstructor));}}this.expect('}');return body;};Parser.prototype.parseClassBody=function(){var node=this.createNode();var elementList=this.parseClassElementList();return this.finalize(node,new Node.ClassBody(elementList));};Parser.prototype.parseClassDeclaration=function(identifierIsOptional){var node=this.createNode();var previousStrict=this.context.strict;this.context.strict=true;this.expectKeyword('class');var id=identifierIsOptional&&this.lookahead.type!==3/* Identifier */?null:this.parseVariableIdentifier();var superClass=null;if(this.matchKeyword('extends')){this.nextToken();superClass=this.isolateCoverGrammar(this.parseLeftHandSideExpressionAllowCall);}var classBody=this.parseClassBody();this.context.strict=previousStrict;return this.finalize(node,new Node.ClassDeclaration(id,superClass,classBody));};Parser.prototype.parseClassExpression=function(){var node=this.createNode();var previousStrict=this.context.strict;this.context.strict=true;this.expectKeyword('class');var id=this.lookahead.type===3/* Identifier */?this.parseVariableIdentifier():null;var superClass=null;if(this.matchKeyword('extends')){this.nextToken();superClass=this.isolateCoverGrammar(this.parseLeftHandSideExpressionAllowCall);}var classBody=this.parseClassBody();this.context.strict=previousStrict;return this.finalize(node,new Node.ClassExpression(id,superClass,classBody));};// https://tc39.github.io/ecma262/#sec-scripts
  // https://tc39.github.io/ecma262/#sec-modules
  Parser.prototype.parseModule=function(){this.context.strict=true;this.context.isModule=true;this.scanner.isModule=true;var node=this.createNode();var body=this.parseDirectivePrologues();while(this.lookahead.type!==2/* EOF */){body.push(this.parseStatementListItem());}return this.finalize(node,new Node.Module(body));};Parser.prototype.parseScript=function(){var node=this.createNode();var body=this.parseDirectivePrologues();while(this.lookahead.type!==2/* EOF */){body.push(this.parseStatementListItem());}return this.finalize(node,new Node.Script(body));};// https://tc39.github.io/ecma262/#sec-imports
  Parser.prototype.parseModuleSpecifier=function(){var node=this.createNode();if(this.lookahead.type!==8/* StringLiteral */){this.throwError(messages_1.Messages.InvalidModuleSpecifier);}var token=this.nextToken();var raw=this.getTokenRaw(token);return this.finalize(node,new Node.Literal(token.value,raw));};// import {<foo as bar>} ...;
  Parser.prototype.parseImportSpecifier=function(){var node=this.createNode();var imported;var local;if(this.lookahead.type===3/* Identifier */){imported=this.parseVariableIdentifier();local=imported;if(this.matchContextualKeyword('as')){this.nextToken();local=this.parseVariableIdentifier();}}else{imported=this.parseIdentifierName();local=imported;if(this.matchContextualKeyword('as')){this.nextToken();local=this.parseVariableIdentifier();}else{this.throwUnexpectedToken(this.nextToken());}}return this.finalize(node,new Node.ImportSpecifier(local,imported));};// {foo, bar as bas}
  Parser.prototype.parseNamedImports=function(){this.expect('{');var specifiers=[];while(!this.match('}')){specifiers.push(this.parseImportSpecifier());if(!this.match('}')){this.expect(',');}}this.expect('}');return specifiers;};// import <foo> ...;
  Parser.prototype.parseImportDefaultSpecifier=function(){var node=this.createNode();var local=this.parseIdentifierName();return this.finalize(node,new Node.ImportDefaultSpecifier(local));};// import <* as foo> ...;
  Parser.prototype.parseImportNamespaceSpecifier=function(){var node=this.createNode();this.expect('*');if(!this.matchContextualKeyword('as')){this.throwError(messages_1.Messages.NoAsAfterImportNamespace);}this.nextToken();var local=this.parseIdentifierName();return this.finalize(node,new Node.ImportNamespaceSpecifier(local));};Parser.prototype.parseImportDeclaration=function(){if(this.context.inFunctionBody){this.throwError(messages_1.Messages.IllegalImportDeclaration);}var node=this.createNode();this.expectKeyword('import');var src;var specifiers=[];if(this.lookahead.type===8/* StringLiteral */){// import 'foo';
  src=this.parseModuleSpecifier();}else{if(this.match('{')){// import {bar}
  specifiers=specifiers.concat(this.parseNamedImports());}else if(this.match('*')){// import * as foo
  specifiers.push(this.parseImportNamespaceSpecifier());}else if(this.isIdentifierName(this.lookahead)&&!this.matchKeyword('default')){// import foo
  specifiers.push(this.parseImportDefaultSpecifier());if(this.match(',')){this.nextToken();if(this.match('*')){// import foo, * as foo
  specifiers.push(this.parseImportNamespaceSpecifier());}else if(this.match('{')){// import foo, {bar}
  specifiers=specifiers.concat(this.parseNamedImports());}else{this.throwUnexpectedToken(this.lookahead);}}}else{this.throwUnexpectedToken(this.nextToken());}if(!this.matchContextualKeyword('from')){var message=this.lookahead.value?messages_1.Messages.UnexpectedToken:messages_1.Messages.MissingFromClause;this.throwError(message,this.lookahead.value);}this.nextToken();src=this.parseModuleSpecifier();}this.consumeSemicolon();return this.finalize(node,new Node.ImportDeclaration(specifiers,src));};// https://tc39.github.io/ecma262/#sec-exports
  Parser.prototype.parseExportSpecifier=function(){var node=this.createNode();var local=this.parseIdentifierName();var exported=local;if(this.matchContextualKeyword('as')){this.nextToken();exported=this.parseIdentifierName();}return this.finalize(node,new Node.ExportSpecifier(local,exported));};Parser.prototype.parseExportDeclaration=function(){if(this.context.inFunctionBody){this.throwError(messages_1.Messages.IllegalExportDeclaration);}var node=this.createNode();this.expectKeyword('export');var exportDeclaration;if(this.matchKeyword('default')){// export default ...
  this.nextToken();if(this.matchKeyword('function')){// export default function foo () {}
  // export default function () {}
  var declaration=this.parseFunctionDeclaration(true);exportDeclaration=this.finalize(node,new Node.ExportDefaultDeclaration(declaration));}else if(this.matchKeyword('class')){// export default class foo {}
  var declaration=this.parseClassDeclaration(true);exportDeclaration=this.finalize(node,new Node.ExportDefaultDeclaration(declaration));}else if(this.matchContextualKeyword('async')){// export default async function f () {}
  // export default async function () {}
  // export default async x => x
  var declaration=this.matchAsyncFunction()?this.parseFunctionDeclaration(true):this.parseAssignmentExpression();exportDeclaration=this.finalize(node,new Node.ExportDefaultDeclaration(declaration));}else{if(this.matchContextualKeyword('from')){this.throwError(messages_1.Messages.UnexpectedToken,this.lookahead.value);}// export default {};
  // export default [];
  // export default (1 + 2);
  var declaration=this.match('{')?this.parseObjectInitializer():this.match('[')?this.parseArrayInitializer():this.parseAssignmentExpression();this.consumeSemicolon();exportDeclaration=this.finalize(node,new Node.ExportDefaultDeclaration(declaration));}}else if(this.match('*')){// export * from 'foo';
  this.nextToken();if(!this.matchContextualKeyword('from')){var message=this.lookahead.value?messages_1.Messages.UnexpectedToken:messages_1.Messages.MissingFromClause;this.throwError(message,this.lookahead.value);}this.nextToken();var src=this.parseModuleSpecifier();this.consumeSemicolon();exportDeclaration=this.finalize(node,new Node.ExportAllDeclaration(src));}else if(this.lookahead.type===4/* Keyword */){// export var f = 1;
  var declaration=void 0;switch(this.lookahead.value){case'let':case'const':declaration=this.parseLexicalDeclaration({inFor:false});break;case'var':case'class':case'function':declaration=this.parseStatementListItem();break;default:this.throwUnexpectedToken(this.lookahead);}exportDeclaration=this.finalize(node,new Node.ExportNamedDeclaration(declaration,[],null));}else if(this.matchAsyncFunction()){var declaration=this.parseFunctionDeclaration();exportDeclaration=this.finalize(node,new Node.ExportNamedDeclaration(declaration,[],null));}else{var specifiers=[];var source=null;var isExportFromIdentifier=false;this.expect('{');while(!this.match('}')){isExportFromIdentifier=isExportFromIdentifier||this.matchKeyword('default');specifiers.push(this.parseExportSpecifier());if(!this.match('}')){this.expect(',');}}this.expect('}');if(this.matchContextualKeyword('from')){// export {default} from 'foo';
  // export {foo} from 'foo';
  this.nextToken();source=this.parseModuleSpecifier();this.consumeSemicolon();}else if(isExportFromIdentifier){// export {default}; // missing fromClause
  var message=this.lookahead.value?messages_1.Messages.UnexpectedToken:messages_1.Messages.MissingFromClause;this.throwError(message,this.lookahead.value);}else{// export {foo};
  this.consumeSemicolon();}exportDeclaration=this.finalize(node,new Node.ExportNamedDeclaration(null,specifiers,source));}return exportDeclaration;};return Parser;}();exports.Parser=Parser;/***/},/* 9 */ /***/function(module,exports){// Ensure the condition is true, otherwise throw an error.
  // This is only to have a better contract semantic, i.e. another safety net
  // to catch a logic error. The condition shall be fulfilled in normal case.
  // Do NOT use this to enforce a certain condition on any user input.
  Object.defineProperty(exports,"__esModule",{value:true});function assert(condition,message){/* istanbul ignore if */if(!condition){throw new Error('ASSERT: '+message);}}exports.assert=assert;/***/},/* 10 */ /***/function(module,exports){/* tslint:disable:max-classes-per-file */Object.defineProperty(exports,"__esModule",{value:true});var ErrorHandler=function(){function ErrorHandler(){this.errors=[];this.tolerant=false;}ErrorHandler.prototype.recordError=function(error){this.errors.push(error);};ErrorHandler.prototype.tolerate=function(error){if(this.tolerant){this.recordError(error);}else{throw error;}};ErrorHandler.prototype.constructError=function(msg,column){var error=new Error(msg);try{throw error;}catch(base){/* istanbul ignore else */if(Object.create&&Object.defineProperty){error=Object.create(base);Object.defineProperty(error,'column',{value:column});}}/* istanbul ignore next */return error;};ErrorHandler.prototype.createError=function(index,line,col,description){var msg='Line '+line+': '+description;var error=this.constructError(msg,col);error.index=index;error.lineNumber=line;error.description=description;return error;};ErrorHandler.prototype.throwError=function(index,line,col,description){throw this.createError(index,line,col,description);};ErrorHandler.prototype.tolerateError=function(index,line,col,description){var error=this.createError(index,line,col,description);if(this.tolerant){this.recordError(error);}else{throw error;}};return ErrorHandler;}();exports.ErrorHandler=ErrorHandler;/***/},/* 11 */ /***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});// Error messages should be identical to V8.
  exports.Messages={BadGetterArity:'Getter must not have any formal parameters',BadSetterArity:'Setter must have exactly one formal parameter',BadSetterRestParameter:'Setter function argument must not be a rest parameter',ConstructorIsAsync:'Class constructor may not be an async method',ConstructorSpecialMethod:'Class constructor may not be an accessor',DeclarationMissingInitializer:'Missing initializer in %0 declaration',DefaultRestParameter:'Unexpected token =',DuplicateBinding:'Duplicate binding %0',DuplicateConstructor:'A class may only have one constructor',DuplicateProtoProperty:'Duplicate __proto__ fields are not allowed in object literals',ForInOfLoopInitializer:'%0 loop variable declaration may not have an initializer',GeneratorInLegacyContext:'Generator declarations are not allowed in legacy contexts',IllegalBreak:'Illegal break statement',IllegalContinue:'Illegal continue statement',IllegalExportDeclaration:'Unexpected token',IllegalImportDeclaration:'Unexpected token',IllegalLanguageModeDirective:'Illegal \'use strict\' directive in function with non-simple parameter list',IllegalReturn:'Illegal return statement',InvalidEscapedReservedWord:'Keyword must not contain escaped characters',InvalidHexEscapeSequence:'Invalid hexadecimal escape sequence',InvalidLHSInAssignment:'Invalid left-hand side in assignment',InvalidLHSInForIn:'Invalid left-hand side in for-in',InvalidLHSInForLoop:'Invalid left-hand side in for-loop',InvalidModuleSpecifier:'Unexpected token',InvalidRegExp:'Invalid regular expression',LetInLexicalBinding:'let is disallowed as a lexically bound name',MissingFromClause:'Unexpected token',MultipleDefaultsInSwitch:'More than one default clause in switch statement',NewlineAfterThrow:'Illegal newline after throw',NoAsAfterImportNamespace:'Unexpected token',NoCatchOrFinally:'Missing catch or finally after try',ParameterAfterRestParameter:'Rest parameter must be last formal parameter',Redeclaration:'%0 \'%1\' has already been declared',StaticPrototype:'Classes may not have static property named prototype',StrictCatchVariable:'Catch variable may not be eval or arguments in strict mode',StrictDelete:'Delete of an unqualified identifier in strict mode.',StrictFunction:'In strict mode code, functions can only be declared at top level or inside a block',StrictFunctionName:'Function name may not be eval or arguments in strict mode',StrictLHSAssignment:'Assignment to eval or arguments is not allowed in strict mode',StrictLHSPostfix:'Postfix increment/decrement may not have eval or arguments operand in strict mode',StrictLHSPrefix:'Prefix increment/decrement may not have eval or arguments operand in strict mode',StrictModeWith:'Strict mode code may not include a with statement',StrictOctalLiteral:'Octal literals are not allowed in strict mode.',StrictParamDupe:'Strict mode function may not have duplicate parameter names',StrictParamName:'Parameter name eval or arguments is not allowed in strict mode',StrictReservedWord:'Use of future reserved word in strict mode',StrictVarName:'Variable name may not be eval or arguments in strict mode',TemplateOctalLiteral:'Octal literals are not allowed in template strings.',UnexpectedEOS:'Unexpected end of input',UnexpectedIdentifier:'Unexpected identifier',UnexpectedNumber:'Unexpected number',UnexpectedReserved:'Unexpected reserved word',UnexpectedString:'Unexpected string',UnexpectedTemplate:'Unexpected quasi %0',UnexpectedToken:'Unexpected token %0',UnexpectedTokenIllegal:'Unexpected token ILLEGAL',UnknownLabel:'Undefined label \'%0\'',UnterminatedRegExp:'Invalid regular expression: missing /'};/***/},/* 12 */ /***/function(module,exports,__webpack_require__){Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__webpack_require__(9);var character_1=__webpack_require__(4);var messages_1=__webpack_require__(11);function hexValue(ch){return '0123456789abcdef'.indexOf(ch.toLowerCase());}function octalValue(ch){return '01234567'.indexOf(ch);}var Scanner=function(){function Scanner(code,handler){this.source=code;this.errorHandler=handler;this.trackComment=false;this.isModule=false;this.length=code.length;this.index=0;this.lineNumber=code.length>0?1:0;this.lineStart=0;this.curlyStack=[];}Scanner.prototype.saveState=function(){return {index:this.index,lineNumber:this.lineNumber,lineStart:this.lineStart};};Scanner.prototype.restoreState=function(state){this.index=state.index;this.lineNumber=state.lineNumber;this.lineStart=state.lineStart;};Scanner.prototype.eof=function(){return this.index>=this.length;};Scanner.prototype.throwUnexpectedToken=function(message){if(message===void 0){message=messages_1.Messages.UnexpectedTokenIllegal;}return this.errorHandler.throwError(this.index,this.lineNumber,this.index-this.lineStart+1,message);};Scanner.prototype.tolerateUnexpectedToken=function(message){if(message===void 0){message=messages_1.Messages.UnexpectedTokenIllegal;}this.errorHandler.tolerateError(this.index,this.lineNumber,this.index-this.lineStart+1,message);};// https://tc39.github.io/ecma262/#sec-comments
  Scanner.prototype.skipSingleLineComment=function(offset){var comments=[];var start,loc;if(this.trackComment){comments=[];start=this.index-offset;loc={start:{line:this.lineNumber,column:this.index-this.lineStart-offset},end:{}};}while(!this.eof()){var ch=this.source.charCodeAt(this.index);++this.index;if(character_1.Character.isLineTerminator(ch)){if(this.trackComment){loc.end={line:this.lineNumber,column:this.index-this.lineStart-1};var entry={multiLine:false,slice:[start+offset,this.index-1],range:[start,this.index-1],loc:loc};comments.push(entry);}if(ch===13&&this.source.charCodeAt(this.index)===10){++this.index;}++this.lineNumber;this.lineStart=this.index;return comments;}}if(this.trackComment){loc.end={line:this.lineNumber,column:this.index-this.lineStart};var entry={multiLine:false,slice:[start+offset,this.index],range:[start,this.index],loc:loc};comments.push(entry);}return comments;};Scanner.prototype.skipMultiLineComment=function(){var comments=[];var start,loc;if(this.trackComment){comments=[];start=this.index-2;loc={start:{line:this.lineNumber,column:this.index-this.lineStart-2},end:{}};}while(!this.eof()){var ch=this.source.charCodeAt(this.index);if(character_1.Character.isLineTerminator(ch)){if(ch===0x0D&&this.source.charCodeAt(this.index+1)===0x0A){++this.index;}++this.lineNumber;++this.index;this.lineStart=this.index;}else if(ch===0x2A){// Block comment ends with '*/'.
  if(this.source.charCodeAt(this.index+1)===0x2F){this.index+=2;if(this.trackComment){loc.end={line:this.lineNumber,column:this.index-this.lineStart};var entry={multiLine:true,slice:[start+2,this.index-2],range:[start,this.index],loc:loc};comments.push(entry);}return comments;}++this.index;}else{++this.index;}}// Ran off the end of the file - the whole thing is a comment
  if(this.trackComment){loc.end={line:this.lineNumber,column:this.index-this.lineStart};var entry={multiLine:true,slice:[start+2,this.index],range:[start,this.index],loc:loc};comments.push(entry);}this.tolerateUnexpectedToken();return comments;};Scanner.prototype.scanComments=function(){var comments;if(this.trackComment){comments=[];}var start=this.index===0;while(!this.eof()){var ch=this.source.charCodeAt(this.index);if(character_1.Character.isWhiteSpace(ch)){++this.index;}else if(character_1.Character.isLineTerminator(ch)){++this.index;if(ch===0x0D&&this.source.charCodeAt(this.index)===0x0A){++this.index;}++this.lineNumber;this.lineStart=this.index;start=true;}else if(ch===0x2F){ch=this.source.charCodeAt(this.index+1);if(ch===0x2F){this.index+=2;var comment=this.skipSingleLineComment(2);if(this.trackComment){comments=comments.concat(comment);}start=true;}else if(ch===0x2A){this.index+=2;var comment=this.skipMultiLineComment();if(this.trackComment){comments=comments.concat(comment);}}else{break;}}else if(start&&ch===0x2D){// U+003E is '>'
  if(this.source.charCodeAt(this.index+1)===0x2D&&this.source.charCodeAt(this.index+2)===0x3E){// '-->' is a single-line comment
  this.index+=3;var comment=this.skipSingleLineComment(3);if(this.trackComment){comments=comments.concat(comment);}}else{break;}}else if(ch===0x3C&&!this.isModule){if(this.source.slice(this.index+1,this.index+4)==='!--'){this.index+=4;// `<!--`
  var comment=this.skipSingleLineComment(4);if(this.trackComment){comments=comments.concat(comment);}}else{break;}}else{break;}}return comments;};// https://tc39.github.io/ecma262/#sec-future-reserved-words
  Scanner.prototype.isFutureReservedWord=function(id){switch(id){case'enum':case'export':case'import':case'super':return true;default:return false;}};Scanner.prototype.isStrictModeReservedWord=function(id){switch(id){case'implements':case'interface':case'package':case'private':case'protected':case'public':case'static':case'yield':case'let':return true;default:return false;}};Scanner.prototype.isRestrictedWord=function(id){return id==='eval'||id==='arguments';};// https://tc39.github.io/ecma262/#sec-keywords
  Scanner.prototype.isKeyword=function(id){switch(id.length){case 2:return id==='if'||id==='in'||id==='do';case 3:return id==='var'||id==='for'||id==='new'||id==='try'||id==='let';case 4:return id==='this'||id==='else'||id==='case'||id==='void'||id==='with'||id==='enum';case 5:return id==='while'||id==='break'||id==='catch'||id==='throw'||id==='const'||id==='yield'||id==='class'||id==='super';case 6:return id==='return'||id==='typeof'||id==='delete'||id==='switch'||id==='export'||id==='import';case 7:return id==='default'||id==='finally'||id==='extends';case 8:return id==='function'||id==='continue'||id==='debugger';case 10:return id==='instanceof';default:return false;}};Scanner.prototype.codePointAt=function(i){var cp=this.source.charCodeAt(i);if(cp>=0xD800&&cp<=0xDBFF){var second=this.source.charCodeAt(i+1);if(second>=0xDC00&&second<=0xDFFF){var first=cp;cp=(first-0xD800)*0x400+second-0xDC00+0x10000;}}return cp;};Scanner.prototype.scanHexEscape=function(prefix){var len=prefix==='u'?4:2;var code=0;for(var i=0;i<len;++i){if(!this.eof()&&character_1.Character.isHexDigit(this.source.charCodeAt(this.index))){code=code*16+hexValue(this.source[this.index++]);}else{return null;}}return String.fromCharCode(code);};Scanner.prototype.scanUnicodeCodePointEscape=function(){var ch=this.source[this.index];var code=0;// At least, one hex digit is required.
  if(ch==='}'){this.throwUnexpectedToken();}while(!this.eof()){ch=this.source[this.index++];if(!character_1.Character.isHexDigit(ch.charCodeAt(0))){break;}code=code*16+hexValue(ch);}if(code>0x10FFFF||ch!=='}'){this.throwUnexpectedToken();}return character_1.Character.fromCodePoint(code);};Scanner.prototype.getIdentifier=function(){var start=this.index++;while(!this.eof()){var ch=this.source.charCodeAt(this.index);if(ch===0x5C){// Blackslash (U+005C) marks Unicode escape sequence.
  this.index=start;return this.getComplexIdentifier();}else if(ch>=0xD800&&ch<0xDFFF){// Need to handle surrogate pairs.
  this.index=start;return this.getComplexIdentifier();}if(character_1.Character.isIdentifierPart(ch)){++this.index;}else{break;}}return this.source.slice(start,this.index);};Scanner.prototype.getComplexIdentifier=function(){var cp=this.codePointAt(this.index);var id=character_1.Character.fromCodePoint(cp);this.index+=id.length;// '\u' (U+005C, U+0075) denotes an escaped character.
  var ch;if(cp===0x5C){if(this.source.charCodeAt(this.index)!==0x75){this.throwUnexpectedToken();}++this.index;if(this.source[this.index]==='{'){++this.index;ch=this.scanUnicodeCodePointEscape();}else{ch=this.scanHexEscape('u');if(ch===null||ch==='\\'||!character_1.Character.isIdentifierStart(ch.charCodeAt(0))){this.throwUnexpectedToken();}}id=ch;}while(!this.eof()){cp=this.codePointAt(this.index);if(!character_1.Character.isIdentifierPart(cp)){break;}ch=character_1.Character.fromCodePoint(cp);id+=ch;this.index+=ch.length;// '\u' (U+005C, U+0075) denotes an escaped character.
  if(cp===0x5C){id=id.substr(0,id.length-1);if(this.source.charCodeAt(this.index)!==0x75){this.throwUnexpectedToken();}++this.index;if(this.source[this.index]==='{'){++this.index;ch=this.scanUnicodeCodePointEscape();}else{ch=this.scanHexEscape('u');if(ch===null||ch==='\\'||!character_1.Character.isIdentifierPart(ch.charCodeAt(0))){this.throwUnexpectedToken();}}id+=ch;}}return id;};Scanner.prototype.octalToDecimal=function(ch){// \0 is not octal escape sequence
  var octal=ch!=='0';var code=octalValue(ch);if(!this.eof()&&character_1.Character.isOctalDigit(this.source.charCodeAt(this.index))){octal=true;code=code*8+octalValue(this.source[this.index++]);// 3 digits are only allowed when string starts
  // with 0, 1, 2, 3
  if('0123'.indexOf(ch)>=0&&!this.eof()&&character_1.Character.isOctalDigit(this.source.charCodeAt(this.index))){code=code*8+octalValue(this.source[this.index++]);}}return {code:code,octal:octal};};// https://tc39.github.io/ecma262/#sec-names-and-keywords
  Scanner.prototype.scanIdentifier=function(){var type;var start=this.index;// Backslash (U+005C) starts an escaped character.
  var id=this.source.charCodeAt(start)===0x5C?this.getComplexIdentifier():this.getIdentifier();// There is no keyword or literal with only one character.
  // Thus, it must be an identifier.
  if(id.length===1){type=3/* Identifier */;}else if(this.isKeyword(id)){type=4/* Keyword */;}else if(id==='null'){type=5/* NullLiteral */;}else if(id==='true'||id==='false'){type=1/* BooleanLiteral */;}else{type=3/* Identifier */;}if(type!==3/* Identifier */&&start+id.length!==this.index){var restore=this.index;this.index=start;this.tolerateUnexpectedToken(messages_1.Messages.InvalidEscapedReservedWord);this.index=restore;}return {type:type,value:id,lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};// https://tc39.github.io/ecma262/#sec-punctuators
  Scanner.prototype.scanPunctuator=function(){var start=this.index;// Check for most common single-character punctuators.
  var str=this.source[this.index];switch(str){case'(':case'{':if(str==='{'){this.curlyStack.push('{');}++this.index;break;case'.':++this.index;if(this.source[this.index]==='.'&&this.source[this.index+1]==='.'){// Spread operator: ...
  this.index+=2;str='...';}break;case'}':++this.index;this.curlyStack.pop();break;case')':case';':case',':case'[':case']':case':':case'?':case'~':++this.index;break;default:// 4-character punctuator.
  str=this.source.substr(this.index,4);if(str==='>>>='){this.index+=4;}else{// 3-character punctuators.
  str=str.substr(0,3);if(str==='==='||str==='!=='||str==='>>>'||str==='<<='||str==='>>='||str==='**='){this.index+=3;}else{// 2-character punctuators.
  str=str.substr(0,2);if(str==='&&'||str==='||'||str==='=='||str==='!='||str==='+='||str==='-='||str==='*='||str==='/='||str==='++'||str==='--'||str==='<<'||str==='>>'||str==='&='||str==='|='||str==='^='||str==='%='||str==='<='||str==='>='||str==='=>'||str==='**'){this.index+=2;}else{// 1-character punctuators.
  str=this.source[this.index];if('<>=!+-*%&|^/'.indexOf(str)>=0){++this.index;}}}}}if(this.index===start){this.throwUnexpectedToken();}return {type:7/* Punctuator */,value:str,lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};// https://tc39.github.io/ecma262/#sec-literals-numeric-literals
  Scanner.prototype.scanHexLiteral=function(start){var num='';while(!this.eof()){if(!character_1.Character.isHexDigit(this.source.charCodeAt(this.index))){break;}num+=this.source[this.index++];}if(num.length===0){this.throwUnexpectedToken();}if(character_1.Character.isIdentifierStart(this.source.charCodeAt(this.index))){this.throwUnexpectedToken();}return {type:6/* NumericLiteral */,value:parseInt('0x'+num,16),lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};Scanner.prototype.scanBinaryLiteral=function(start){var num='';var ch;while(!this.eof()){ch=this.source[this.index];if(ch!=='0'&&ch!=='1'){break;}num+=this.source[this.index++];}if(num.length===0){// only 0b or 0B
  this.throwUnexpectedToken();}if(!this.eof()){ch=this.source.charCodeAt(this.index);/* istanbul ignore else */if(character_1.Character.isIdentifierStart(ch)||character_1.Character.isDecimalDigit(ch)){this.throwUnexpectedToken();}}return {type:6/* NumericLiteral */,value:parseInt(num,2),lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};Scanner.prototype.scanOctalLiteral=function(prefix,start){var num='';var octal=false;if(character_1.Character.isOctalDigit(prefix.charCodeAt(0))){octal=true;num='0'+this.source[this.index++];}else{++this.index;}while(!this.eof()){if(!character_1.Character.isOctalDigit(this.source.charCodeAt(this.index))){break;}num+=this.source[this.index++];}if(!octal&&num.length===0){// only 0o or 0O
  this.throwUnexpectedToken();}if(character_1.Character.isIdentifierStart(this.source.charCodeAt(this.index))||character_1.Character.isDecimalDigit(this.source.charCodeAt(this.index))){this.throwUnexpectedToken();}return {type:6/* NumericLiteral */,value:parseInt(num,8),octal:octal,lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};Scanner.prototype.isImplicitOctalLiteral=function(){// Implicit octal, unless there is a non-octal digit.
  // (Annex B.1.1 on Numeric Literals)
  for(var i=this.index+1;i<this.length;++i){var ch=this.source[i];if(ch==='8'||ch==='9'){return false;}if(!character_1.Character.isOctalDigit(ch.charCodeAt(0))){return true;}}return true;};Scanner.prototype.scanNumericLiteral=function(){var start=this.index;var ch=this.source[start];assert_1.assert(character_1.Character.isDecimalDigit(ch.charCodeAt(0))||ch==='.','Numeric literal must start with a decimal digit or a decimal point');var num='';if(ch!=='.'){num=this.source[this.index++];ch=this.source[this.index];// Hex number starts with '0x'.
  // Octal number starts with '0'.
  // Octal number in ES6 starts with '0o'.
  // Binary number in ES6 starts with '0b'.
  if(num==='0'){if(ch==='x'||ch==='X'){++this.index;return this.scanHexLiteral(start);}if(ch==='b'||ch==='B'){++this.index;return this.scanBinaryLiteral(start);}if(ch==='o'||ch==='O'){return this.scanOctalLiteral(ch,start);}if(ch&&character_1.Character.isOctalDigit(ch.charCodeAt(0))){if(this.isImplicitOctalLiteral()){return this.scanOctalLiteral(ch,start);}}}while(character_1.Character.isDecimalDigit(this.source.charCodeAt(this.index))){num+=this.source[this.index++];}ch=this.source[this.index];}if(ch==='.'){num+=this.source[this.index++];while(character_1.Character.isDecimalDigit(this.source.charCodeAt(this.index))){num+=this.source[this.index++];}ch=this.source[this.index];}if(ch==='e'||ch==='E'){num+=this.source[this.index++];ch=this.source[this.index];if(ch==='+'||ch==='-'){num+=this.source[this.index++];}if(character_1.Character.isDecimalDigit(this.source.charCodeAt(this.index))){while(character_1.Character.isDecimalDigit(this.source.charCodeAt(this.index))){num+=this.source[this.index++];}}else{this.throwUnexpectedToken();}}if(character_1.Character.isIdentifierStart(this.source.charCodeAt(this.index))){this.throwUnexpectedToken();}return {type:6/* NumericLiteral */,value:parseFloat(num),lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};// https://tc39.github.io/ecma262/#sec-literals-string-literals
  Scanner.prototype.scanStringLiteral=function(){var start=this.index;var quote=this.source[start];assert_1.assert(quote==='\''||quote==='"','String literal must starts with a quote');++this.index;var octal=false;var str='';while(!this.eof()){var ch=this.source[this.index++];if(ch===quote){quote='';break;}else if(ch==='\\'){ch=this.source[this.index++];if(!ch||!character_1.Character.isLineTerminator(ch.charCodeAt(0))){switch(ch){case'u':if(this.source[this.index]==='{'){++this.index;str+=this.scanUnicodeCodePointEscape();}else{var unescaped_1=this.scanHexEscape(ch);if(unescaped_1===null){this.throwUnexpectedToken();}str+=unescaped_1;}break;case'x':var unescaped=this.scanHexEscape(ch);if(unescaped===null){this.throwUnexpectedToken(messages_1.Messages.InvalidHexEscapeSequence);}str+=unescaped;break;case'n':str+='\n';break;case'r':str+='\r';break;case't':str+='\t';break;case'b':str+='\b';break;case'f':str+='\f';break;case'v':str+='\x0B';break;case'8':case'9':str+=ch;this.tolerateUnexpectedToken();break;default:if(ch&&character_1.Character.isOctalDigit(ch.charCodeAt(0))){var octToDec=this.octalToDecimal(ch);octal=octToDec.octal||octal;str+=String.fromCharCode(octToDec.code);}else{str+=ch;}break;}}else{++this.lineNumber;if(ch==='\r'&&this.source[this.index]==='\n'){++this.index;}this.lineStart=this.index;}}else if(character_1.Character.isLineTerminator(ch.charCodeAt(0))){break;}else{str+=ch;}}if(quote!==''){this.index=start;this.throwUnexpectedToken();}return {type:8/* StringLiteral */,value:str,octal:octal,lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};// https://tc39.github.io/ecma262/#sec-template-literal-lexical-components
  Scanner.prototype.scanTemplate=function(){var cooked='';var terminated=false;var start=this.index;var head=this.source[start]==='`';var tail=false;var rawOffset=2;++this.index;while(!this.eof()){var ch=this.source[this.index++];if(ch==='`'){rawOffset=1;tail=true;terminated=true;break;}else if(ch==='$'){if(this.source[this.index]==='{'){this.curlyStack.push('${');++this.index;terminated=true;break;}cooked+=ch;}else if(ch==='\\'){ch=this.source[this.index++];if(!character_1.Character.isLineTerminator(ch.charCodeAt(0))){switch(ch){case'n':cooked+='\n';break;case'r':cooked+='\r';break;case't':cooked+='\t';break;case'u':if(this.source[this.index]==='{'){++this.index;cooked+=this.scanUnicodeCodePointEscape();}else{var restore=this.index;var unescaped_2=this.scanHexEscape(ch);if(unescaped_2!==null){cooked+=unescaped_2;}else{this.index=restore;cooked+=ch;}}break;case'x':var unescaped=this.scanHexEscape(ch);if(unescaped===null){this.throwUnexpectedToken(messages_1.Messages.InvalidHexEscapeSequence);}cooked+=unescaped;break;case'b':cooked+='\b';break;case'f':cooked+='\f';break;case'v':cooked+='\v';break;default:if(ch==='0'){if(character_1.Character.isDecimalDigit(this.source.charCodeAt(this.index))){// Illegal: \01 \02 and so on
  this.throwUnexpectedToken(messages_1.Messages.TemplateOctalLiteral);}cooked+='\0';}else if(character_1.Character.isOctalDigit(ch.charCodeAt(0))){// Illegal: \1 \2
  this.throwUnexpectedToken(messages_1.Messages.TemplateOctalLiteral);}else{cooked+=ch;}break;}}else{++this.lineNumber;if(ch==='\r'&&this.source[this.index]==='\n'){++this.index;}this.lineStart=this.index;}}else if(character_1.Character.isLineTerminator(ch.charCodeAt(0))){++this.lineNumber;if(ch==='\r'&&this.source[this.index]==='\n'){++this.index;}this.lineStart=this.index;cooked+='\n';}else{cooked+=ch;}}if(!terminated){this.throwUnexpectedToken();}if(!head){this.curlyStack.pop();}return {type:10/* Template */,value:this.source.slice(start+1,this.index-rawOffset),cooked:cooked,head:head,tail:tail,lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};// https://tc39.github.io/ecma262/#sec-literals-regular-expression-literals
  Scanner.prototype.testRegExp=function(pattern,flags){// The BMP character to use as a replacement for astral symbols when
  // translating an ES6 "u"-flagged pattern to an ES5-compatible
  // approximation.
  // Note: replacing with '\uFFFF' enables false positives in unlikely
  // scenarios. For example, `[\u{1044f}-\u{10440}]` is an invalid
  // pattern that would not be detected by this substitution.
  var astralSubstitute='\uFFFF';var tmp=pattern;var self=this;if(flags.indexOf('u')>=0){tmp=tmp.replace(/\\u\{([0-9a-fA-F]+)\}|\\u([a-fA-F0-9]{4})/g,function($0,$1,$2){var codePoint=parseInt($1||$2,16);if(codePoint>0x10FFFF){self.throwUnexpectedToken(messages_1.Messages.InvalidRegExp);}if(codePoint<=0xFFFF){return String.fromCharCode(codePoint);}return astralSubstitute;}).replace(/[\uD800-\uDBFF][\uDC00-\uDFFF]/g,astralSubstitute);}// First, detect invalid regular expressions.
  try{RegExp(tmp);}catch(e){this.throwUnexpectedToken(messages_1.Messages.InvalidRegExp);}// Return a regular expression object for this pattern-flag pair, or
  // `null` in case the current environment doesn't support the flags it
  // uses.
  try{return new RegExp(pattern,flags);}catch(exception){/* istanbul ignore next */return null;}};Scanner.prototype.scanRegExpBody=function(){var ch=this.source[this.index];assert_1.assert(ch==='/','Regular expression literal must start with a slash');var str=this.source[this.index++];var classMarker=false;var terminated=false;while(!this.eof()){ch=this.source[this.index++];str+=ch;if(ch==='\\'){ch=this.source[this.index++];// https://tc39.github.io/ecma262/#sec-literals-regular-expression-literals
  if(character_1.Character.isLineTerminator(ch.charCodeAt(0))){this.throwUnexpectedToken(messages_1.Messages.UnterminatedRegExp);}str+=ch;}else if(character_1.Character.isLineTerminator(ch.charCodeAt(0))){this.throwUnexpectedToken(messages_1.Messages.UnterminatedRegExp);}else if(classMarker){if(ch===']'){classMarker=false;}}else{if(ch==='/'){terminated=true;break;}else if(ch==='['){classMarker=true;}}}if(!terminated){this.throwUnexpectedToken(messages_1.Messages.UnterminatedRegExp);}// Exclude leading and trailing slash.
  return str.substr(1,str.length-2);};Scanner.prototype.scanRegExpFlags=function(){var str='';var flags='';while(!this.eof()){var ch=this.source[this.index];if(!character_1.Character.isIdentifierPart(ch.charCodeAt(0))){break;}++this.index;if(ch==='\\'&&!this.eof()){ch=this.source[this.index];if(ch==='u'){++this.index;var restore=this.index;var char=this.scanHexEscape('u');if(char!==null){flags+=char;for(str+='\\u';restore<this.index;++restore){str+=this.source[restore];}}else{this.index=restore;flags+='u';str+='\\u';}this.tolerateUnexpectedToken();}else{str+='\\';this.tolerateUnexpectedToken();}}else{flags+=ch;str+=ch;}}return flags;};Scanner.prototype.scanRegExp=function(){var start=this.index;var pattern=this.scanRegExpBody();var flags=this.scanRegExpFlags();var value=this.testRegExp(pattern,flags);return {type:9/* RegularExpression */,value:'',pattern:pattern,flags:flags,regex:value,lineNumber:this.lineNumber,lineStart:this.lineStart,start:start,end:this.index};};Scanner.prototype.lex=function(){if(this.eof()){return {type:2/* EOF */,value:'',lineNumber:this.lineNumber,lineStart:this.lineStart,start:this.index,end:this.index};}var cp=this.source.charCodeAt(this.index);if(character_1.Character.isIdentifierStart(cp)){return this.scanIdentifier();}// Very common: ( and ) and ;
  if(cp===0x28||cp===0x29||cp===0x3B){return this.scanPunctuator();}// String literal starts with single quote (U+0027) or double quote (U+0022).
  if(cp===0x27||cp===0x22){return this.scanStringLiteral();}// Dot (.) U+002E can also start a floating-point number, hence the need
  // to check the next character.
  if(cp===0x2E){if(character_1.Character.isDecimalDigit(this.source.charCodeAt(this.index+1))){return this.scanNumericLiteral();}return this.scanPunctuator();}if(character_1.Character.isDecimalDigit(cp)){return this.scanNumericLiteral();}// Template literals start with ` (U+0060) for template head
  // or } (U+007D) for template middle or template tail.
  if(cp===0x60||cp===0x7D&&this.curlyStack[this.curlyStack.length-1]==='${'){return this.scanTemplate();}// Possible identifier start in a surrogate pair.
  if(cp>=0xD800&&cp<0xDFFF){if(character_1.Character.isIdentifierStart(this.codePointAt(this.index))){return this.scanIdentifier();}}return this.scanPunctuator();};return Scanner;}();exports.Scanner=Scanner;/***/},/* 13 */ /***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});exports.TokenName={};exports.TokenName[1/* BooleanLiteral */]='Boolean';exports.TokenName[2/* EOF */]='<end>';exports.TokenName[3/* Identifier */]='Identifier';exports.TokenName[4/* Keyword */]='Keyword';exports.TokenName[5/* NullLiteral */]='Null';exports.TokenName[6/* NumericLiteral */]='Numeric';exports.TokenName[7/* Punctuator */]='Punctuator';exports.TokenName[8/* StringLiteral */]='String';exports.TokenName[9/* RegularExpression */]='RegularExpression';exports.TokenName[10/* Template */]='Template';/***/},/* 14 */ /***/function(module,exports){// Generated by generate-xhtml-entities.js. DO NOT MODIFY!
  Object.defineProperty(exports,"__esModule",{value:true});exports.XHTMLEntities={quot:'\u0022',amp:'\u0026',apos:'\u0027',gt:'\u003E',nbsp:'\u00A0',iexcl:'\u00A1',cent:'\u00A2',pound:'\u00A3',curren:'\u00A4',yen:'\u00A5',brvbar:'\u00A6',sect:'\u00A7',uml:'\u00A8',copy:'\u00A9',ordf:'\u00AA',laquo:'\u00AB',not:'\u00AC',shy:'\u00AD',reg:'\u00AE',macr:'\u00AF',deg:'\u00B0',plusmn:'\u00B1',sup2:'\u00B2',sup3:'\u00B3',acute:'\u00B4',micro:'\u00B5',para:'\u00B6',middot:'\u00B7',cedil:'\u00B8',sup1:'\u00B9',ordm:'\u00BA',raquo:'\u00BB',frac14:'\u00BC',frac12:'\u00BD',frac34:'\u00BE',iquest:'\u00BF',Agrave:'\u00C0',Aacute:'\u00C1',Acirc:'\u00C2',Atilde:'\u00C3',Auml:'\u00C4',Aring:'\u00C5',AElig:'\u00C6',Ccedil:'\u00C7',Egrave:'\u00C8',Eacute:'\u00C9',Ecirc:'\u00CA',Euml:'\u00CB',Igrave:'\u00CC',Iacute:'\u00CD',Icirc:'\u00CE',Iuml:'\u00CF',ETH:'\u00D0',Ntilde:'\u00D1',Ograve:'\u00D2',Oacute:'\u00D3',Ocirc:'\u00D4',Otilde:'\u00D5',Ouml:'\u00D6',times:'\u00D7',Oslash:'\u00D8',Ugrave:'\u00D9',Uacute:'\u00DA',Ucirc:'\u00DB',Uuml:'\u00DC',Yacute:'\u00DD',THORN:'\u00DE',szlig:'\u00DF',agrave:'\u00E0',aacute:'\u00E1',acirc:'\u00E2',atilde:'\u00E3',auml:'\u00E4',aring:'\u00E5',aelig:'\u00E6',ccedil:'\u00E7',egrave:'\u00E8',eacute:'\u00E9',ecirc:'\u00EA',euml:'\u00EB',igrave:'\u00EC',iacute:'\u00ED',icirc:'\u00EE',iuml:'\u00EF',eth:'\u00F0',ntilde:'\u00F1',ograve:'\u00F2',oacute:'\u00F3',ocirc:'\u00F4',otilde:'\u00F5',ouml:'\u00F6',divide:'\u00F7',oslash:'\u00F8',ugrave:'\u00F9',uacute:'\u00FA',ucirc:'\u00FB',uuml:'\u00FC',yacute:'\u00FD',thorn:'\u00FE',yuml:'\u00FF',OElig:'\u0152',oelig:'\u0153',Scaron:'\u0160',scaron:'\u0161',Yuml:'\u0178',fnof:'\u0192',circ:'\u02C6',tilde:'\u02DC',Alpha:'\u0391',Beta:'\u0392',Gamma:'\u0393',Delta:'\u0394',Epsilon:'\u0395',Zeta:'\u0396',Eta:'\u0397',Theta:'\u0398',Iota:'\u0399',Kappa:'\u039A',Lambda:'\u039B',Mu:'\u039C',Nu:'\u039D',Xi:'\u039E',Omicron:'\u039F',Pi:'\u03A0',Rho:'\u03A1',Sigma:'\u03A3',Tau:'\u03A4',Upsilon:'\u03A5',Phi:'\u03A6',Chi:'\u03A7',Psi:'\u03A8',Omega:'\u03A9',alpha:'\u03B1',beta:'\u03B2',gamma:'\u03B3',delta:'\u03B4',epsilon:'\u03B5',zeta:'\u03B6',eta:'\u03B7',theta:'\u03B8',iota:'\u03B9',kappa:'\u03BA',lambda:'\u03BB',mu:'\u03BC',nu:'\u03BD',xi:'\u03BE',omicron:'\u03BF',pi:'\u03C0',rho:'\u03C1',sigmaf:'\u03C2',sigma:'\u03C3',tau:'\u03C4',upsilon:'\u03C5',phi:'\u03C6',chi:'\u03C7',psi:'\u03C8',omega:'\u03C9',thetasym:'\u03D1',upsih:'\u03D2',piv:'\u03D6',ensp:'\u2002',emsp:'\u2003',thinsp:'\u2009',zwnj:'\u200C',zwj:'\u200D',lrm:'\u200E',rlm:'\u200F',ndash:'\u2013',mdash:'\u2014',lsquo:'\u2018',rsquo:'\u2019',sbquo:'\u201A',ldquo:'\u201C',rdquo:'\u201D',bdquo:'\u201E',dagger:'\u2020',Dagger:'\u2021',bull:'\u2022',hellip:'\u2026',permil:'\u2030',prime:'\u2032',Prime:'\u2033',lsaquo:'\u2039',rsaquo:'\u203A',oline:'\u203E',frasl:'\u2044',euro:'\u20AC',image:'\u2111',weierp:'\u2118',real:'\u211C',trade:'\u2122',alefsym:'\u2135',larr:'\u2190',uarr:'\u2191',rarr:'\u2192',darr:'\u2193',harr:'\u2194',crarr:'\u21B5',lArr:'\u21D0',uArr:'\u21D1',rArr:'\u21D2',dArr:'\u21D3',hArr:'\u21D4',forall:'\u2200',part:'\u2202',exist:'\u2203',empty:'\u2205',nabla:'\u2207',isin:'\u2208',notin:'\u2209',ni:'\u220B',prod:'\u220F',sum:'\u2211',minus:'\u2212',lowast:'\u2217',radic:'\u221A',prop:'\u221D',infin:'\u221E',ang:'\u2220',and:'\u2227',or:'\u2228',cap:'\u2229',cup:'\u222A',int:'\u222B',there4:'\u2234',sim:'\u223C',cong:'\u2245',asymp:'\u2248',ne:'\u2260',equiv:'\u2261',le:'\u2264',ge:'\u2265',sub:'\u2282',sup:'\u2283',nsub:'\u2284',sube:'\u2286',supe:'\u2287',oplus:'\u2295',otimes:'\u2297',perp:'\u22A5',sdot:'\u22C5',lceil:'\u2308',rceil:'\u2309',lfloor:'\u230A',rfloor:'\u230B',loz:'\u25CA',spades:'\u2660',clubs:'\u2663',hearts:'\u2665',diams:'\u2666',lang:'\u27E8',rang:'\u27E9'};/***/},/* 15 */ /***/function(module,exports,__webpack_require__){Object.defineProperty(exports,"__esModule",{value:true});var error_handler_1=__webpack_require__(10);var scanner_1=__webpack_require__(12);var token_1=__webpack_require__(13);var Reader=function(){function Reader(){this.values=[];this.curly=this.paren=-1;}// A function following one of those tokens is an expression.
  Reader.prototype.beforeFunctionExpression=function(t){return ['(','{','[','in','typeof','instanceof','new','return','case','delete','throw','void',// assignment operators
  '=','+=','-=','*=','**=','/=','%=','<<=','>>=','>>>=','&=','|=','^=',',',// binary/unary operators
  '+','-','*','**','/','%','++','--','<<','>>','>>>','&','|','^','!','~','&&','||','?',':','===','==','>=','<=','<','>','!=','!=='].indexOf(t)>=0;};// Determine if forward slash (/) is an operator or part of a regular expression
  // https://github.com/mozilla/sweet.js/wiki/design
  Reader.prototype.isRegexStart=function(){var previous=this.values[this.values.length-1];var regex=previous!==null;switch(previous){case'this':case']':regex=false;break;case')':var keyword=this.values[this.paren-1];regex=keyword==='if'||keyword==='while'||keyword==='for'||keyword==='with';break;case'}':// Dividing a function by anything makes little sense,
  // but we have to check for that.
  regex=false;if(this.values[this.curly-3]==='function'){// Anonymous function, e.g. function(){} /42
  var check=this.values[this.curly-4];regex=check?!this.beforeFunctionExpression(check):false;}else if(this.values[this.curly-4]==='function'){// Named function, e.g. function f(){} /42/
  var check=this.values[this.curly-5];regex=check?!this.beforeFunctionExpression(check):true;}break;default:break;}return regex;};Reader.prototype.push=function(token){if(token.type===7/* Punctuator */||token.type===4/* Keyword */){if(token.value==='{'){this.curly=this.values.length;}else if(token.value==='('){this.paren=this.values.length;}this.values.push(token.value);}else{this.values.push(null);}};return Reader;}();var Tokenizer=function(){function Tokenizer(code,config){this.errorHandler=new error_handler_1.ErrorHandler();this.errorHandler.tolerant=config?typeof config.tolerant==='boolean'&&config.tolerant:false;this.scanner=new scanner_1.Scanner(code,this.errorHandler);this.scanner.trackComment=config?typeof config.comment==='boolean'&&config.comment:false;this.trackRange=config?typeof config.range==='boolean'&&config.range:false;this.trackLoc=config?typeof config.loc==='boolean'&&config.loc:false;this.buffer=[];this.reader=new Reader();}Tokenizer.prototype.errors=function(){return this.errorHandler.errors;};Tokenizer.prototype.getNextToken=function(){if(this.buffer.length===0){var comments=this.scanner.scanComments();if(this.scanner.trackComment){for(var i=0;i<comments.length;++i){var e=comments[i];var value=this.scanner.source.slice(e.slice[0],e.slice[1]);var comment={type:e.multiLine?'BlockComment':'LineComment',value:value};if(this.trackRange){comment.range=e.range;}if(this.trackLoc){comment.loc=e.loc;}this.buffer.push(comment);}}if(!this.scanner.eof()){var loc=void 0;if(this.trackLoc){loc={start:{line:this.scanner.lineNumber,column:this.scanner.index-this.scanner.lineStart},end:{}};}var startRegex=this.scanner.source[this.scanner.index]==='/'&&this.reader.isRegexStart();var token=startRegex?this.scanner.scanRegExp():this.scanner.lex();this.reader.push(token);var entry={type:token_1.TokenName[token.type],value:this.scanner.source.slice(token.start,token.end)};if(this.trackRange){entry.range=[token.start,token.end];}if(this.trackLoc){loc.end={line:this.scanner.lineNumber,column:this.scanner.index-this.scanner.lineStart};entry.loc=loc;}if(token.type===9/* RegularExpression */){var pattern=token.pattern;var flags=token.flags;entry.regex={pattern:pattern,flags:flags};}this.buffer.push(entry);}}return this.buffer.shift();};return Tokenizer;}();exports.Tokenizer=Tokenizer;/***/}/******/]));});});unwrapExports(esprima$1);var esprima$2=createCommonjsModule(function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});// This module is suitable for passing as options.parser when calling
  // recast.parse to process ECMAScript code with Esprima:
  //
  //   const ast = recast.parse(source, {
  //     parser: require("recast/parsers/esprima")
  //   });
  //
  function parse(source,options){var comments=[];var ast=esprima$1.parse(source,{loc:true,locations:true,comment:true,onComment:comments,range:util.getOption(options,"range",false),tolerant:util.getOption(options,"tolerant",true),tokens:true});if(!Array.isArray(ast.comments)){ast.comments=comments;}return ast;}exports.parse=parse;});unwrapExports(esprima$2);var esprima_1=esprima$2.parse;/*
  	The MIT License (MIT)

  	Copyright (c) 2016 CoderPuppy

  	Permission is hereby granted, free of charge, to any person obtaining a copy
  	of this software and associated documentation files (the "Software"), to deal
  	in the Software without restriction, including without limitation the rights
  	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  	copies of the Software, and to permit persons to whom the Software is
  	furnished to do so, subject to the following conditions:

  	The above copyright notice and this permission notice shall be included in all
  	copies or substantial portions of the Software.

  	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  	SOFTWARE.

  	*/var _endianness;function endianness(){if(typeof _endianness==='undefined'){var a=new ArrayBuffer(2);var b=new Uint8Array(a);var c=new Uint16Array(a);b[0]=1;b[1]=2;if(c[0]===258){_endianness='BE';}else if(c[0]===513){_endianness='LE';}else{throw new Error('unable to figure out endianess');}}return _endianness;}function hostname(){if(typeof commonjsGlobal.location!=='undefined'){return commonjsGlobal.location.hostname;}else return '';}function loadavg(){return [];}function uptime(){return 0;}function freemem(){return Number.MAX_VALUE;}function totalmem(){return Number.MAX_VALUE;}function cpus(){return [];}function type(){return 'Browser';}function release(){if(typeof commonjsGlobal.navigator!=='undefined'){return commonjsGlobal.navigator.appVersion;}return '';}function networkInterfaces(){}function getNetworkInterfaces(){}function tmpDir(){return '/tmp';}var tmpdir=tmpDir;var EOL='\n';var require$$1={EOL:EOL,tmpdir:tmpdir,tmpDir:tmpDir,networkInterfaces:networkInterfaces,getNetworkInterfaces:getNetworkInterfaces,release:release,type:type,cpus:cpus,totalmem:totalmem,freemem:freemem,uptime:uptime,loadavg:loadavg,hostname:hostname,endianness:endianness};var options=createCommonjsModule(function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});var defaults={parser:esprima$2,tabWidth:4,useTabs:false,reuseWhitespace:true,lineTerminator:require$$1.EOL,wrapColumn:74,sourceFileName:null,sourceMapName:null,sourceRoot:null,inputSourceMap:null,range:false,tolerant:true,quote:null,trailingComma:false,arrayBracketSpacing:false,objectCurlySpacing:true,arrowParensAlways:false,flowObjectCommas:true,tokens:true},hasOwn=defaults.hasOwnProperty;// Copy options and fill in default values.
  function normalize(opts){var options=opts||defaults;function get(key){return hasOwn.call(options,key)?options[key]:defaults[key];}return {tabWidth:+get("tabWidth"),useTabs:!!get("useTabs"),reuseWhitespace:!!get("reuseWhitespace"),lineTerminator:get("lineTerminator"),wrapColumn:Math.max(get("wrapColumn"),0),sourceFileName:get("sourceFileName"),sourceMapName:get("sourceMapName"),sourceRoot:get("sourceRoot"),inputSourceMap:get("inputSourceMap"),parser:get("esprima")||get("parser"),range:get("range"),tolerant:get("tolerant"),quote:get("quote"),trailingComma:get("trailingComma"),arrayBracketSpacing:get("arrayBracketSpacing"),objectCurlySpacing:get("objectCurlySpacing"),arrowParensAlways:get("arrowParensAlways"),flowObjectCommas:get("flowObjectCommas"),tokens:!!get("tokens")};}exports.normalize=normalize;});unwrapExports(options);var options_1=options.normalize;var mapping=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var Mapping=/** @class */function(){function Mapping(sourceLines,sourceLoc,targetLoc){if(targetLoc===void 0){targetLoc=sourceLoc;}this.sourceLines=sourceLines;this.sourceLoc=sourceLoc;this.targetLoc=targetLoc;}Mapping.prototype.slice=function(lines,start,end){if(end===void 0){end=lines.lastPos();}var sourceLines=this.sourceLines;var sourceLoc=this.sourceLoc;var targetLoc=this.targetLoc;function skip(name){var sourceFromPos=sourceLoc[name];var targetFromPos=targetLoc[name];var targetToPos=start;if(name==="end"){targetToPos=end;}else{assert_1.default.strictEqual(name,"start");}return skipChars(sourceLines,sourceFromPos,lines,targetFromPos,targetToPos);}if(util.comparePos(start,targetLoc.start)<=0){if(util.comparePos(targetLoc.end,end)<=0){targetLoc={start:subtractPos(targetLoc.start,start.line,start.column),end:subtractPos(targetLoc.end,start.line,start.column)};// The sourceLoc can stay the same because the contents of the
  // targetLoc have not changed.
  }else if(util.comparePos(end,targetLoc.start)<=0){return null;}else{sourceLoc={start:sourceLoc.start,end:skip("end")};targetLoc={start:subtractPos(targetLoc.start,start.line,start.column),end:subtractPos(end,start.line,start.column)};}}else{if(util.comparePos(targetLoc.end,start)<=0){return null;}if(util.comparePos(targetLoc.end,end)<=0){sourceLoc={start:skip("start"),end:sourceLoc.end};targetLoc={// Same as subtractPos(start, start.line, start.column):
  start:{line:1,column:0},end:subtractPos(targetLoc.end,start.line,start.column)};}else{sourceLoc={start:skip("start"),end:skip("end")};targetLoc={// Same as subtractPos(start, start.line, start.column):
  start:{line:1,column:0},end:subtractPos(end,start.line,start.column)};}}return new Mapping(this.sourceLines,sourceLoc,targetLoc);};Mapping.prototype.add=function(line,column){return new Mapping(this.sourceLines,this.sourceLoc,{start:addPos(this.targetLoc.start,line,column),end:addPos(this.targetLoc.end,line,column)});};Mapping.prototype.subtract=function(line,column){return new Mapping(this.sourceLines,this.sourceLoc,{start:subtractPos(this.targetLoc.start,line,column),end:subtractPos(this.targetLoc.end,line,column)});};Mapping.prototype.indent=function(by,skipFirstLine,noNegativeColumns){if(skipFirstLine===void 0){skipFirstLine=false;}if(noNegativeColumns===void 0){noNegativeColumns=false;}if(by===0){return this;}var targetLoc=this.targetLoc;var startLine=targetLoc.start.line;var endLine=targetLoc.end.line;if(skipFirstLine&&startLine===1&&endLine===1){return this;}targetLoc={start:targetLoc.start,end:targetLoc.end};if(!skipFirstLine||startLine>1){var startColumn=targetLoc.start.column+by;targetLoc.start={line:startLine,column:noNegativeColumns?Math.max(0,startColumn):startColumn};}if(!skipFirstLine||endLine>1){var endColumn=targetLoc.end.column+by;targetLoc.end={line:endLine,column:noNegativeColumns?Math.max(0,endColumn):endColumn};}return new Mapping(this.sourceLines,this.sourceLoc,targetLoc);};return Mapping;}();exports.default=Mapping;function addPos(toPos,line,column){return {line:toPos.line+line-1,column:toPos.line===1?toPos.column+column:toPos.column};}function subtractPos(fromPos,line,column){return {line:fromPos.line-line+1,column:fromPos.line===line?fromPos.column-column:fromPos.column};}function skipChars(sourceLines,sourceFromPos,targetLines,targetFromPos,targetToPos){var targetComparison=util.comparePos(targetFromPos,targetToPos);if(targetComparison===0){// Trivial case: no characters to skip.
  return sourceFromPos;}if(targetComparison<0){// Skipping forward.
  var sourceCursor=sourceLines.skipSpaces(sourceFromPos)||sourceLines.lastPos();var targetCursor=targetLines.skipSpaces(targetFromPos)||targetLines.lastPos();var lineDiff=targetToPos.line-targetCursor.line;sourceCursor.line+=lineDiff;targetCursor.line+=lineDiff;if(lineDiff>0){// If jumping to later lines, reset columns to the beginnings
  // of those lines.
  sourceCursor.column=0;targetCursor.column=0;}else{assert_1.default.strictEqual(lineDiff,0);}while(util.comparePos(targetCursor,targetToPos)<0&&targetLines.nextPos(targetCursor,true)){assert_1.default.ok(sourceLines.nextPos(sourceCursor,true));assert_1.default.strictEqual(sourceLines.charAt(sourceCursor),targetLines.charAt(targetCursor));}}else{// Skipping backward.
  var sourceCursor=sourceLines.skipSpaces(sourceFromPos,true)||sourceLines.firstPos();var targetCursor=targetLines.skipSpaces(targetFromPos,true)||targetLines.firstPos();var lineDiff=targetToPos.line-targetCursor.line;sourceCursor.line+=lineDiff;targetCursor.line+=lineDiff;if(lineDiff<0){// If jumping to earlier lines, reset columns to the ends of
  // those lines.
  sourceCursor.column=sourceLines.getLineLength(sourceCursor.line);targetCursor.column=targetLines.getLineLength(targetCursor.line);}else{assert_1.default.strictEqual(lineDiff,0);}while(util.comparePos(targetToPos,targetCursor)<0&&targetLines.prevPos(targetCursor,true)){assert_1.default.ok(sourceLines.prevPos(sourceCursor,true));assert_1.default.strictEqual(sourceLines.charAt(sourceCursor),targetLines.charAt(targetCursor));}}return sourceCursor;}});unwrapExports(mapping);var lines=createCommonjsModule(function(module,exports){var __assign=this&&this.__assign||function(){__assign=Object.assign||function(t){for(var s,i=1,n=arguments.length;i<n;i++){s=arguments[i];for(var p in s)if(Object.prototype.hasOwnProperty.call(s,p))t[p]=s[p];}return t;};return __assign.apply(this,arguments);};var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var source_map_1=__importDefault(sourceMap);var mapping_1=__importDefault(mapping);var Lines=/** @class */function(){function Lines(infos,sourceFileName){if(sourceFileName===void 0){sourceFileName=null;}this.infos=infos;this.mappings=[];this.cachedSourceMap=null;this.cachedTabWidth=void 0;assert_1.default.ok(infos.length>0);this.length=infos.length;this.name=sourceFileName||null;if(this.name){this.mappings.push(new mapping_1.default(this,{start:this.firstPos(),end:this.lastPos()}));}}Lines.prototype.toString=function(options){return this.sliceString(this.firstPos(),this.lastPos(),options);};Lines.prototype.getSourceMap=function(sourceMapName,sourceRoot){if(!sourceMapName){// Although we could make up a name or generate an anonymous
  // source map, instead we assume that any consumer who does not
  // provide a name does not actually want a source map.
  return null;}var targetLines=this;function updateJSON(json){json=json||{};json.file=sourceMapName;if(sourceRoot){json.sourceRoot=sourceRoot;}return json;}if(targetLines.cachedSourceMap){// Since Lines objects are immutable, we can reuse any source map
  // that was previously generated. Nevertheless, we return a new
  // JSON object here to protect the cached source map from outside
  // modification.
  return updateJSON(targetLines.cachedSourceMap.toJSON());}var smg=new source_map_1.default.SourceMapGenerator(updateJSON());var sourcesToContents={};targetLines.mappings.forEach(function(mapping){var sourceCursor=mapping.sourceLines.skipSpaces(mapping.sourceLoc.start)||mapping.sourceLines.lastPos();var targetCursor=targetLines.skipSpaces(mapping.targetLoc.start)||targetLines.lastPos();while(util.comparePos(sourceCursor,mapping.sourceLoc.end)<0&&util.comparePos(targetCursor,mapping.targetLoc.end)<0){var sourceChar=mapping.sourceLines.charAt(sourceCursor);var targetChar=targetLines.charAt(targetCursor);assert_1.default.strictEqual(sourceChar,targetChar);var sourceName=mapping.sourceLines.name;// Add mappings one character at a time for maximum resolution.
  smg.addMapping({source:sourceName,original:{line:sourceCursor.line,column:sourceCursor.column},generated:{line:targetCursor.line,column:targetCursor.column}});if(!hasOwn.call(sourcesToContents,sourceName)){var sourceContent=mapping.sourceLines.toString();smg.setSourceContent(sourceName,sourceContent);sourcesToContents[sourceName]=sourceContent;}targetLines.nextPos(targetCursor,true);mapping.sourceLines.nextPos(sourceCursor,true);}});targetLines.cachedSourceMap=smg;return smg.toJSON();};Lines.prototype.bootstrapCharAt=function(pos){assert_1.default.strictEqual(typeof pos,"object");assert_1.default.strictEqual(typeof pos.line,"number");assert_1.default.strictEqual(typeof pos.column,"number");var line=pos.line,column=pos.column,strings=this.toString().split(lineTerminatorSeqExp),string=strings[line-1];if(typeof string==="undefined")return "";if(column===string.length&&line<strings.length)return "\n";if(column>=string.length)return "";return string.charAt(column);};Lines.prototype.charAt=function(pos){assert_1.default.strictEqual(typeof pos,"object");assert_1.default.strictEqual(typeof pos.line,"number");assert_1.default.strictEqual(typeof pos.column,"number");var line=pos.line,column=pos.column,secret=this,infos=secret.infos,info=infos[line-1],c=column;if(typeof info==="undefined"||c<0)return "";var indent=this.getIndentAt(line);if(c<indent)return " ";c+=info.sliceStart-indent;if(c===info.sliceEnd&&line<this.length)return "\n";if(c>=info.sliceEnd)return "";return info.line.charAt(c);};Lines.prototype.stripMargin=function(width,skipFirstLine){if(width===0)return this;assert_1.default.ok(width>0,"negative margin: "+width);if(skipFirstLine&&this.length===1)return this;var lines=new Lines(this.infos.map(function(info,i){if(info.line&&(i>0||!skipFirstLine)){info=__assign({},info,{indent:Math.max(0,info.indent-width)});}return info;}));if(this.mappings.length>0){var newMappings=lines.mappings;assert_1.default.strictEqual(newMappings.length,0);this.mappings.forEach(function(mapping){newMappings.push(mapping.indent(width,skipFirstLine,true));});}return lines;};Lines.prototype.indent=function(by){if(by===0){return this;}var lines=new Lines(this.infos.map(function(info){if(info.line&&!info.locked){info=__assign({},info,{indent:info.indent+by});}return info;}));if(this.mappings.length>0){var newMappings=lines.mappings;assert_1.default.strictEqual(newMappings.length,0);this.mappings.forEach(function(mapping){newMappings.push(mapping.indent(by));});}return lines;};Lines.prototype.indentTail=function(by){if(by===0){return this;}if(this.length<2){return this;}var lines=new Lines(this.infos.map(function(info,i){if(i>0&&info.line&&!info.locked){info=__assign({},info,{indent:info.indent+by});}return info;}));if(this.mappings.length>0){var newMappings=lines.mappings;assert_1.default.strictEqual(newMappings.length,0);this.mappings.forEach(function(mapping){newMappings.push(mapping.indent(by,true));});}return lines;};Lines.prototype.lockIndentTail=function(){if(this.length<2){return this;}return new Lines(this.infos.map(function(info,i){return __assign({},info,{locked:i>0});}));};Lines.prototype.getIndentAt=function(line){assert_1.default.ok(line>=1,"no line "+line+" (line numbers start from 1)");return Math.max(this.infos[line-1].indent,0);};Lines.prototype.guessTabWidth=function(){if(typeof this.cachedTabWidth==="number"){return this.cachedTabWidth;}var counts=[];// Sparse array.
  var lastIndent=0;for(var line=1,last=this.length;line<=last;++line){var info=this.infos[line-1];var sliced=info.line.slice(info.sliceStart,info.sliceEnd);// Whitespace-only lines don't tell us much about the likely tab
  // width of this code.
  if(isOnlyWhitespace(sliced)){continue;}var diff=Math.abs(info.indent-lastIndent);counts[diff]=~~counts[diff]+1;lastIndent=info.indent;}var maxCount=-1;var result=2;for(var tabWidth=1;tabWidth<counts.length;tabWidth+=1){if(hasOwn.call(counts,tabWidth)&&counts[tabWidth]>maxCount){maxCount=counts[tabWidth];result=tabWidth;}}return this.cachedTabWidth=result;};// Determine if the list of lines has a first line that starts with a //
  // or /* comment. If this is the case, the code may need to be wrapped in
  // parens to avoid ASI issues.
  Lines.prototype.startsWithComment=function(){if(this.infos.length===0){return false;}var firstLineInfo=this.infos[0],sliceStart=firstLineInfo.sliceStart,sliceEnd=firstLineInfo.sliceEnd,firstLine=firstLineInfo.line.slice(sliceStart,sliceEnd).trim();return firstLine.length===0||firstLine.slice(0,2)==="//"||firstLine.slice(0,2)==="/*";};Lines.prototype.isOnlyWhitespace=function(){return isOnlyWhitespace(this.toString());};Lines.prototype.isPrecededOnlyByWhitespace=function(pos){var info=this.infos[pos.line-1];var indent=Math.max(info.indent,0);var diff=pos.column-indent;if(diff<=0){// If pos.column does not exceed the indentation amount, then
  // there must be only whitespace before it.
  return true;}var start=info.sliceStart;var end=Math.min(start+diff,info.sliceEnd);var prefix=info.line.slice(start,end);return isOnlyWhitespace(prefix);};Lines.prototype.getLineLength=function(line){var info=this.infos[line-1];return this.getIndentAt(line)+info.sliceEnd-info.sliceStart;};Lines.prototype.nextPos=function(pos,skipSpaces){if(skipSpaces===void 0){skipSpaces=false;}var l=Math.max(pos.line,0),c=Math.max(pos.column,0);if(c<this.getLineLength(l)){pos.column+=1;return skipSpaces?!!this.skipSpaces(pos,false,true):true;}if(l<this.length){pos.line+=1;pos.column=0;return skipSpaces?!!this.skipSpaces(pos,false,true):true;}return false;};Lines.prototype.prevPos=function(pos,skipSpaces){if(skipSpaces===void 0){skipSpaces=false;}var l=pos.line,c=pos.column;if(c<1){l-=1;if(l<1)return false;c=this.getLineLength(l);}else{c=Math.min(c-1,this.getLineLength(l));}pos.line=l;pos.column=c;return skipSpaces?!!this.skipSpaces(pos,true,true):true;};Lines.prototype.firstPos=function(){// Trivial, but provided for completeness.
  return {line:1,column:0};};Lines.prototype.lastPos=function(){return {line:this.length,column:this.getLineLength(this.length)};};Lines.prototype.skipSpaces=function(pos,backward,modifyInPlace){if(backward===void 0){backward=false;}if(modifyInPlace===void 0){modifyInPlace=false;}if(pos){pos=modifyInPlace?pos:{line:pos.line,column:pos.column};}else if(backward){pos=this.lastPos();}else{pos=this.firstPos();}if(backward){while(this.prevPos(pos)){if(!isOnlyWhitespace(this.charAt(pos))&&this.nextPos(pos)){return pos;}}return null;}else{while(isOnlyWhitespace(this.charAt(pos))){if(!this.nextPos(pos)){return null;}}return pos;}};Lines.prototype.trimLeft=function(){var pos=this.skipSpaces(this.firstPos(),false,true);return pos?this.slice(pos):emptyLines;};Lines.prototype.trimRight=function(){var pos=this.skipSpaces(this.lastPos(),true,true);return pos?this.slice(this.firstPos(),pos):emptyLines;};Lines.prototype.trim=function(){var start=this.skipSpaces(this.firstPos(),false,true);if(start===null){return emptyLines;}var end=this.skipSpaces(this.lastPos(),true,true);if(end===null){return emptyLines;}return this.slice(start,end);};Lines.prototype.eachPos=function(callback,startPos,skipSpaces){if(startPos===void 0){startPos=this.firstPos();}if(skipSpaces===void 0){skipSpaces=false;}var pos=this.firstPos();if(startPos){pos.line=startPos.line,pos.column=startPos.column;}if(skipSpaces&&!this.skipSpaces(pos,false,true)){return;// Encountered nothing but spaces.
  }do callback.call(this,pos);while(this.nextPos(pos,skipSpaces));};Lines.prototype.bootstrapSlice=function(start,end){var strings=this.toString().split(lineTerminatorSeqExp).slice(start.line-1,end.line);if(strings.length>0){strings.push(strings.pop().slice(0,end.column));strings[0]=strings[0].slice(start.column);}return fromString(strings.join("\n"));};Lines.prototype.slice=function(start,end){if(!end){if(!start){// The client seems to want a copy of this Lines object, but
  // Lines objects are immutable, so it's perfectly adequate to
  // return the same object.
  return this;}// Slice to the end if no end position was provided.
  end=this.lastPos();}if(!start){throw new Error("cannot slice with end but not start");}var sliced=this.infos.slice(start.line-1,end.line);if(start.line===end.line){sliced[0]=sliceInfo(sliced[0],start.column,end.column);}else{assert_1.default.ok(start.line<end.line);sliced[0]=sliceInfo(sliced[0],start.column);sliced.push(sliceInfo(sliced.pop(),0,end.column));}var lines=new Lines(sliced);if(this.mappings.length>0){var newMappings=lines.mappings;assert_1.default.strictEqual(newMappings.length,0);this.mappings.forEach(function(mapping){var sliced=mapping.slice(this,start,end);if(sliced){newMappings.push(sliced);}},this);}return lines;};Lines.prototype.bootstrapSliceString=function(start,end,options){return this.slice(start,end).toString(options);};Lines.prototype.sliceString=function(start,end,options$1){if(start===void 0){start=this.firstPos();}if(end===void 0){end=this.lastPos();}options$1=options.normalize(options$1);var parts=[];var _a=options$1.tabWidth,tabWidth=_a===void 0?2:_a;for(var line=start.line;line<=end.line;++line){var info=this.infos[line-1];if(line===start.line){if(line===end.line){info=sliceInfo(info,start.column,end.column);}else{info=sliceInfo(info,start.column);}}else if(line===end.line){info=sliceInfo(info,0,end.column);}var indent=Math.max(info.indent,0);var before=info.line.slice(0,info.sliceStart);if(options$1.reuseWhitespace&&isOnlyWhitespace(before)&&countSpaces(before,options$1.tabWidth)===indent){// Reuse original spaces if the indentation is correct.
  parts.push(info.line.slice(0,info.sliceEnd));continue;}var tabs=0;var spaces=indent;if(options$1.useTabs){tabs=Math.floor(indent/tabWidth);spaces-=tabs*tabWidth;}var result="";if(tabs>0){result+=new Array(tabs+1).join("\t");}if(spaces>0){result+=new Array(spaces+1).join(" ");}result+=info.line.slice(info.sliceStart,info.sliceEnd);parts.push(result);}return parts.join(options$1.lineTerminator);};Lines.prototype.isEmpty=function(){return this.length<2&&this.getLineLength(1)<1;};Lines.prototype.join=function(elements){var separator=this;var infos=[];var mappings=[];var prevInfo;function appendLines(linesOrNull){if(linesOrNull===null){return;}if(prevInfo){var info=linesOrNull.infos[0];var indent=new Array(info.indent+1).join(" ");var prevLine=infos.length;var prevColumn=Math.max(prevInfo.indent,0)+prevInfo.sliceEnd-prevInfo.sliceStart;prevInfo.line=prevInfo.line.slice(0,prevInfo.sliceEnd)+indent+info.line.slice(info.sliceStart,info.sliceEnd);// If any part of a line is indentation-locked, the whole line
  // will be indentation-locked.
  prevInfo.locked=prevInfo.locked||info.locked;prevInfo.sliceEnd=prevInfo.line.length;if(linesOrNull.mappings.length>0){linesOrNull.mappings.forEach(function(mapping){mappings.push(mapping.add(prevLine,prevColumn));});}}else if(linesOrNull.mappings.length>0){mappings.push.apply(mappings,linesOrNull.mappings);}linesOrNull.infos.forEach(function(info,i){if(!prevInfo||i>0){prevInfo=__assign({},info);infos.push(prevInfo);}});}function appendWithSeparator(linesOrNull,i){if(i>0)appendLines(separator);appendLines(linesOrNull);}elements.map(function(elem){var lines=fromString(elem);if(lines.isEmpty())return null;return lines;}).forEach(function(linesOrNull,i){if(separator.isEmpty()){appendLines(linesOrNull);}else{appendWithSeparator(linesOrNull,i);}});if(infos.length<1)return emptyLines;var lines=new Lines(infos);lines.mappings=mappings;return lines;};Lines.prototype.concat=function(){var args=[];for(var _i=0;_i<arguments.length;_i++){args[_i]=arguments[_i];}var list=[this];list.push.apply(list,args);assert_1.default.strictEqual(list.length,args.length+1);return emptyLines.join(list);};return Lines;}();exports.Lines=Lines;var fromStringCache={};var hasOwn=fromStringCache.hasOwnProperty;var maxCacheKeyLen=10;function countSpaces(spaces,tabWidth){var count=0;var len=spaces.length;for(var i=0;i<len;++i){switch(spaces.charCodeAt(i)){case 9:// '\t'
  assert_1.default.strictEqual(typeof tabWidth,"number");assert_1.default.ok(tabWidth>0);var next=Math.ceil(count/tabWidth)*tabWidth;if(next===count){count+=tabWidth;}else{count=next;}break;case 11:// '\v'
  case 12:// '\f'
  case 13:// '\r'
  case 0xfeff:// zero-width non-breaking space
  // These characters contribute nothing to indentation.
  break;case 32:// ' '
  default:// Treat all other whitespace like ' '.
  count+=1;break;}}return count;}exports.countSpaces=countSpaces;var leadingSpaceExp=/^\s*/;// As specified here: http://www.ecma-international.org/ecma-262/6.0/#sec-line-terminators
  var lineTerminatorSeqExp=/\u000D\u000A|\u000D(?!\u000A)|\u000A|\u2028|\u2029/;/**
  	 * @param {Object} options - Options object that configures printing.
  	 */function fromString(string,options$1){if(string instanceof Lines)return string;string+="";var tabWidth=options$1&&options$1.tabWidth;var tabless=string.indexOf("\t")<0;var cacheable=!options$1&&tabless&&string.length<=maxCacheKeyLen;assert_1.default.ok(tabWidth||tabless,"No tab width specified but encountered tabs in string\n"+string);if(cacheable&&hasOwn.call(fromStringCache,string))return fromStringCache[string];var lines=new Lines(string.split(lineTerminatorSeqExp).map(function(line){// TODO: handle null exec result
  var spaces=leadingSpaceExp.exec(line)[0];return {line:line,indent:countSpaces(spaces,tabWidth),// Boolean indicating whether this line can be reindented.
  locked:false,sliceStart:spaces.length,sliceEnd:line.length};}),options.normalize(options$1).sourceFileName);if(cacheable)fromStringCache[string]=lines;return lines;}exports.fromString=fromString;function isOnlyWhitespace(string){return !/\S/.test(string);}function sliceInfo(info,startCol,endCol){var sliceStart=info.sliceStart;var sliceEnd=info.sliceEnd;var indent=Math.max(info.indent,0);var lineLength=indent+sliceEnd-sliceStart;if(typeof endCol==="undefined"){endCol=lineLength;}startCol=Math.max(startCol,0);endCol=Math.min(endCol,lineLength);endCol=Math.max(endCol,startCol);if(endCol<indent){indent=endCol;sliceEnd=sliceStart;}else{sliceEnd-=lineLength-endCol;}lineLength=endCol;lineLength-=startCol;if(startCol<indent){indent-=startCol;}else{startCol-=indent;indent=0;sliceStart+=startCol;}assert_1.default.ok(indent>=0);assert_1.default.ok(sliceStart<=sliceEnd);assert_1.default.strictEqual(lineLength,indent+sliceEnd-sliceStart);if(info.indent===indent&&info.sliceStart===sliceStart&&info.sliceEnd===sliceEnd){return info;}return {line:info.line,indent:indent,// A destructive slice always unlocks indentation.
  locked:false,sliceStart:sliceStart,sliceEnd:sliceEnd};}function concat(elements){return emptyLines.join(elements);}exports.concat=concat;// The emptyLines object needs to be created all the way down here so that
  // Lines.prototype will be fully populated.
  var emptyLines=fromString("");});unwrapExports(lines);var lines_1=lines.Lines;var lines_2=lines.countSpaces;var lines_3=lines.fromString;var lines_4=lines.concat;var originalObject=Object;var originalDefProp=Object.defineProperty;var originalCreate=Object.create;function defProp(obj,name,value){if(originalDefProp)try{originalDefProp.call(originalObject,obj,name,{value:value});}catch(definePropertyIsBrokenInIE8){obj[name]=value;}else{obj[name]=value;}}// For functions that will be invoked using .call or .apply, we need to
  // define those methods on the function objects themselves, rather than
  // inheriting them from Function.prototype, so that a malicious or clumsy
  // third party cannot interfere with the functionality of this module by
  // redefining Function.prototype.call or .apply.
  function makeSafeToCall(fun){if(fun){defProp(fun,"call",fun.call);defProp(fun,"apply",fun.apply);}return fun;}makeSafeToCall(originalDefProp);makeSafeToCall(originalCreate);var hasOwn$1=makeSafeToCall(Object.prototype.hasOwnProperty);var numToStr=makeSafeToCall(Number.prototype.toString);var strSlice=makeSafeToCall(String.prototype.slice);var cloner=function cloner(){};function create(prototype){if(originalCreate){return originalCreate.call(originalObject,prototype);}cloner.prototype=prototype||null;return new cloner();}var rand=Math.random;var uniqueKeys=create(null);function makeUniqueKey(){// Collisions are highly unlikely, but this module is in the business of
  // making guarantees rather than safe bets.
  do var uniqueKey=internString(strSlice.call(numToStr.call(rand(),36),2));while(hasOwn$1.call(uniqueKeys,uniqueKey));return uniqueKeys[uniqueKey]=uniqueKey;}function internString(str){var obj={};obj[str]=true;return Object.keys(obj)[0];}// External users might find this function useful, but it is not necessary
  // for the typical use of this module.
  var makeUniqueKey_1=makeUniqueKey;// Object.getOwnPropertyNames is the only way to enumerate non-enumerable
  // properties, so if we wrap it to ignore our secret keys, there should be
  // no way (except guessing) to access those properties.
  var originalGetOPNs=Object.getOwnPropertyNames;Object.getOwnPropertyNames=function getOwnPropertyNames(object){for(var names=originalGetOPNs(object),src=0,dst=0,len=names.length;src<len;++src){if(!hasOwn$1.call(uniqueKeys,names[src])){if(src>dst){names[dst]=names[src];}++dst;}}names.length=dst;return names;};function defaultCreatorFn(object){return create(null);}function makeAccessor(secretCreatorFn){var brand=makeUniqueKey();var passkey=create(null);secretCreatorFn=secretCreatorFn||defaultCreatorFn;function register(object){var secret;// Created lazily.
  function vault(key,forget){// Only code that has access to the passkey can retrieve (or forget)
  // the secret object.
  if(key===passkey){return forget?secret=null:secret||(secret=secretCreatorFn(object));}}defProp(object,brand,vault);}function accessor(object){if(!hasOwn$1.call(object,brand))register(object);return object[brand](passkey);}accessor.forget=function(object){if(hasOwn$1.call(object,brand))object[brand](passkey,true);};return accessor;}var makeAccessor_1=makeAccessor;var _private={makeUniqueKey:makeUniqueKey_1,makeAccessor:makeAccessor_1};var comments=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};var __importStar=this&&this.__importStar||function(mod){if(mod&&mod.__esModule)return mod;var result={};if(mod!=null)for(var k in mod)if(Object.hasOwnProperty.call(mod,k))result[k]=mod[k];result["default"]=mod;return result;};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var types=__importStar(main);var n=types.namedTypes;var isArray=types.builtInTypes.array;var isObject=types.builtInTypes.object;var childNodesCacheKey=_private.makeUniqueKey();// TODO Move a non-caching implementation of this function into ast-types,
  // and implement a caching wrapper function here.
  function getSortedChildNodes(node,lines,resultArray){if(!node){return;}// The .loc checks below are sensitive to some of the problems that
  // are fixed by this utility function. Specifically, if it decides to
  // set node.loc to null, indicating that the node's .loc information
  // is unreliable, then we don't want to add node to the resultArray.
  util.fixFaultyLocations(node,lines);if(resultArray){if(n.Node.check(node)&&n.SourceLocation.check(node.loc)){// This reverse insertion sort almost always takes constant
  // time because we almost always (maybe always?) append the
  // nodes in order anyway.
  for(var i=resultArray.length-1;i>=0;--i){if(util.comparePos(resultArray[i].loc.end,node.loc.start)<=0){break;}}resultArray.splice(i+1,0,node);return;}}else if(node[childNodesCacheKey]){return node[childNodesCacheKey];}var names;if(isArray.check(node)){names=Object.keys(node);}else if(isObject.check(node)){names=types.getFieldNames(node);}else{return;}if(!resultArray){Object.defineProperty(node,childNodesCacheKey,{value:resultArray=[],enumerable:false});}for(var i=0,nameCount=names.length;i<nameCount;++i){getSortedChildNodes(node[names[i]],lines,resultArray);}return resultArray;}// As efficiently as possible, decorate the comment object with
  // .precedingNode, .enclosingNode, and/or .followingNode properties, at
  // least one of which is guaranteed to be defined.
  function decorateComment(node,comment,lines){var childNodes=getSortedChildNodes(node,lines);// Time to dust off the old binary search robes and wizard hat.
  var left=0,right=childNodes.length;while(left<right){var middle=left+right>>1;var child=childNodes[middle];if(util.comparePos(child.loc.start,comment.loc.start)<=0&&util.comparePos(comment.loc.end,child.loc.end)<=0){// The comment is completely contained by this child node.
  decorateComment(comment.enclosingNode=child,comment,lines);return;// Abandon the binary search at this level.
  }if(util.comparePos(child.loc.end,comment.loc.start)<=0){// This child node falls completely before the comment.
  // Because we will never consider this node or any nodes
  // before it again, this node must be the closest preceding
  // node we have encountered so far.
  var precedingNode=child;left=middle+1;continue;}if(util.comparePos(comment.loc.end,child.loc.start)<=0){// This child node falls completely after the comment.
  // Because we will never consider this node or any nodes after
  // it again, this node must be the closest following node we
  // have encountered so far.
  var followingNode=child;right=middle;continue;}throw new Error("Comment location overlaps with node location");}if(precedingNode){comment.precedingNode=precedingNode;}if(followingNode){comment.followingNode=followingNode;}}function attach(comments,ast,lines){if(!isArray.check(comments)){return;}var tiesToBreak=[];comments.forEach(function(comment){comment.loc.lines=lines;decorateComment(ast,comment,lines);var pn=comment.precedingNode;var en=comment.enclosingNode;var fn=comment.followingNode;if(pn&&fn){var tieCount=tiesToBreak.length;if(tieCount>0){var lastTie=tiesToBreak[tieCount-1];assert_1.default.strictEqual(lastTie.precedingNode===comment.precedingNode,lastTie.followingNode===comment.followingNode);if(lastTie.followingNode!==comment.followingNode){breakTies(tiesToBreak,lines);}}tiesToBreak.push(comment);}else if(pn){// No contest: we have a trailing comment.
  breakTies(tiesToBreak,lines);addTrailingComment(pn,comment);}else if(fn){// No contest: we have a leading comment.
  breakTies(tiesToBreak,lines);addLeadingComment(fn,comment);}else if(en){// The enclosing node has no child nodes at all, so what we
  // have here is a dangling comment, e.g. [/* crickets */].
  breakTies(tiesToBreak,lines);addDanglingComment(en,comment);}else{throw new Error("AST contains no nodes at all?");}});breakTies(tiesToBreak,lines);comments.forEach(function(comment){// These node references were useful for breaking ties, but we
  // don't need them anymore, and they create cycles in the AST that
  // may lead to infinite recursion if we don't delete them here.
  delete comment.precedingNode;delete comment.enclosingNode;delete comment.followingNode;});}exports.attach=attach;function breakTies(tiesToBreak,lines){var tieCount=tiesToBreak.length;if(tieCount===0){return;}var pn=tiesToBreak[0].precedingNode;var fn=tiesToBreak[0].followingNode;var gapEndPos=fn.loc.start;// Iterate backwards through tiesToBreak, examining the gaps
  // between the tied comments. In order to qualify as leading, a
  // comment must be separated from fn by an unbroken series of
  // whitespace-only gaps (or other comments).
  for(var indexOfFirstLeadingComment=tieCount;indexOfFirstLeadingComment>0;--indexOfFirstLeadingComment){var comment=tiesToBreak[indexOfFirstLeadingComment-1];assert_1.default.strictEqual(comment.precedingNode,pn);assert_1.default.strictEqual(comment.followingNode,fn);var gap=lines.sliceString(comment.loc.end,gapEndPos);if(/\S/.test(gap)){// The gap string contained something other than whitespace.
  break;}gapEndPos=comment.loc.start;}while(indexOfFirstLeadingComment<=tieCount&&(comment=tiesToBreak[indexOfFirstLeadingComment])&&(// If the comment is a //-style comment and indented more
  // deeply than the node itself, reconsider it as trailing.
  comment.type==="Line"||comment.type==="CommentLine")&&comment.loc.start.column>fn.loc.start.column){++indexOfFirstLeadingComment;}tiesToBreak.forEach(function(comment,i){if(i<indexOfFirstLeadingComment){addTrailingComment(pn,comment);}else{addLeadingComment(fn,comment);}});tiesToBreak.length=0;}function addCommentHelper(node,comment){var comments=node.comments||(node.comments=[]);comments.push(comment);}function addLeadingComment(node,comment){comment.leading=true;comment.trailing=false;addCommentHelper(node,comment);}function addDanglingComment(node,comment){comment.leading=false;comment.trailing=false;addCommentHelper(node,comment);}function addTrailingComment(node,comment){comment.leading=false;comment.trailing=true;addCommentHelper(node,comment);}function printLeadingComment(commentPath,print){var comment=commentPath.getValue();n.Comment.assert(comment);var loc=comment.loc;var lines$1=loc&&loc.lines;var parts=[print(commentPath)];if(comment.trailing){// When we print trailing comments as leading comments, we don't
  // want to bring any trailing spaces along.
  parts.push("\n");}else if(lines$1 instanceof lines.Lines){var trailingSpace=lines$1.slice(loc.end,lines$1.skipSpaces(loc.end)||lines$1.lastPos());if(trailingSpace.length===1){// If the trailing space contains no newlines, then we want to
  // preserve it exactly as we found it.
  parts.push(trailingSpace);}else{// If the trailing space contains newlines, then replace it
  // with just that many newlines, with all other spaces removed.
  parts.push(new Array(trailingSpace.length).join("\n"));}}else{parts.push("\n");}return lines.concat(parts);}function printTrailingComment(commentPath,print){var comment=commentPath.getValue(commentPath);n.Comment.assert(comment);var loc=comment.loc;var lines$1=loc&&loc.lines;var parts=[];if(lines$1 instanceof lines.Lines){var fromPos=lines$1.skipSpaces(loc.start,true)||lines$1.firstPos();var leadingSpace=lines$1.slice(fromPos,loc.start);if(leadingSpace.length===1){// If the leading space contains no newlines, then we want to
  // preserve it exactly as we found it.
  parts.push(leadingSpace);}else{// If the leading space contains newlines, then replace it
  // with just that many newlines, sans all other spaces.
  parts.push(new Array(leadingSpace.length).join("\n"));}}parts.push(print(commentPath));return lines.concat(parts);}function printComments(path,print){var value=path.getValue();var innerLines=print(path);var comments=n.Node.check(value)&&types.getFieldValue(value,"comments");if(!comments||comments.length===0){return innerLines;}var leadingParts=[];var trailingParts=[innerLines];path.each(function(commentPath){var comment=commentPath.getValue();var leading=types.getFieldValue(comment,"leading");var trailing=types.getFieldValue(comment,"trailing");if(leading||trailing&&!(n.Statement.check(value)||comment.type==="Block"||comment.type==="CommentBlock")){leadingParts.push(printLeadingComment(commentPath,print));}else if(trailing){trailingParts.push(printTrailingComment(commentPath,print));}},"comments");leadingParts.push.apply(leadingParts,trailingParts);return lines.concat(leadingParts);}exports.printComments=printComments;});unwrapExports(comments);var comments_1=comments.attach;var comments_2=comments.printComments;var parser=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};var __importStar=this&&this.__importStar||function(mod){if(mod&&mod.__esModule)return mod;var result={};if(mod!=null)for(var k in mod)if(Object.hasOwnProperty.call(mod,k))result[k]=mod[k];result["default"]=mod;return result;};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var types=__importStar(main);var b=types.builders;var isObject=types.builtInTypes.object;var isArray=types.builtInTypes.array;var util$1=__importStar(util);function parse(source,options$1){options$1=options.normalize(options$1);var lines$1=lines.fromString(source,options$1);var sourceWithoutTabs=lines$1.toString({tabWidth:options$1.tabWidth,reuseWhitespace:false,useTabs:false});var comments$1=[];var ast=options$1.parser.parse(sourceWithoutTabs,{jsx:true,loc:true,locations:true,range:options$1.range,comment:true,onComment:comments$1,tolerant:util$1.getOption(options$1,"tolerant",true),ecmaVersion:6,sourceType:util$1.getOption(options$1,"sourceType","module")});// Use ast.tokens if possible, and otherwise fall back to the Esprima
  // tokenizer. All the preconfigured ../parsers/* expose ast.tokens
  // automatically, but custom parsers might need additional configuration
  // to avoid this fallback.
  var tokens=Array.isArray(ast.tokens)?ast.tokens:esprima$1.tokenize(sourceWithoutTabs,{loc:true});// We will reattach the tokens array to the file object below.
  delete ast.tokens;// Make sure every token has a token.value string.
  tokens.forEach(function(token){if(typeof token.value!=="string"){token.value=lines$1.sliceString(token.loc.start,token.loc.end);}});if(Array.isArray(ast.comments)){comments$1=ast.comments;delete ast.comments;}if(ast.loc){// If the source was empty, some parsers give loc.{start,end}.line
  // values of 0, instead of the minimum of 1.
  util$1.fixFaultyLocations(ast,lines$1);}else{ast.loc={start:lines$1.firstPos(),end:lines$1.lastPos()};}ast.loc.lines=lines$1;ast.loc.indent=0;var file;var program;if(ast.type==="Program"){program=ast;// In order to ensure we reprint leading and trailing program
  // comments, wrap the original Program node with a File node. Only
  // ESTree parsers (Acorn and Esprima) return a Program as the root AST
  // node. Most other (Babylon-like) parsers return a File.
  file=b.file(ast,options$1.sourceFileName||null);file.loc={start:lines$1.firstPos(),end:lines$1.lastPos(),lines:lines$1,indent:0};}else if(ast.type==="File"){file=ast;program=file.program;}// Expose file.tokens unless the caller passed false for options.tokens.
  if(options$1.tokens){file.tokens=tokens;}// Expand the Program's .loc to include all comments (not just those
  // attached to the Program node, as its children may have comments as
  // well), since sometimes program.loc.{start,end} will coincide with the
  // .loc.{start,end} of the first and last *statements*, mistakenly
  // excluding comments that fall outside that region.
  var trueProgramLoc=util$1.getTrueLoc({type:program.type,loc:program.loc,body:[],comments:comments$1},lines$1);program.loc.start=trueProgramLoc.start;program.loc.end=trueProgramLoc.end;// Passing file.program here instead of just file means that initial
  // comments will be attached to program.body[0] instead of program.
  comments.attach(comments$1,program.body.length?file.program:file,lines$1);// Return a copy of the original AST so that any changes made may be
  // compared to the original.
  return new TreeCopier(lines$1,tokens).copy(file);}exports.parse=parse;var TreeCopier=function TreeCopier(lines,tokens){assert_1.default.ok(this instanceof TreeCopier);this.lines=lines;this.tokens=tokens;this.startTokenIndex=0;this.endTokenIndex=tokens.length;this.indent=0;this.seen=new Map();};var TCp=TreeCopier.prototype;TCp.copy=function(node){if(this.seen.has(node)){return this.seen.get(node);}if(isArray.check(node)){var copy=new Array(node.length);this.seen.set(node,copy);node.forEach(function(item,i){copy[i]=this.copy(item);},this);return copy;}if(!isObject.check(node)){return node;}util$1.fixFaultyLocations(node,this.lines);var copy=Object.create(Object.getPrototypeOf(node),{original:{value:node,configurable:false,enumerable:false,writable:true}});this.seen.set(node,copy);var loc=node.loc;var oldIndent=this.indent;var newIndent=oldIndent;var oldStartTokenIndex=this.startTokenIndex;var oldEndTokenIndex=this.endTokenIndex;if(loc){// When node is a comment, we set node.loc.indent to
  // node.loc.start.column so that, when/if we print the comment by
  // itself, we can strip that much whitespace from the left margin of
  // the comment. This only really matters for multiline Block comments,
  // but it doesn't hurt for Line comments.
  if(node.type==="Block"||node.type==="Line"||node.type==="CommentBlock"||node.type==="CommentLine"||this.lines.isPrecededOnlyByWhitespace(loc.start)){newIndent=this.indent=loc.start.column;}// Every node.loc has a reference to the original source lines as well
  // as a complete list of source tokens.
  loc.lines=this.lines;loc.tokens=this.tokens;loc.indent=newIndent;// Set loc.start.token and loc.end.token such that
  // loc.tokens.slice(loc.start.token, loc.end.token) returns a list of
  // all the tokens that make up this node.
  this.findTokenRange(loc);}var keys=Object.keys(node);var keyCount=keys.length;for(var i=0;i<keyCount;++i){var key=keys[i];if(key==="loc"){copy[key]=node[key];}else if(key==="tokens"&&node.type==="File"){// Preserve file.tokens (uncopied) in case client code cares about
  // it, even though Recast ignores it when reprinting.
  copy[key]=node[key];}else{copy[key]=this.copy(node[key]);}}this.indent=oldIndent;this.startTokenIndex=oldStartTokenIndex;this.endTokenIndex=oldEndTokenIndex;return copy;};// If we didn't have any idea where in loc.tokens to look for tokens
  // contained by this loc, a binary search would be appropriate, but
  // because we maintain this.startTokenIndex and this.endTokenIndex as we
  // traverse the AST, we only need to make small (linear) adjustments to
  // those indexes with each recursive iteration.
  TCp.findTokenRange=function(loc){// In the unlikely event that loc.tokens[this.startTokenIndex] starts
  // *after* loc.start, we need to rewind this.startTokenIndex first.
  while(this.startTokenIndex>0){var token=loc.tokens[this.startTokenIndex];if(util$1.comparePos(loc.start,token.loc.start)<0){--this.startTokenIndex;}else break;}// In the unlikely event that loc.tokens[this.endTokenIndex - 1] ends
  // *before* loc.end, we need to fast-forward this.endTokenIndex first.
  while(this.endTokenIndex<loc.tokens.length){var token=loc.tokens[this.endTokenIndex];if(util$1.comparePos(token.loc.end,loc.end)<0){++this.endTokenIndex;}else break;}// Increment this.startTokenIndex until we've found the first token
  // contained by this node.
  while(this.startTokenIndex<this.endTokenIndex){var token=loc.tokens[this.startTokenIndex];if(util$1.comparePos(token.loc.start,loc.start)<0){++this.startTokenIndex;}else break;}// Index into loc.tokens of the first token within this node.
  loc.start.token=this.startTokenIndex;// Decrement this.endTokenIndex until we've found the first token after
  // this node (not contained by the node).
  while(this.endTokenIndex>this.startTokenIndex){var token=loc.tokens[this.endTokenIndex-1];if(util$1.comparePos(loc.end,token.loc.end)<0){--this.endTokenIndex;}else break;}// Index into loc.tokens of the first token *after* this node.
  // If loc.start.token === loc.end.token, the node contains no tokens,
  // and the index is that of the next token following this node.
  loc.end.token=this.endTokenIndex;};});unwrapExports(parser);var parser_1=parser.parse;var fastPath=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};var __importStar=this&&this.__importStar||function(mod){if(mod&&mod.__esModule)return mod;var result={};if(mod!=null)for(var k in mod)if(Object.hasOwnProperty.call(mod,k))result[k]=mod[k];result["default"]=mod;return result;};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var types=__importStar(main);var n=types.namedTypes;var isArray=types.builtInTypes.array;var isNumber=types.builtInTypes.number;var util$1=__importStar(util);var FastPath=function FastPath(value){assert_1.default.ok(this instanceof FastPath);this.stack=[value];};var FPp=FastPath.prototype;// Static convenience function for coercing a value to a FastPath.
  FastPath.from=function(obj){if(obj instanceof FastPath){// Return a defensive copy of any existing FastPath instances.
  return obj.copy();}if(obj instanceof types.NodePath){// For backwards compatibility, unroll NodePath instances into
  // lightweight FastPath [..., name, value] stacks.
  var copy=Object.create(FastPath.prototype);var stack=[obj.value];for(var pp;pp=obj.parentPath;obj=pp)stack.push(obj.name,pp.value);copy.stack=stack.reverse();return copy;}// Otherwise use obj as the value of the new FastPath instance.
  return new FastPath(obj);};FPp.copy=function copy(){var copy=Object.create(FastPath.prototype);copy.stack=this.stack.slice(0);return copy;};// The name of the current property is always the penultimate element of
  // this.stack, and always a String.
  FPp.getName=function getName(){var s=this.stack;var len=s.length;if(len>1){return s[len-2];}// Since the name is always a string, null is a safe sentinel value to
  // return if we do not know the name of the (root) value.
  return null;};// The value of the current property is always the final element of
  // this.stack.
  FPp.getValue=function getValue(){var s=this.stack;return s[s.length-1];};FPp.valueIsDuplicate=function(){var s=this.stack;var valueIndex=s.length-1;return s.lastIndexOf(s[valueIndex],valueIndex-1)>=0;};function getNodeHelper(path,count){var s=path.stack;for(var i=s.length-1;i>=0;i-=2){var value=s[i];if(n.Node.check(value)&&--count<0){return value;}}return null;}FPp.getNode=function getNode(count){if(count===void 0){count=0;}return getNodeHelper(this,~~count);};FPp.getParentNode=function getParentNode(count){if(count===void 0){count=0;}return getNodeHelper(this,~~count+1);};// The length of the stack can be either even or odd, depending on whether
  // or not we have a name for the root value. The difference between the
  // index of the root value and the index of the final value is always
  // even, though, which allows us to return the root value in constant time
  // (i.e. without iterating backwards through the stack).
  FPp.getRootValue=function getRootValue(){var s=this.stack;if(s.length%2===0){return s[1];}return s[0];};// Temporarily push properties named by string arguments given after the
  // callback function onto this.stack, then call the callback with a
  // reference to this (modified) FastPath object. Note that the stack will
  // be restored to its original state after the callback is finished, so it
  // is probably a mistake to retain a reference to the path.
  FPp.call=function call(callback/*, name1, name2, ... */){var s=this.stack;var origLen=s.length;var value=s[origLen-1];var argc=arguments.length;for(var i=1;i<argc;++i){var name=arguments[i];value=value[name];s.push(name,value);}var result=callback(this);s.length=origLen;return result;};// Similar to FastPath.prototype.call, except that the value obtained by
  // accessing this.getValue()[name1][name2]... should be array-like. The
  // callback will be called with a reference to this path object for each
  // element of the array.
  FPp.each=function each(callback/*, name1, name2, ... */){var s=this.stack;var origLen=s.length;var value=s[origLen-1];var argc=arguments.length;for(var i=1;i<argc;++i){var name=arguments[i];value=value[name];s.push(name,value);}for(var i=0;i<value.length;++i){if(i in value){s.push(i,value[i]);// If the callback needs to know the value of i, call
  // path.getName(), assuming path is the parameter name.
  callback(this);s.length-=2;}}s.length=origLen;};// Similar to FastPath.prototype.each, except that the results of the
  // callback function invocations are stored in an array and returned at
  // the end of the iteration.
  FPp.map=function map(callback/*, name1, name2, ... */){var s=this.stack;var origLen=s.length;var value=s[origLen-1];var argc=arguments.length;for(var i=1;i<argc;++i){var name=arguments[i];value=value[name];s.push(name,value);}var result=new Array(value.length);for(var i=0;i<value.length;++i){if(i in value){s.push(i,value[i]);result[i]=callback(this,i);s.length-=2;}}s.length=origLen;return result;};// Returns true if the node at the tip of the path is wrapped with
  // parentheses, OR if the only reason the node needed parentheses was that
  // it couldn't be the first expression in the enclosing statement (see
  // FastPath#canBeFirstInStatement), and it has an opening `(` character.
  // For example, the FunctionExpression in `(function(){}())` appears to
  // need parentheses only because it's the first expression in the AST, but
  // since it happens to be preceded by a `(` (which is not apparent from
  // the AST but can be determined using FastPath#getPrevToken), there is no
  // ambiguity about how to parse it, so it counts as having parentheses,
  // even though it is not immediately followed by a `)`.
  FPp.hasParens=function(){var node=this.getNode();var prevToken=this.getPrevToken(node);if(!prevToken){return false;}var nextToken=this.getNextToken(node);if(!nextToken){return false;}if(prevToken.value==="("){if(nextToken.value===")"){// If the node preceded by a `(` token and followed by a `)` token,
  // then of course it has parentheses.
  return true;}// If this is one of the few Expression types that can't come first in
  // the enclosing statement because of parsing ambiguities (namely,
  // FunctionExpression, ObjectExpression, and ClassExpression) and
  // this.firstInStatement() returns true, and the node would not need
  // parentheses in an expression context because this.needsParens(true)
  // returns false, then it just needs an opening parenthesis to resolve
  // the parsing ambiguity that made it appear to need parentheses.
  var justNeedsOpeningParen=!this.canBeFirstInStatement()&&this.firstInStatement()&&!this.needsParens(true);if(justNeedsOpeningParen){return true;}}return false;};FPp.getPrevToken=function(node){node=node||this.getNode();var loc=node&&node.loc;var tokens=loc&&loc.tokens;if(tokens&&loc.start.token>0){var token=tokens[loc.start.token-1];if(token){// Do not return tokens that fall outside the root subtree.
  var rootLoc=this.getRootValue().loc;if(util$1.comparePos(rootLoc.start,token.loc.start)<=0){return token;}}}return null;};FPp.getNextToken=function(node){node=node||this.getNode();var loc=node&&node.loc;var tokens=loc&&loc.tokens;if(tokens&&loc.end.token<tokens.length){var token=tokens[loc.end.token];if(token){// Do not return tokens that fall outside the root subtree.
  var rootLoc=this.getRootValue().loc;if(util$1.comparePos(token.loc.end,rootLoc.end)<=0){return token;}}}return null;};// Inspired by require("ast-types").NodePath.prototype.needsParens, but
  // more efficient because we're iterating backwards through a stack.
  FPp.needsParens=function(assumeExpressionContext){var node=this.getNode();// This needs to come before `if (!parent) { return false }` because
  // an object destructuring assignment requires parens for
  // correctness even when it's the topmost expression.
  if(node.type==="AssignmentExpression"&&node.left.type==='ObjectPattern'){return true;}var parent=this.getParentNode();if(!parent){return false;}var name=this.getName();// If the value of this path is some child of a Node and not a Node
  // itself, then it doesn't need parentheses. Only Node objects (in fact,
  // only Expression nodes) need parentheses.
  if(this.getValue()!==node){return false;}// Only statements don't need parentheses.
  if(n.Statement.check(node)){return false;}// Identifiers never need parentheses.
  if(node.type==="Identifier"){return false;}if(parent.type==="ParenthesizedExpression"){return false;}switch(node.type){case"UnaryExpression":case"SpreadElement":case"SpreadProperty":return parent.type==="MemberExpression"&&name==="object"&&parent.object===node;case"BinaryExpression":case"LogicalExpression":switch(parent.type){case"CallExpression":return name==="callee"&&parent.callee===node;case"UnaryExpression":case"SpreadElement":case"SpreadProperty":return true;case"MemberExpression":return name==="object"&&parent.object===node;case"BinaryExpression":case"LogicalExpression":var po=parent.operator;var pp=PRECEDENCE[po];var no=node.operator;var np=PRECEDENCE[no];if(pp>np){return true;}if(pp===np&&name==="right"){assert_1.default.strictEqual(parent.right,node);return true;}default:return false;}case"SequenceExpression":switch(parent.type){case"ReturnStatement":return false;case"ForStatement":// Although parentheses wouldn't hurt around sequence expressions in
  // the head of for loops, traditional style dictates that e.g. i++,
  // j++ should not be wrapped with parentheses.
  return false;case"ExpressionStatement":return name!=="expression";default:// Otherwise err on the side of overparenthesization, adding
  // explicit exceptions above if this proves overzealous.
  return true;}case"YieldExpression":switch(parent.type){case"BinaryExpression":case"LogicalExpression":case"UnaryExpression":case"SpreadElement":case"SpreadProperty":case"CallExpression":case"MemberExpression":case"NewExpression":case"ConditionalExpression":case"YieldExpression":return true;default:return false;}case"IntersectionTypeAnnotation":case"UnionTypeAnnotation":return parent.type==="NullableTypeAnnotation";case"Literal":return parent.type==="MemberExpression"&&isNumber.check(node.value)&&name==="object"&&parent.object===node;// Babel 6 Literal split
  case"NumericLiteral":return parent.type==="MemberExpression"&&name==="object"&&parent.object===node;case"AssignmentExpression":case"ConditionalExpression":switch(parent.type){case"UnaryExpression":case"SpreadElement":case"SpreadProperty":case"BinaryExpression":case"LogicalExpression":return true;case"CallExpression":case"NewExpression":return name==="callee"&&parent.callee===node;case"ConditionalExpression":return name==="test"&&parent.test===node;case"MemberExpression":return name==="object"&&parent.object===node;default:return false;}case"ArrowFunctionExpression":if(n.CallExpression.check(parent)&&name==='callee'){return true;}if(n.MemberExpression.check(parent)&&name==='object'){return true;}return isBinary(parent);case"ObjectExpression":if(parent.type==="ArrowFunctionExpression"&&name==="body"){return true;}break;case"CallExpression":if(name==="declaration"&&n.ExportDefaultDeclaration.check(parent)&&n.FunctionExpression.check(node.callee)){return true;}}if(parent.type==="NewExpression"&&name==="callee"&&parent.callee===node){return containsCallExpression(node);}if(assumeExpressionContext!==true&&!this.canBeFirstInStatement()&&this.firstInStatement()){return true;}return false;};function isBinary(node){return n.BinaryExpression.check(node)||n.LogicalExpression.check(node);}var PRECEDENCE={};[["||"],["&&"],["|"],["^"],["&"],["==","===","!=","!=="],["<",">","<=",">=","in","instanceof"],[">>","<<",">>>"],["+","-"],["*","/","%","**"]].forEach(function(tier,i){tier.forEach(function(op){PRECEDENCE[op]=i;});});function containsCallExpression(node){if(n.CallExpression.check(node)){return true;}if(isArray.check(node)){return node.some(containsCallExpression);}if(n.Node.check(node)){return types.someField(node,function(_name,child){return containsCallExpression(child);});}return false;}FPp.canBeFirstInStatement=function(){var node=this.getNode();if(n.FunctionExpression.check(node)){return false;}if(n.ObjectExpression.check(node)){return false;}if(n.ClassExpression.check(node)){return false;}return true;};FPp.firstInStatement=function(){var s=this.stack;var parentName,parent;var childName,child;for(var i=s.length-1;i>=0;i-=2){if(n.Node.check(s[i])){childName=parentName;child=parent;parentName=s[i-1];parent=s[i];}if(!parent||!child){continue;}if(n.BlockStatement.check(parent)&&parentName==="body"&&childName===0){assert_1.default.strictEqual(parent.body[0],child);return true;}if(n.ExpressionStatement.check(parent)&&childName==="expression"){assert_1.default.strictEqual(parent.expression,child);return true;}if(n.AssignmentExpression.check(parent)&&childName==="left"){assert_1.default.strictEqual(parent.left,child);return true;}if(n.ArrowFunctionExpression.check(parent)&&childName==="body"){assert_1.default.strictEqual(parent.body,child);return true;}if(n.SequenceExpression.check(parent)&&parentName==="expressions"&&childName===0){assert_1.default.strictEqual(parent.expressions[0],child);continue;}if(n.CallExpression.check(parent)&&childName==="callee"){assert_1.default.strictEqual(parent.callee,child);continue;}if(n.MemberExpression.check(parent)&&childName==="object"){assert_1.default.strictEqual(parent.object,child);continue;}if(n.ConditionalExpression.check(parent)&&childName==="test"){assert_1.default.strictEqual(parent.test,child);continue;}if(isBinary(parent)&&childName==="left"){assert_1.default.strictEqual(parent.left,child);continue;}if(n.UnaryExpression.check(parent)&&!parent.prefix&&childName==="argument"){assert_1.default.strictEqual(parent.argument,child);continue;}return false;}return true;};exports.default=FastPath;});unwrapExports(fastPath);var patcher=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};var __importStar=this&&this.__importStar||function(mod){if(mod&&mod.__esModule)return mod;var result={};if(mod!=null)for(var k in mod)if(Object.hasOwnProperty.call(mod,k))result[k]=mod[k];result["default"]=mod;return result;};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var linesModule=__importStar(lines);var types=__importStar(main);var Printable=types.namedTypes.Printable;var Expression=types.namedTypes.Expression;var ReturnStatement=types.namedTypes.ReturnStatement;var SourceLocation=types.namedTypes.SourceLocation;var fast_path_1=__importDefault(fastPath);var isObject=types.builtInTypes.object;var isArray=types.builtInTypes.array;var isString=types.builtInTypes.string;var riskyAdjoiningCharExp=/[0-9a-z_$]/i;var Patcher=function Patcher(lines){assert_1.default.ok(this instanceof Patcher);assert_1.default.ok(lines instanceof linesModule.Lines);var self=this,replacements=[];self.replace=function(loc,lines){if(isString.check(lines))lines=linesModule.fromString(lines);replacements.push({lines:lines,start:loc.start,end:loc.end});};self.get=function(loc){// If no location is provided, return the complete Lines object.
  loc=loc||{start:{line:1,column:0},end:{line:lines.length,column:lines.getLineLength(lines.length)}};var sliceFrom=loc.start,toConcat=[];function pushSlice(from,to){assert_1.default.ok(util.comparePos(from,to)<=0);toConcat.push(lines.slice(from,to));}replacements.sort(function(a,b){return util.comparePos(a.start,b.start);}).forEach(function(rep){if(util.comparePos(sliceFrom,rep.start)>0);else{pushSlice(sliceFrom,rep.start);toConcat.push(rep.lines);sliceFrom=rep.end;}});pushSlice(sliceFrom,loc.end);return linesModule.concat(toConcat);};};exports.Patcher=Patcher;var Pp=Patcher.prototype;Pp.tryToReprintComments=function(newNode,oldNode,print){var patcher=this;if(!newNode.comments&&!oldNode.comments){// We were (vacuously) able to reprint all the comments!
  return true;}var newPath=fast_path_1.default.from(newNode);var oldPath=fast_path_1.default.from(oldNode);newPath.stack.push("comments",getSurroundingComments(newNode));oldPath.stack.push("comments",getSurroundingComments(oldNode));var reprints=[];var ableToReprintComments=findArrayReprints(newPath,oldPath,reprints);// No need to pop anything from newPath.stack or oldPath.stack, since
  // newPath and oldPath are fresh local variables.
  if(ableToReprintComments&&reprints.length>0){reprints.forEach(function(reprint){var oldComment=reprint.oldPath.getValue();assert_1.default.ok(oldComment.leading||oldComment.trailing);patcher.replace(oldComment.loc,// Comments can't have .comments, so it doesn't matter whether we
  // print with comments or without.
  print(reprint.newPath).indentTail(oldComment.loc.indent));});}return ableToReprintComments;};// Get all comments that are either leading or trailing, ignoring any
  // comments that occur inside node.loc. Returns an empty array for nodes
  // with no leading or trailing comments.
  function getSurroundingComments(node){var result=[];if(node.comments&&node.comments.length>0){node.comments.forEach(function(comment){if(comment.leading||comment.trailing){result.push(comment);}});}return result;}Pp.deleteComments=function(node){if(!node.comments){return;}var patcher=this;node.comments.forEach(function(comment){if(comment.leading){// Delete leading comments along with any trailing whitespace they
  // might have.
  patcher.replace({start:comment.loc.start,end:node.loc.lines.skipSpaces(comment.loc.end,false,false)},"");}else if(comment.trailing){// Delete trailing comments along with any leading whitespace they
  // might have.
  patcher.replace({start:node.loc.lines.skipSpaces(comment.loc.start,true,false),end:comment.loc.end},"");}});};function getReprinter(path){assert_1.default.ok(path instanceof fast_path_1.default);// Make sure that this path refers specifically to a Node, rather than
  // some non-Node subproperty of a Node.
  var node=path.getValue();if(!Printable.check(node))return;var orig=node.original;var origLoc=orig&&orig.loc;var lines=origLoc&&origLoc.lines;var reprints=[];if(!lines||!findReprints(path,reprints))return;return function(print){var patcher=new Patcher(lines);reprints.forEach(function(reprint){var newNode=reprint.newPath.getValue();var oldNode=reprint.oldPath.getValue();SourceLocation.assert(oldNode.loc,true);var needToPrintNewPathWithComments=!patcher.tryToReprintComments(newNode,oldNode,print);if(needToPrintNewPathWithComments){// Since we were not able to preserve all leading/trailing
  // comments, we delete oldNode's comments, print newPath with
  // comments, and then patch the resulting lines where oldNode used
  // to be.
  patcher.deleteComments(oldNode);}var newLines=print(reprint.newPath,{includeComments:needToPrintNewPathWithComments,// If the oldNode we're replacing already had parentheses, we may
  // not need to print the new node with any extra parentheses,
  // because the existing parentheses will suffice. However, if the
  // newNode has a different type than the oldNode, let the printer
  // decide if reprint.newPath needs parentheses, as usual.
  avoidRootParens:oldNode.type===newNode.type&&reprint.oldPath.hasParens()}).indentTail(oldNode.loc.indent);var nls=needsLeadingSpace(lines,oldNode.loc,newLines);var nts=needsTrailingSpace(lines,oldNode.loc,newLines);// If we try to replace the argument of a ReturnStatement like
  // return"asdf" with e.g. a literal null expression, we run the risk
  // of ending up with returnnull, so we need to add an extra leading
  // space in situations where that might happen. Likewise for
  // "asdf"in obj. See #170.
  if(nls||nts){var newParts=[];nls&&newParts.push(" ");newParts.push(newLines);nts&&newParts.push(" ");newLines=linesModule.concat(newParts);}patcher.replace(oldNode.loc,newLines);});// Recall that origLoc is the .loc of an ancestor node that is
  // guaranteed to contain all the reprinted nodes and comments.
  var patchedLines=patcher.get(origLoc).indentTail(-orig.loc.indent);if(path.needsParens()){return linesModule.concat(["(",patchedLines,")"]);}return patchedLines;};}exports.getReprinter=getReprinter;// If the last character before oldLoc and the first character of newLines
  // are both identifier characters, they must be separated by a space,
  // otherwise they will most likely get fused together into a single token.
  function needsLeadingSpace(oldLines,oldLoc,newLines){var posBeforeOldLoc=util.copyPos(oldLoc.start);// The character just before the location occupied by oldNode.
  var charBeforeOldLoc=oldLines.prevPos(posBeforeOldLoc)&&oldLines.charAt(posBeforeOldLoc);// First character of the reprinted node.
  var newFirstChar=newLines.charAt(newLines.firstPos());return charBeforeOldLoc&&riskyAdjoiningCharExp.test(charBeforeOldLoc)&&newFirstChar&&riskyAdjoiningCharExp.test(newFirstChar);}// If the last character of newLines and the first character after oldLoc
  // are both identifier characters, they must be separated by a space,
  // otherwise they will most likely get fused together into a single token.
  function needsTrailingSpace(oldLines,oldLoc,newLines){// The character just after the location occupied by oldNode.
  var charAfterOldLoc=oldLines.charAt(oldLoc.end);var newLastPos=newLines.lastPos();// Last character of the reprinted node.
  var newLastChar=newLines.prevPos(newLastPos)&&newLines.charAt(newLastPos);return newLastChar&&riskyAdjoiningCharExp.test(newLastChar)&&charAfterOldLoc&&riskyAdjoiningCharExp.test(charAfterOldLoc);}function findReprints(newPath,reprints){var newNode=newPath.getValue();Printable.assert(newNode);var oldNode=newNode.original;Printable.assert(oldNode);assert_1.default.deepEqual(reprints,[]);if(newNode.type!==oldNode.type){return false;}var oldPath=new fast_path_1.default(oldNode);var canReprint=findChildReprints(newPath,oldPath,reprints);if(!canReprint){// Make absolutely sure the calling code does not attempt to reprint
  // any nodes.
  reprints.length=0;}return canReprint;}function findAnyReprints(newPath,oldPath,reprints){var newNode=newPath.getValue();var oldNode=oldPath.getValue();if(newNode===oldNode)return true;if(isArray.check(newNode))return findArrayReprints(newPath,oldPath,reprints);if(isObject.check(newNode))return findObjectReprints(newPath,oldPath,reprints);return false;}function findArrayReprints(newPath,oldPath,reprints){var newNode=newPath.getValue();var oldNode=oldPath.getValue();if(newNode===oldNode||newPath.valueIsDuplicate()||oldPath.valueIsDuplicate()){return true;}isArray.assert(newNode);var len=newNode.length;if(!(isArray.check(oldNode)&&oldNode.length===len))return false;for(var i=0;i<len;++i){newPath.stack.push(i,newNode[i]);oldPath.stack.push(i,oldNode[i]);var canReprint=findAnyReprints(newPath,oldPath,reprints);newPath.stack.length-=2;oldPath.stack.length-=2;if(!canReprint){return false;}}return true;}function findObjectReprints(newPath,oldPath,reprints){var newNode=newPath.getValue();isObject.assert(newNode);if(newNode.original===null){// If newNode.original node was set to null, reprint the node.
  return false;}var oldNode=oldPath.getValue();if(!isObject.check(oldNode))return false;if(newNode===oldNode||newPath.valueIsDuplicate()||oldPath.valueIsDuplicate()){return true;}if(Printable.check(newNode)){if(!Printable.check(oldNode)){return false;}// Here we need to decide whether the reprinted code for newNode is
  // appropriate for patching into the location of oldNode.
  if(newNode.type===oldNode.type){var childReprints=[];if(findChildReprints(newPath,oldPath,childReprints)){reprints.push.apply(reprints,childReprints);}else if(oldNode.loc){// If we have no .loc information for oldNode, then we won't be
  // able to reprint it.
  reprints.push({oldPath:oldPath.copy(),newPath:newPath.copy()});}else{return false;}return true;}if(Expression.check(newNode)&&Expression.check(oldNode)&&// If we have no .loc information for oldNode, then we won't be
  // able to reprint it.
  oldNode.loc){// If both nodes are subtypes of Expression, then we should be able
  // to fill the location occupied by the old node with code printed
  // for the new node with no ill consequences.
  reprints.push({oldPath:oldPath.copy(),newPath:newPath.copy()});return true;}// The nodes have different types, and at least one of the types is
  // not a subtype of the Expression type, so we cannot safely assume
  // the nodes are syntactically interchangeable.
  return false;}return findChildReprints(newPath,oldPath,reprints);}function findChildReprints(newPath,oldPath,reprints){var newNode=newPath.getValue();var oldNode=oldPath.getValue();isObject.assert(newNode);isObject.assert(oldNode);if(newNode.original===null){// If newNode.original node was set to null, reprint the node.
  return false;}// If this node needs parentheses and will not be wrapped with
  // parentheses when reprinted, then return false to skip reprinting and
  // let it be printed generically.
  if(newPath.needsParens()&&!oldPath.hasParens()){return false;}var keys=util.getUnionOfKeys(oldNode,newNode);if(oldNode.type==="File"||newNode.type==="File"){// Don't bother traversing file.tokens, an often very large array
  // returned by Babylon, and useless for our purposes.
  delete keys.tokens;}// Don't bother traversing .loc objects looking for reprintable nodes.
  delete keys.loc;var originalReprintCount=reprints.length;for(var k in keys){if(k.charAt(0)==="_"){// Ignore "private" AST properties added by e.g. Babel plugins and
  // parsers like Babylon.
  continue;}newPath.stack.push(k,types.getFieldValue(newNode,k));oldPath.stack.push(k,types.getFieldValue(oldNode,k));var canReprint=findAnyReprints(newPath,oldPath,reprints);newPath.stack.length-=2;oldPath.stack.length-=2;if(!canReprint){return false;}}// Return statements might end up running into ASI issues due to
  // comments inserted deep within the tree, so reprint them if anything
  // changed within them.
  if(ReturnStatement.check(newPath.getNode())&&reprints.length>originalReprintCount){return false;}return true;}});unwrapExports(patcher);var patcher_1=patcher.Patcher;var patcher_2=patcher.getReprinter;var printer=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};var __importStar=this&&this.__importStar||function(mod){if(mod&&mod.__esModule)return mod;var result={};if(mod!=null)for(var k in mod)if(Object.hasOwnProperty.call(mod,k))result[k]=mod[k];result["default"]=mod;return result;};Object.defineProperty(exports,"__esModule",{value:true});var assert_1=__importDefault(assert);var types=__importStar(main);var namedTypes=types.namedTypes;var isString=types.builtInTypes.string;var isObject=types.builtInTypes.object;var fast_path_1=__importDefault(fastPath);var util$1=__importStar(util);var PrintResult=function PrintResult(code,sourceMap){assert_1.default.ok(this instanceof PrintResult);isString.assert(code);this.code=code;if(sourceMap){isObject.assert(sourceMap);this.map=sourceMap;}};var PRp=PrintResult.prototype;var warnedAboutToString=false;PRp.toString=function(){if(!warnedAboutToString){console.warn("Deprecation warning: recast.print now returns an object with "+"a .code property. You appear to be treating the object as a "+"string, which might still work but is strongly discouraged.");warnedAboutToString=true;}return this.code;};var emptyPrintResult=new PrintResult("");var Printer=function Printer(config){assert_1.default.ok(this instanceof Printer);var explicitTabWidth=config&&config.tabWidth;config=options.normalize(config);// It's common for client code to pass the same options into both
  // recast.parse and recast.print, but the Printer doesn't need (and
  // can be confused by) config.sourceFileName, so we null it out.
  config.sourceFileName=null;// Non-destructively modifies options with overrides, and returns a
  // new print function that uses the modified options.
  function makePrintFunctionWith(options,overrides){options=Object.assign({},options,overrides);return function(path){return print(path,options);};}function print(path,options){assert_1.default.ok(path instanceof fast_path_1.default);options=options||{};if(options.includeComments){return comments.printComments(path,makePrintFunctionWith(options,{includeComments:false}));}var oldTabWidth=config.tabWidth;if(!explicitTabWidth){var loc=path.getNode().loc;if(loc&&loc.lines&&loc.lines.guessTabWidth){config.tabWidth=loc.lines.guessTabWidth();}}var reprinter=patcher.getReprinter(path);var lines=reprinter// Since the print function that we pass to the reprinter will
  // be used to print "new" nodes, it's tempting to think we
  // should pass printRootGenerically instead of print, to avoid
  // calling maybeReprint again, but that would be a mistake
  // because the new nodes might not be entirely new, but merely
  // moved from elsewhere in the AST. The print function is the
  // right choice because it gives us the opportunity to reprint
  // such nodes using their original source.
  ?reprinter(print):genericPrint(path,config,options,makePrintFunctionWith(options,{includeComments:true,avoidRootParens:false}));config.tabWidth=oldTabWidth;return lines;}this.print=function(ast){if(!ast){return emptyPrintResult;}var lines=print(fast_path_1.default.from(ast),{includeComments:true,avoidRootParens:false});return new PrintResult(lines.toString(config),util$1.composeSourceMaps(config.inputSourceMap,lines.getSourceMap(config.sourceMapName,config.sourceRoot)));};this.printGenerically=function(ast){if(!ast){return emptyPrintResult;}// Print the entire AST generically.
  function printGenerically(path){return comments.printComments(path,function(path){return genericPrint(path,config,{includeComments:true,avoidRootParens:false},printGenerically);});}var path=fast_path_1.default.from(ast);var oldReuseWhitespace=config.reuseWhitespace;// Do not reuse whitespace (or anything else, for that matter)
  // when printing generically.
  config.reuseWhitespace=false;// TODO Allow printing of comments?
  var pr=new PrintResult(printGenerically(path).toString(config));config.reuseWhitespace=oldReuseWhitespace;return pr;};};exports.Printer=Printer;function genericPrint(path,config,options,printPath){assert_1.default.ok(path instanceof fast_path_1.default);var node=path.getValue();var parts=[];var linesWithoutParens=genericPrintNoParens(path,config,printPath);if(!node||linesWithoutParens.isEmpty()){return linesWithoutParens;}var shouldAddParens=false;var decoratorsLines=printDecorators(path,printPath);if(decoratorsLines.isEmpty()){// Nodes with decorators can't have parentheses, so we can avoid
  // computing path.needsParens() except in this case.
  if(!options.avoidRootParens){shouldAddParens=path.needsParens();}}else{parts.push(decoratorsLines);}if(shouldAddParens){parts.unshift("(");}parts.push(linesWithoutParens);if(shouldAddParens){parts.push(")");}return lines.concat(parts);}// Note that the `options` parameter of this function is what other
  // functions in this file call the `config` object (that is, the
  // configuration object originally passed into the Printer constructor).
  // Its properties are documented in lib/options.js.
  function genericPrintNoParens(path,options,print){var n=path.getValue();if(!n){return lines.fromString("");}if(typeof n==="string"){return lines.fromString(n,options);}namedTypes.Printable.assert(n);var parts=[];switch(n.type){case"File":return path.call(print,"program");case"Program":// Babel 6
  if(n.directives){path.each(function(childPath){parts.push(print(childPath),";\n");},"directives");}if(n.interpreter){parts.push(path.call(print,"interpreter"));}parts.push(path.call(function(bodyPath){return printStatementSequence(bodyPath,options,print);},"body"));return lines.concat(parts);case"Noop":// Babel extension.
  case"EmptyStatement":return lines.fromString("");case"ExpressionStatement":return lines.concat([path.call(print,"expression"),";"]);case"ParenthesizedExpression":// Babel extension.
  return lines.concat(["(",path.call(print,"expression"),")"]);case"BinaryExpression":case"LogicalExpression":case"AssignmentExpression":return lines.fromString(" ").join([path.call(print,"left"),n.operator,path.call(print,"right")]);case"AssignmentPattern":return lines.concat([path.call(print,"left")," = ",path.call(print,"right")]);case"MemberExpression":case"OptionalMemberExpression":parts.push(path.call(print,"object"));var property=path.call(print,"property");var optional=n.type==="OptionalMemberExpression";if(n.computed){parts.push(optional?"?.[":"[",property,"]");}else{parts.push(optional?"?.":".",property);}return lines.concat(parts);case"MetaProperty":return lines.concat([path.call(print,"meta"),".",path.call(print,"property")]);case"BindExpression":if(n.object){parts.push(path.call(print,"object"));}parts.push("::",path.call(print,"callee"));return lines.concat(parts);case"Path":return lines.fromString(".").join(n.body);case"Identifier":return lines.concat([lines.fromString(n.name,options),n.optional?"?":"",path.call(print,"typeAnnotation")]);case"SpreadElement":case"SpreadElementPattern":case"RestProperty":// Babel 6 for ObjectPattern
  case"SpreadProperty":case"SpreadPropertyPattern":case"ObjectTypeSpreadProperty":case"RestElement":return lines.concat(["...",path.call(print,"argument"),path.call(print,"typeAnnotation")]);case"FunctionDeclaration":case"FunctionExpression":case"TSDeclareFunction":if(n.declare){parts.push("declare ");}if(n.async){parts.push("async ");}parts.push("function");if(n.generator)parts.push("*");if(n.id){parts.push(" ",path.call(print,"id"),path.call(print,"typeParameters"));}parts.push("(",printFunctionParams(path,options,print),")",path.call(print,"returnType"));if(n.body){parts.push(" ",path.call(print,"body"));}return lines.concat(parts);case"ArrowFunctionExpression":if(n.async){parts.push("async ");}if(n.typeParameters){parts.push(path.call(print,"typeParameters"));}if(!options.arrowParensAlways&&n.params.length===1&&!n.rest&&n.params[0].type==='Identifier'&&!n.params[0].typeAnnotation&&!n.returnType){parts.push(path.call(print,"params",0));}else{parts.push("(",printFunctionParams(path,options,print),")",path.call(print,"returnType"));}parts.push(" => ",path.call(print,"body"));return lines.concat(parts);case"MethodDefinition":return printMethod(path,options,print);case"YieldExpression":parts.push("yield");if(n.delegate)parts.push("*");if(n.argument)parts.push(" ",path.call(print,"argument"));return lines.concat(parts);case"AwaitExpression":parts.push("await");if(n.all)parts.push("*");if(n.argument)parts.push(" ",path.call(print,"argument"));return lines.concat(parts);case"ModuleDeclaration":parts.push("module",path.call(print,"id"));if(n.source){assert_1.default.ok(!n.body);parts.push("from",path.call(print,"source"));}else{parts.push(path.call(print,"body"));}return lines.fromString(" ").join(parts);case"ImportSpecifier":if(n.importKind&&n.importKind!=="value"){parts.push(n.importKind+" ");}if(n.imported){parts.push(path.call(print,"imported"));if(n.local&&n.local.name!==n.imported.name){parts.push(" as ",path.call(print,"local"));}}else if(n.id){parts.push(path.call(print,"id"));if(n.name){parts.push(" as ",path.call(print,"name"));}}return lines.concat(parts);case"ExportSpecifier":if(n.local){parts.push(path.call(print,"local"));if(n.exported&&n.exported.name!==n.local.name){parts.push(" as ",path.call(print,"exported"));}}else if(n.id){parts.push(path.call(print,"id"));if(n.name){parts.push(" as ",path.call(print,"name"));}}return lines.concat(parts);case"ExportBatchSpecifier":return lines.fromString("*");case"ImportNamespaceSpecifier":parts.push("* as ");if(n.local){parts.push(path.call(print,"local"));}else if(n.id){parts.push(path.call(print,"id"));}return lines.concat(parts);case"ImportDefaultSpecifier":if(n.local){return path.call(print,"local");}return path.call(print,"id");case"TSExportAssignment":return lines.concat(["export = ",path.call(print,"expression")]);case"ExportDeclaration":case"ExportDefaultDeclaration":case"ExportNamedDeclaration":return printExportDeclaration(path,options,print);case"ExportAllDeclaration":parts.push("export *");if(n.exported){parts.push(" as ",path.call(print,"exported"));}parts.push(" from ",path.call(print,"source"),";");return lines.concat(parts);case"TSNamespaceExportDeclaration":parts.push("export as namespace ",path.call(print,"id"));return maybeAddSemicolon(lines.concat(parts));case"ExportNamespaceSpecifier":return lines.concat(["* as ",path.call(print,"exported")]);case"ExportDefaultSpecifier":return path.call(print,"exported");case"Import":return lines.fromString("import",options);case"ImportDeclaration":{parts.push("import ");if(n.importKind&&n.importKind!=="value"){parts.push(n.importKind+" ");}if(n.specifiers&&n.specifiers.length>0){var unbracedSpecifiers_1=[];var bracedSpecifiers_1=[];path.each(function(specifierPath){var spec=specifierPath.getValue();if(spec.type==="ImportSpecifier"){bracedSpecifiers_1.push(print(specifierPath));}else if(spec.type==="ImportDefaultSpecifier"||spec.type==="ImportNamespaceSpecifier"){unbracedSpecifiers_1.push(print(specifierPath));}},"specifiers");unbracedSpecifiers_1.forEach(function(lines,i){if(i>0){parts.push(", ");}parts.push(lines);});if(bracedSpecifiers_1.length>0){var lines_2=lines.fromString(", ").join(bracedSpecifiers_1);if(lines_2.getLineLength(1)>options.wrapColumn){lines_2=lines.concat([lines.fromString(",\n").join(bracedSpecifiers_1).indent(options.tabWidth),","]);}if(unbracedSpecifiers_1.length>0){parts.push(", ");}if(lines_2.length>1){parts.push("{\n",lines_2,"\n}");}else if(options.objectCurlySpacing){parts.push("{ ",lines_2," }");}else{parts.push("{",lines_2,"}");}}parts.push(" from ");}parts.push(path.call(print,"source"),";");return lines.concat(parts);}case"BlockStatement":var naked=path.call(function(bodyPath){return printStatementSequence(bodyPath,options,print);},"body");if(naked.isEmpty()){if(!n.directives||n.directives.length===0){return lines.fromString("{}");}}parts.push("{\n");// Babel 6
  if(n.directives){path.each(function(childPath){parts.push(print(childPath).indent(options.tabWidth),";",n.directives.length>1||!naked.isEmpty()?"\n":"");},"directives");}parts.push(naked.indent(options.tabWidth));parts.push("\n}");return lines.concat(parts);case"ReturnStatement":parts.push("return");if(n.argument){var argLines=path.call(print,"argument");if(argLines.startsWithComment()||argLines.length>1&&namedTypes.JSXElement&&namedTypes.JSXElement.check(n.argument)){parts.push(" (\n",argLines.indent(options.tabWidth),"\n)");}else{parts.push(" ",argLines);}}parts.push(";");return lines.concat(parts);case"CallExpression":case"OptionalCallExpression":parts.push(path.call(print,"callee"));if(n.type==="OptionalCallExpression"&&n.callee.type!=="OptionalMemberExpression"){parts.push("?.");}parts.push(printArgumentsList(path,options,print));return lines.concat(parts);case"ObjectExpression":case"ObjectPattern":case"ObjectTypeAnnotation":var allowBreak=false;var isTypeAnnotation=n.type==="ObjectTypeAnnotation";var separator=options.flowObjectCommas?",":isTypeAnnotation?";":",";var fields=[];if(isTypeAnnotation){fields.push("indexers","callProperties");if(n.internalSlots!=null){fields.push("internalSlots");}}fields.push("properties");var len=0;fields.forEach(function(field){len+=n[field].length;});var oneLine=isTypeAnnotation&&len===1||len===0;var leftBrace=n.exact?"{|":"{";var rightBrace=n.exact?"|}":"}";parts.push(oneLine?leftBrace:leftBrace+"\n");var leftBraceIndex=parts.length-1;var i=0;fields.forEach(function(field){path.each(function(childPath){var lines=print(childPath);if(!oneLine){lines=lines.indent(options.tabWidth);}var multiLine=!isTypeAnnotation&&lines.length>1;if(multiLine&&allowBreak){// Similar to the logic for BlockStatement.
  parts.push("\n");}parts.push(lines);if(i<len-1){// Add an extra line break if the previous object property
  // had a multi-line value.
  parts.push(separator+(multiLine?"\n\n":"\n"));allowBreak=!multiLine;}else if(len!==1&&isTypeAnnotation){parts.push(separator);}else if(!oneLine&&util$1.isTrailingCommaEnabled(options,"objects")){parts.push(separator);}i++;},field);});if(n.inexact){var line=lines.fromString("...",options);if(oneLine){if(len>0){parts.push(separator," ");}parts.push(line);}else{// No trailing separator after ... to maintain parity with prettier.
  parts.push("\n",line.indent(options.tabWidth));}}parts.push(oneLine?rightBrace:"\n"+rightBrace);if(i!==0&&oneLine&&options.objectCurlySpacing){parts[leftBraceIndex]=leftBrace+" ";parts[parts.length-1]=" "+rightBrace;}if(n.typeAnnotation){parts.push(path.call(print,"typeAnnotation"));}return lines.concat(parts);case"PropertyPattern":return lines.concat([path.call(print,"key"),": ",path.call(print,"pattern")]);case"ObjectProperty":// Babel 6
  case"Property":// Non-standard AST node type.
  if(n.method||n.kind==="get"||n.kind==="set"){return printMethod(path,options,print);}var key=path.call(print,"key");if(n.computed){parts.push("[",key,"]");}else{parts.push(key);}if(!n.shorthand){parts.push(": ",path.call(print,"value"));}return lines.concat(parts);case"ClassMethod":// Babel 6
  case"ObjectMethod":// Babel 6
  case"ClassPrivateMethod":case"TSDeclareMethod":return printMethod(path,options,print);case"PrivateName":return lines.concat(["#",path.call(print,"id")]);case"Decorator":return lines.concat(["@",path.call(print,"expression")]);case"ArrayExpression":case"ArrayPattern":var elems=n.elements,len=elems.length;var printed=path.map(print,"elements");var joined=lines.fromString(", ").join(printed);var oneLine=joined.getLineLength(1)<=options.wrapColumn;if(oneLine){if(options.arrayBracketSpacing){parts.push("[ ");}else{parts.push("[");}}else{parts.push("[\n");}path.each(function(elemPath){var i=elemPath.getName();var elem=elemPath.getValue();if(!elem){// If the array expression ends with a hole, that hole
  // will be ignored by the interpreter, but if it ends with
  // two (or more) holes, we need to write out two (or more)
  // commas so that the resulting code is interpreted with
  // both (all) of the holes.
  parts.push(",");}else{var lines=printed[i];if(oneLine){if(i>0)parts.push(" ");}else{lines=lines.indent(options.tabWidth);}parts.push(lines);if(i<len-1||!oneLine&&util$1.isTrailingCommaEnabled(options,"arrays"))parts.push(",");if(!oneLine)parts.push("\n");}},"elements");if(oneLine&&options.arrayBracketSpacing){parts.push(" ]");}else{parts.push("]");}return lines.concat(parts);case"SequenceExpression":return lines.fromString(", ").join(path.map(print,"expressions"));case"ThisExpression":return lines.fromString("this");case"Super":return lines.fromString("super");case"NullLiteral":// Babel 6 Literal split
  return lines.fromString("null");case"RegExpLiteral":// Babel 6 Literal split
  return lines.fromString(n.extra.raw);case"BigIntLiteral":// Babel 7 Literal split
  return lines.fromString(n.value+"n");case"NumericLiteral":// Babel 6 Literal Split
  // Keep original representation for numeric values not in base 10.
  if(n.extra&&typeof n.extra.raw==="string"&&Number(n.extra.raw)===n.value){return lines.fromString(n.extra.raw,options);}return lines.fromString(n.value,options);case"BooleanLiteral":// Babel 6 Literal split
  case"StringLiteral":// Babel 6 Literal split
  case"Literal":// Numeric values may be in bases other than 10. Use their raw
  // representation if equivalent.
  if(typeof n.value==="number"&&typeof n.raw==="string"&&Number(n.raw)===n.value){return lines.fromString(n.raw,options);}if(typeof n.value!=="string"){return lines.fromString(n.value,options);}return lines.fromString(nodeStr(n.value,options),options);case"Directive":// Babel 6
  return path.call(print,"value");case"DirectiveLiteral":// Babel 6
  return lines.fromString(nodeStr(n.value,options));case"InterpreterDirective":return lines.fromString("#!"+n.value+"\n",options);case"ModuleSpecifier":if(n.local){throw new Error("The ESTree ModuleSpecifier type should be abstract");}// The Esprima ModuleSpecifier type is just a string-valued
  // Literal identifying the imported-from module.
  return lines.fromString(nodeStr(n.value,options),options);case"UnaryExpression":parts.push(n.operator);if(/[a-z]$/.test(n.operator))parts.push(" ");parts.push(path.call(print,"argument"));return lines.concat(parts);case"UpdateExpression":parts.push(path.call(print,"argument"),n.operator);if(n.prefix)parts.reverse();return lines.concat(parts);case"ConditionalExpression":return lines.concat([path.call(print,"test")," ? ",path.call(print,"consequent")," : ",path.call(print,"alternate")]);case"NewExpression":parts.push("new ",path.call(print,"callee"));var args=n.arguments;if(args){parts.push(printArgumentsList(path,options,print));}return lines.concat(parts);case"VariableDeclaration":if(n.declare){parts.push("declare ");}parts.push(n.kind," ");var maxLen=0;var printed=path.map(function(childPath){var lines=print(childPath);maxLen=Math.max(lines.length,maxLen);return lines;},"declarations");if(maxLen===1){parts.push(lines.fromString(", ").join(printed));}else if(printed.length>1){parts.push(lines.fromString(",\n").join(printed).indentTail(n.kind.length+1));}else{parts.push(printed[0]);}// We generally want to terminate all variable declarations with a
  // semicolon, except when they are children of for loops.
  var parentNode=path.getParentNode();if(!namedTypes.ForStatement.check(parentNode)&&!namedTypes.ForInStatement.check(parentNode)&&!(namedTypes.ForOfStatement&&namedTypes.ForOfStatement.check(parentNode))&&!(namedTypes.ForAwaitStatement&&namedTypes.ForAwaitStatement.check(parentNode))){parts.push(";");}return lines.concat(parts);case"VariableDeclarator":return n.init?lines.fromString(" = ").join([path.call(print,"id"),path.call(print,"init")]):path.call(print,"id");case"WithStatement":return lines.concat(["with (",path.call(print,"object"),") ",path.call(print,"body")]);case"IfStatement":var con=adjustClause(path.call(print,"consequent"),options);parts.push("if (",path.call(print,"test"),")",con);if(n.alternate)parts.push(endsWithBrace(con)?" else":"\nelse",adjustClause(path.call(print,"alternate"),options));return lines.concat(parts);case"ForStatement":// TODO Get the for (;;) case right.
  var init=path.call(print,"init"),sep=init.length>1?";\n":"; ",forParen="for (",indented=lines.fromString(sep).join([init,path.call(print,"test"),path.call(print,"update")]).indentTail(forParen.length),head=lines.concat([forParen,indented,")"]),clause=adjustClause(path.call(print,"body"),options);parts.push(head);if(head.length>1){parts.push("\n");clause=clause.trimLeft();}parts.push(clause);return lines.concat(parts);case"WhileStatement":return lines.concat(["while (",path.call(print,"test"),")",adjustClause(path.call(print,"body"),options)]);case"ForInStatement":// Note: esprima can't actually parse "for each (".
  return lines.concat([n.each?"for each (":"for (",path.call(print,"left")," in ",path.call(print,"right"),")",adjustClause(path.call(print,"body"),options)]);case"ForOfStatement":case"ForAwaitStatement":parts.push("for ");if(n.await||n.type==="ForAwaitStatement"){parts.push("await ");}parts.push("(",path.call(print,"left")," of ",path.call(print,"right"),")",adjustClause(path.call(print,"body"),options));return lines.concat(parts);case"DoWhileStatement":var doBody=lines.concat(["do",adjustClause(path.call(print,"body"),options)]);parts.push(doBody);if(endsWithBrace(doBody))parts.push(" while");else parts.push("\nwhile");parts.push(" (",path.call(print,"test"),");");return lines.concat(parts);case"DoExpression":var statements=path.call(function(bodyPath){return printStatementSequence(bodyPath,options,print);},"body");return lines.concat(["do {\n",statements.indent(options.tabWidth),"\n}"]);case"BreakStatement":parts.push("break");if(n.label)parts.push(" ",path.call(print,"label"));parts.push(";");return lines.concat(parts);case"ContinueStatement":parts.push("continue");if(n.label)parts.push(" ",path.call(print,"label"));parts.push(";");return lines.concat(parts);case"LabeledStatement":return lines.concat([path.call(print,"label"),":\n",path.call(print,"body")]);case"TryStatement":parts.push("try ",path.call(print,"block"));if(n.handler){parts.push(" ",path.call(print,"handler"));}else if(n.handlers){path.each(function(handlerPath){parts.push(" ",print(handlerPath));},"handlers");}if(n.finalizer){parts.push(" finally ",path.call(print,"finalizer"));}return lines.concat(parts);case"CatchClause":parts.push("catch ");if(n.param){parts.push("(",path.call(print,"param"));}if(n.guard){// Note: esprima does not recognize conditional catch clauses.
  parts.push(" if ",path.call(print,"guard"));}if(n.param){parts.push(") ");}parts.push(path.call(print,"body"));return lines.concat(parts);case"ThrowStatement":return lines.concat(["throw ",path.call(print,"argument"),";"]);case"SwitchStatement":return lines.concat(["switch (",path.call(print,"discriminant"),") {\n",lines.fromString("\n").join(path.map(print,"cases")),"\n}"]);// Note: ignoring n.lexical because it has no printing consequences.
  case"SwitchCase":if(n.test)parts.push("case ",path.call(print,"test"),":");else parts.push("default:");if(n.consequent.length>0){parts.push("\n",path.call(function(consequentPath){return printStatementSequence(consequentPath,options,print);},"consequent").indent(options.tabWidth));}return lines.concat(parts);case"DebuggerStatement":return lines.fromString("debugger;");// JSX extensions below.
  case"JSXAttribute":parts.push(path.call(print,"name"));if(n.value)parts.push("=",path.call(print,"value"));return lines.concat(parts);case"JSXIdentifier":return lines.fromString(n.name,options);case"JSXNamespacedName":return lines.fromString(":").join([path.call(print,"namespace"),path.call(print,"name")]);case"JSXMemberExpression":return lines.fromString(".").join([path.call(print,"object"),path.call(print,"property")]);case"JSXSpreadAttribute":return lines.concat(["{...",path.call(print,"argument"),"}"]);case"JSXSpreadChild":return lines.concat(["{...",path.call(print,"expression"),"}"]);case"JSXExpressionContainer":return lines.concat(["{",path.call(print,"expression"),"}"]);case"JSXElement":case"JSXFragment":var openingPropName="opening"+(n.type==="JSXElement"?"Element":"Fragment");var closingPropName="closing"+(n.type==="JSXElement"?"Element":"Fragment");var openingLines=path.call(print,openingPropName);if(n[openingPropName].selfClosing){assert_1.default.ok(!n[closingPropName],"unexpected "+closingPropName+" element in self-closing "+n.type);return openingLines;}var childLines=lines.concat(path.map(function(childPath){var child=childPath.getValue();if(namedTypes.Literal.check(child)&&typeof child.value==="string"){if(/\S/.test(child.value)){return child.value.replace(/^\s+|\s+$/g,"");}else if(/\n/.test(child.value)){return "\n";}}return print(childPath);},"children")).indentTail(options.tabWidth);var closingLines=path.call(print,closingPropName);return lines.concat([openingLines,childLines,closingLines]);case"JSXOpeningElement":parts.push("<",path.call(print,"name"));var attrParts=[];path.each(function(attrPath){attrParts.push(" ",print(attrPath));},"attributes");var attrLines=lines.concat(attrParts);var needLineWrap=attrLines.length>1||attrLines.getLineLength(1)>options.wrapColumn;if(needLineWrap){attrParts.forEach(function(part,i){if(part===" "){assert_1.default.strictEqual(i%2,0);attrParts[i]="\n";}});attrLines=lines.concat(attrParts).indentTail(options.tabWidth);}parts.push(attrLines,n.selfClosing?" />":">");return lines.concat(parts);case"JSXClosingElement":return lines.concat(["</",path.call(print,"name"),">"]);case"JSXOpeningFragment":return lines.fromString("<>");case"JSXClosingFragment":return lines.fromString("</>");case"JSXText":return lines.fromString(n.value,options);case"JSXEmptyExpression":return lines.fromString("");case"TypeAnnotatedIdentifier":return lines.concat([path.call(print,"annotation")," ",path.call(print,"identifier")]);case"ClassBody":if(n.body.length===0){return lines.fromString("{}");}return lines.concat(["{\n",path.call(function(bodyPath){return printStatementSequence(bodyPath,options,print);},"body").indent(options.tabWidth),"\n}"]);case"ClassPropertyDefinition":parts.push("static ",path.call(print,"definition"));if(!namedTypes.MethodDefinition.check(n.definition))parts.push(";");return lines.concat(parts);case"ClassProperty":var access=n.accessibility||n.access;if(typeof access==="string"){parts.push(access," ");}if(n.static){parts.push("static ");}if(n.abstract){parts.push("abstract ");}if(n.readonly){parts.push("readonly ");}var key=path.call(print,"key");if(n.computed){key=lines.concat(["[",key,"]"]);}if(n.variance){key=lines.concat([printVariance(path,print),key]);}parts.push(key);if(n.optional){parts.push("?");}if(n.typeAnnotation){parts.push(path.call(print,"typeAnnotation"));}if(n.value){parts.push(" = ",path.call(print,"value"));}parts.push(";");return lines.concat(parts);case"ClassPrivateProperty":if(n.static){parts.push("static ");}parts.push(path.call(print,"key"));if(n.typeAnnotation){parts.push(path.call(print,"typeAnnotation"));}if(n.value){parts.push(" = ",path.call(print,"value"));}parts.push(";");return lines.concat(parts);case"ClassDeclaration":case"ClassExpression":if(n.declare){parts.push("declare ");}if(n.abstract){parts.push("abstract ");}parts.push("class");if(n.id){parts.push(" ",path.call(print,"id"));}if(n.typeParameters){parts.push(path.call(print,"typeParameters"));}if(n.superClass){parts.push(" extends ",path.call(print,"superClass"),path.call(print,"superTypeParameters"));}if(n["implements"]&&n['implements'].length>0){parts.push(" implements ",lines.fromString(", ").join(path.map(print,"implements")));}parts.push(" ",path.call(print,"body"));return lines.concat(parts);case"TemplateElement":return lines.fromString(n.value.raw,options).lockIndentTail();case"TemplateLiteral":var expressions=path.map(print,"expressions");parts.push("`");path.each(function(childPath){var i=childPath.getName();parts.push(print(childPath));if(i<expressions.length){parts.push("${",expressions[i],"}");}},"quasis");parts.push("`");return lines.concat(parts).lockIndentTail();case"TaggedTemplateExpression":return lines.concat([path.call(print,"tag"),path.call(print,"quasi")]);// These types are unprintable because they serve as abstract
  // supertypes for other (printable) types.
  case"Node":case"Printable":case"SourceLocation":case"Position":case"Statement":case"Function":case"Pattern":case"Expression":case"Declaration":case"Specifier":case"NamedSpecifier":case"Comment":// Supertype of Block and Line
  case"Flow":// Supertype of all Flow AST node types
  case"FlowType":// Supertype of all Flow types
  case"FlowPredicate":// Supertype of InferredPredicate and DeclaredPredicate
  case"MemberTypeAnnotation":// Flow
  case"Type":// Flow
  case"TSHasOptionalTypeParameterInstantiation":case"TSHasOptionalTypeParameters":case"TSHasOptionalTypeAnnotation":throw new Error("unprintable type: "+JSON.stringify(n.type));case"CommentBlock":// Babel block comment.
  case"Block":// Esprima block comment.
  return lines.concat(["/*",lines.fromString(n.value,options),"*/"]);case"CommentLine":// Babel line comment.
  case"Line":// Esprima line comment.
  return lines.concat(["//",lines.fromString(n.value,options)]);// Type Annotations for Facebook Flow, typically stripped out or
  // transformed away before printing.
  case"TypeAnnotation":if(n.typeAnnotation){if(n.typeAnnotation.type!=="FunctionTypeAnnotation"){parts.push(": ");}parts.push(path.call(print,"typeAnnotation"));return lines.concat(parts);}return lines.fromString("");case"ExistentialTypeParam":case"ExistsTypeAnnotation":return lines.fromString("*",options);case"EmptyTypeAnnotation":return lines.fromString("empty",options);case"AnyTypeAnnotation":return lines.fromString("any",options);case"MixedTypeAnnotation":return lines.fromString("mixed",options);case"ArrayTypeAnnotation":return lines.concat([path.call(print,"elementType"),"[]"]);case"TupleTypeAnnotation":var printed=path.map(print,"types");var joined=lines.fromString(", ").join(printed);var oneLine=joined.getLineLength(1)<=options.wrapColumn;if(oneLine){if(options.arrayBracketSpacing){parts.push("[ ");}else{parts.push("[");}}else{parts.push("[\n");}path.each(function(elemPath){var i=elemPath.getName();var elem=elemPath.getValue();if(!elem){// If the array expression ends with a hole, that hole
  // will be ignored by the interpreter, but if it ends with
  // two (or more) holes, we need to write out two (or more)
  // commas so that the resulting code is interpreted with
  // both (all) of the holes.
  parts.push(",");}else{var lines=printed[i];if(oneLine){if(i>0)parts.push(" ");}else{lines=lines.indent(options.tabWidth);}parts.push(lines);if(i<n.types.length-1||!oneLine&&util$1.isTrailingCommaEnabled(options,"arrays"))parts.push(",");if(!oneLine)parts.push("\n");}},"types");if(oneLine&&options.arrayBracketSpacing){parts.push(" ]");}else{parts.push("]");}return lines.concat(parts);case"BooleanTypeAnnotation":return lines.fromString("boolean",options);case"BooleanLiteralTypeAnnotation":assert_1.default.strictEqual(typeof n.value,"boolean");return lines.fromString(""+n.value,options);case"InterfaceTypeAnnotation":parts.push("interface");if(n.extends&&n.extends.length>0){parts.push(" extends ",lines.fromString(", ").join(path.map(print,"extends")));}parts.push(" ",path.call(print,"body"));return lines.concat(parts);case"DeclareClass":return printFlowDeclaration(path,["class ",path.call(print,"id")," ",path.call(print,"body")]);case"DeclareFunction":return printFlowDeclaration(path,["function ",path.call(print,"id"),";"]);case"DeclareModule":return printFlowDeclaration(path,["module ",path.call(print,"id")," ",path.call(print,"body")]);case"DeclareModuleExports":return printFlowDeclaration(path,["module.exports",path.call(print,"typeAnnotation")]);case"DeclareVariable":return printFlowDeclaration(path,["var ",path.call(print,"id"),";"]);case"DeclareExportDeclaration":case"DeclareExportAllDeclaration":return lines.concat(["declare ",printExportDeclaration(path,options,print)]);case"InferredPredicate":return lines.fromString("%checks",options);case"DeclaredPredicate":return lines.concat(["%checks(",path.call(print,"value"),")"]);case"FunctionTypeAnnotation":// FunctionTypeAnnotation is ambiguous:
  // declare function(a: B): void; OR
  // var A: (a: B) => void;
  var parent=path.getParentNode(0);var isArrowFunctionTypeAnnotation=!(namedTypes.ObjectTypeCallProperty.check(parent)||namedTypes.ObjectTypeInternalSlot.check(parent)&&parent.method||namedTypes.DeclareFunction.check(path.getParentNode(2)));var needsColon=isArrowFunctionTypeAnnotation&&!namedTypes.FunctionTypeParam.check(parent);if(needsColon){parts.push(": ");}parts.push("(",printFunctionParams(path,options,print),")");// The returnType is not wrapped in a TypeAnnotation, so the colon
  // needs to be added separately.
  if(n.returnType){parts.push(isArrowFunctionTypeAnnotation?" => ":": ",path.call(print,"returnType"));}return lines.concat(parts);case"FunctionTypeParam":return lines.concat([path.call(print,"name"),n.optional?'?':'',": ",path.call(print,"typeAnnotation")]);case"GenericTypeAnnotation":return lines.concat([path.call(print,"id"),path.call(print,"typeParameters")]);case"DeclareInterface":parts.push("declare ");// Fall through to InterfaceDeclaration...
  case"InterfaceDeclaration":case"TSInterfaceDeclaration":if(n.declare){parts.push("declare ");}parts.push("interface ",path.call(print,"id"),path.call(print,"typeParameters")," ");if(n["extends"]&&n["extends"].length>0){parts.push("extends ",lines.fromString(", ").join(path.map(print,"extends"))," ");}if(n.body){parts.push(path.call(print,"body"));}return lines.concat(parts);case"ClassImplements":case"InterfaceExtends":return lines.concat([path.call(print,"id"),path.call(print,"typeParameters")]);case"IntersectionTypeAnnotation":return lines.fromString(" & ").join(path.map(print,"types"));case"NullableTypeAnnotation":return lines.concat(["?",path.call(print,"typeAnnotation")]);case"NullLiteralTypeAnnotation":return lines.fromString("null",options);case"ThisTypeAnnotation":return lines.fromString("this",options);case"NumberTypeAnnotation":return lines.fromString("number",options);case"ObjectTypeCallProperty":return path.call(print,"value");case"ObjectTypeIndexer":return lines.concat([printVariance(path,print),"[",path.call(print,"id"),": ",path.call(print,"key"),"]: ",path.call(print,"value")]);case"ObjectTypeProperty":return lines.concat([printVariance(path,print),path.call(print,"key"),n.optional?"?":"",": ",path.call(print,"value")]);case"ObjectTypeInternalSlot":return lines.concat([n.static?"static ":"","[[",path.call(print,"id"),"]]",n.optional?"?":"",n.value.type!=="FunctionTypeAnnotation"?": ":"",path.call(print,"value")]);case"QualifiedTypeIdentifier":return lines.concat([path.call(print,"qualification"),".",path.call(print,"id")]);case"StringLiteralTypeAnnotation":return lines.fromString(nodeStr(n.value,options),options);case"NumberLiteralTypeAnnotation":case"NumericLiteralTypeAnnotation":assert_1.default.strictEqual(typeof n.value,"number");return lines.fromString(JSON.stringify(n.value),options);case"StringTypeAnnotation":return lines.fromString("string",options);case"DeclareTypeAlias":parts.push("declare ");// Fall through to TypeAlias...
  case"TypeAlias":return lines.concat(["type ",path.call(print,"id"),path.call(print,"typeParameters")," = ",path.call(print,"right"),";"]);case"DeclareOpaqueType":parts.push("declare ");// Fall through to OpaqueType...
  case"OpaqueType":parts.push("opaque type ",path.call(print,"id"),path.call(print,"typeParameters"));if(n["supertype"]){parts.push(": ",path.call(print,"supertype"));}if(n["impltype"]){parts.push(" = ",path.call(print,"impltype"));}parts.push(";");return lines.concat(parts);case"TypeCastExpression":return lines.concat(["(",path.call(print,"expression"),path.call(print,"typeAnnotation"),")"]);case"TypeParameterDeclaration":case"TypeParameterInstantiation":return lines.concat(["<",lines.fromString(", ").join(path.map(print,"params")),">"]);case"Variance":if(n.kind==="plus"){return lines.fromString("+");}if(n.kind==="minus"){return lines.fromString("-");}return lines.fromString("");case"TypeParameter":if(n.variance){parts.push(printVariance(path,print));}parts.push(path.call(print,'name'));if(n.bound){parts.push(path.call(print,'bound'));}if(n['default']){parts.push('=',path.call(print,'default'));}return lines.concat(parts);case"TypeofTypeAnnotation":return lines.concat([lines.fromString("typeof ",options),path.call(print,"argument")]);case"UnionTypeAnnotation":return lines.fromString(" | ").join(path.map(print,"types"));case"VoidTypeAnnotation":return lines.fromString("void",options);case"NullTypeAnnotation":return lines.fromString("null",options);// Type Annotations for TypeScript (when using Babylon as parser)
  case"TSType":throw new Error("unprintable type: "+JSON.stringify(n.type));case"TSNumberKeyword":return lines.fromString("number",options);case"TSBigIntKeyword":return lines.fromString("bigint",options);case"TSObjectKeyword":return lines.fromString("object",options);case"TSBooleanKeyword":return lines.fromString("boolean",options);case"TSStringKeyword":return lines.fromString("string",options);case"TSSymbolKeyword":return lines.fromString("symbol",options);case"TSAnyKeyword":return lines.fromString("any",options);case"TSVoidKeyword":return lines.fromString("void",options);case"TSThisType":return lines.fromString("this",options);case"TSNullKeyword":return lines.fromString("null",options);case"TSUndefinedKeyword":return lines.fromString("undefined",options);case"TSUnknownKeyword":return lines.fromString("unknown",options);case"TSNeverKeyword":return lines.fromString("never",options);case"TSArrayType":return lines.concat([path.call(print,"elementType"),"[]"]);case"TSLiteralType":return path.call(print,"literal");case"TSUnionType":return lines.fromString(" | ").join(path.map(print,"types"));case"TSIntersectionType":return lines.fromString(" & ").join(path.map(print,"types"));case"TSConditionalType":parts.push(path.call(print,"checkType")," extends ",path.call(print,"extendsType")," ? ",path.call(print,"trueType")," : ",path.call(print,"falseType"));return lines.concat(parts);case"TSInferType":parts.push("infer ",path.call(print,"typeParameter"));return lines.concat(parts);case"TSParenthesizedType":return lines.concat(["(",path.call(print,"typeAnnotation"),")"]);case"TSFunctionType":case"TSConstructorType":return lines.concat([path.call(print,"typeParameters"),"(",printFunctionParams(path,options,print),")",path.call(print,"typeAnnotation")]);case"TSMappedType":{parts.push(n.readonly?"readonly ":"","[",path.call(print,"typeParameter"),"]",n.optional?"?":"");if(n.typeAnnotation){parts.push(": ",path.call(print,"typeAnnotation"),";");}return lines.concat(["{\n",lines.concat(parts).indent(options.tabWidth),"\n}"]);}case"TSTupleType":return lines.concat(["[",lines.fromString(", ").join(path.map(print,"elementTypes")),"]"]);case"TSRestType":return lines.concat(["...",path.call(print,"typeAnnotation"),"[]"]);case"TSOptionalType":return lines.concat([path.call(print,"typeAnnotation"),"?"]);case"TSIndexedAccessType":return lines.concat([path.call(print,"objectType"),"[",path.call(print,"indexType"),"]"]);case"TSTypeOperator":return lines.concat([path.call(print,"operator")," ",path.call(print,"typeAnnotation")]);case"TSTypeLiteral":{var memberLines_1=lines.fromString(",\n").join(path.map(print,"members"));if(memberLines_1.isEmpty()){return lines.fromString("{}",options);}parts.push("{\n",memberLines_1.indent(options.tabWidth),"\n}");return lines.concat(parts);}case"TSEnumMember":parts.push(path.call(print,"id"));if(n.initializer){parts.push(" = ",path.call(print,"initializer"));}return lines.concat(parts);case"TSTypeQuery":return lines.concat(["typeof ",path.call(print,"exprName")]);case"TSParameterProperty":if(n.accessibility){parts.push(n.accessibility," ");}if(n.export){parts.push("export ");}if(n.static){parts.push("static ");}if(n.readonly){parts.push("readonly ");}parts.push(path.call(print,"parameter"));return lines.concat(parts);case"TSTypeReference":return lines.concat([path.call(print,"typeName"),path.call(print,"typeParameters")]);case"TSQualifiedName":return lines.concat([path.call(print,"left"),".",path.call(print,"right")]);case"TSAsExpression":{var withParens=n.extra&&n.extra.parenthesized===true;if(withParens)parts.push("(");parts.push(path.call(print,"expression"),lines.fromString(" as "),path.call(print,"typeAnnotation"));if(withParens)parts.push(")");return lines.concat(parts);}case"TSNonNullExpression":return lines.concat([path.call(print,"expression"),"!"]);case"TSTypeAnnotation":{// similar to flow's FunctionTypeAnnotation, this can be
  // ambiguous: it can be prefixed by => or :
  // in a type predicate, it takes the for u is U
  var parent=path.getParentNode(0);var prefix=": ";if(namedTypes.TSFunctionType.check(parent)){prefix=" => ";}if(namedTypes.TSTypePredicate.check(parent)){prefix=" is ";}return lines.concat([prefix,path.call(print,"typeAnnotation")]);}case"TSIndexSignature":return lines.concat([n.readonly?"readonly ":"","[",path.map(print,"parameters"),"]",path.call(print,"typeAnnotation")]);case"TSPropertySignature":parts.push(printVariance(path,print),n.readonly?"readonly ":"");if(n.computed){parts.push("[",path.call(print,"key"),"]");}else{parts.push(path.call(print,"key"));}parts.push(n.optional?"?":"",path.call(print,"typeAnnotation"));return lines.concat(parts);case"TSMethodSignature":if(n.computed){parts.push("[",path.call(print,"key"),"]");}else{parts.push(path.call(print,"key"));}if(n.optional){parts.push("?");}parts.push(path.call(print,"typeParameters"),"(",printFunctionParams(path,options,print),")",path.call(print,"typeAnnotation"));return lines.concat(parts);case"TSTypePredicate":return lines.concat([path.call(print,"parameterName"),path.call(print,"typeAnnotation")]);case"TSCallSignatureDeclaration":return lines.concat([path.call(print,"typeParameters"),"(",printFunctionParams(path,options,print),")",path.call(print,"typeAnnotation")]);case"TSConstructSignatureDeclaration":if(n.typeParameters){parts.push("new",path.call(print,"typeParameters"));}else{parts.push("new ");}parts.push("(",printFunctionParams(path,options,print),")",path.call(print,"typeAnnotation"));return lines.concat(parts);case"TSTypeAliasDeclaration":return lines.concat([n.declare?"declare ":"","type ",path.call(print,"id"),path.call(print,"typeParameters")," = ",path.call(print,"typeAnnotation"),";"]);case"TSTypeParameter":parts.push(path.call(print,"name"));// ambiguous because of TSMappedType
  var parent=path.getParentNode(0);var isInMappedType=namedTypes.TSMappedType.check(parent);if(n.constraint){parts.push(isInMappedType?" in ":" extends ",path.call(print,"constraint"));}if(n["default"]){parts.push(" = ",path.call(print,"default"));}return lines.concat(parts);case"TSTypeAssertion":var withParens=n.extra&&n.extra.parenthesized===true;if(withParens){parts.push("(");}parts.push("<",path.call(print,"typeAnnotation"),"> ",path.call(print,"expression"));if(withParens){parts.push(")");}return lines.concat(parts);case"TSTypeParameterDeclaration":case"TSTypeParameterInstantiation":return lines.concat(["<",lines.fromString(", ").join(path.map(print,"params")),">"]);case"TSEnumDeclaration":parts.push(n.declare?"declare ":"",n.const?"const ":"","enum ",path.call(print,"id"));var memberLines=lines.fromString(",\n").join(path.map(print,"members"));if(memberLines.isEmpty()){parts.push(" {}");}else{parts.push(" {\n",memberLines.indent(options.tabWidth),"\n}");}return lines.concat(parts);case"TSExpressionWithTypeArguments":return lines.concat([path.call(print,"expression"),path.call(print,"typeParameters")]);case"TSInterfaceBody":var lines$1=lines.fromString(";\n").join(path.map(print,"body"));if(lines$1.isEmpty()){return lines.fromString("{}",options);}return lines.concat(["{\n",lines$1.indent(options.tabWidth),";","\n}"]);case"TSImportType":parts.push("import(",path.call(print,"argument"),")");if(n.qualifier){parts.push(".",path.call(print,"qualifier"));}if(n.typeParameters){parts.push(path.call(print,"typeParameters"));}return lines.concat(parts);case"TSImportEqualsDeclaration":if(n.isExport){parts.push("export ");}parts.push("import ",path.call(print,"id")," = ",path.call(print,"moduleReference"));return maybeAddSemicolon(lines.concat(parts));case"TSExternalModuleReference":return lines.concat(["require(",path.call(print,"expression"),")"]);case"TSModuleDeclaration":{var parent_1=path.getParentNode();if(parent_1.type==="TSModuleDeclaration"){parts.push(".");}else{if(n.declare){parts.push("declare ");}if(!n.global){var isExternal=n.id.type==="StringLiteral"||n.id.type==="Literal"&&typeof n.id.value==="string";if(isExternal){parts.push("module ");}else if(n.loc&&n.loc.lines&&n.id.loc){var prefix_1=n.loc.lines.sliceString(n.loc.start,n.id.loc.start);// These keywords are fundamentally ambiguous in the
  // Babylon parser, and not reflected in the AST, so
  // the best we can do is to match the original code,
  // when possible.
  if(prefix_1.indexOf("module")>=0){parts.push("module ");}else{parts.push("namespace ");}}else{parts.push("namespace ");}}}parts.push(path.call(print,"id"));if(n.body&&n.body.type==="TSModuleDeclaration"){parts.push(path.call(print,"body"));}else if(n.body){var bodyLines=path.call(print,"body");if(bodyLines.isEmpty()){parts.push(" {}");}else{parts.push(" {\n",bodyLines.indent(options.tabWidth),"\n}");}}return lines.concat(parts);}case"TSModuleBlock":return path.call(function(bodyPath){return printStatementSequence(bodyPath,options,print);},"body");// Unhandled types below. If encountered, nodes of these types should
  // be either left alone or desugared into AST types that are fully
  // supported by the pretty-printer.
  case"ClassHeritage":// TODO
  case"ComprehensionBlock":// TODO
  case"ComprehensionExpression":// TODO
  case"Glob":// TODO
  case"GeneratorExpression":// TODO
  case"LetStatement":// TODO
  case"LetExpression":// TODO
  case"GraphExpression":// TODO
  case"GraphIndexExpression":// TODO
  // XML types that nobody cares about or needs to print.
  case"XMLDefaultDeclaration":case"XMLAnyName":case"XMLQualifiedIdentifier":case"XMLFunctionQualifiedIdentifier":case"XMLAttributeSelector":case"XMLFilterExpression":case"XML":case"XMLElement":case"XMLList":case"XMLEscape":case"XMLText":case"XMLStartTag":case"XMLEndTag":case"XMLPointTag":case"XMLName":case"XMLAttribute":case"XMLCdata":case"XMLComment":case"XMLProcessingInstruction":default:debugger;throw new Error("unknown type: "+JSON.stringify(n.type));}}function printDecorators(path,printPath){var parts=[];var node=path.getValue();if(node.decorators&&node.decorators.length>0&&// If the parent node is an export declaration, it will be
  // responsible for printing node.decorators.
  !util$1.getParentExportDeclaration(path)){path.each(function(decoratorPath){parts.push(printPath(decoratorPath),"\n");},"decorators");}else if(util$1.isExportDeclaration(node)&&node.declaration&&node.declaration.decorators){// Export declarations are responsible for printing any decorators
  // that logically apply to node.declaration.
  path.each(function(decoratorPath){parts.push(printPath(decoratorPath),"\n");},"declaration","decorators");}return lines.concat(parts);}function printStatementSequence(path,options,print){var filtered=[];var sawComment=false;var sawStatement=false;path.each(function(stmtPath){var stmt=stmtPath.getValue();// Just in case the AST has been modified to contain falsy
  // "statements," it's safer simply to skip them.
  if(!stmt){return;}// Skip printing EmptyStatement nodes to avoid leaving stray
  // semicolons lying around.
  if(stmt.type==="EmptyStatement"&&!(stmt.comments&&stmt.comments.length>0)){return;}if(namedTypes.Comment.check(stmt)){// The pretty printer allows a dangling Comment node to act as
  // a Statement when the Comment can't be attached to any other
  // non-Comment node in the tree.
  sawComment=true;}else if(namedTypes.Statement.check(stmt)){sawStatement=true;}else{// When the pretty printer encounters a string instead of an
  // AST node, it just prints the string. This behavior can be
  // useful for fine-grained formatting decisions like inserting
  // blank lines.
  isString.assert(stmt);}// We can't hang onto stmtPath outside of this function, because
  // it's just a reference to a mutable FastPath object, so we have
  // to go ahead and print it here.
  filtered.push({node:stmt,printed:print(stmtPath)});});if(sawComment){assert_1.default.strictEqual(sawStatement,false,"Comments may appear as statements in otherwise empty statement "+"lists, but may not coexist with non-Comment nodes.");}var prevTrailingSpace=null;var len=filtered.length;var parts=[];filtered.forEach(function(info,i){var printed=info.printed;var stmt=info.node;var multiLine=printed.length>1;var notFirst=i>0;var notLast=i<len-1;var leadingSpace;var trailingSpace;var lines=stmt&&stmt.loc&&stmt.loc.lines;var trueLoc=lines&&options.reuseWhitespace&&util$1.getTrueLoc(stmt,lines);if(notFirst){if(trueLoc){var beforeStart=lines.skipSpaces(trueLoc.start,true);var beforeStartLine=beforeStart?beforeStart.line:1;var leadingGap=trueLoc.start.line-beforeStartLine;leadingSpace=Array(leadingGap+1).join("\n");}else{leadingSpace=multiLine?"\n\n":"\n";}}else{leadingSpace="";}if(notLast){if(trueLoc){var afterEnd=lines.skipSpaces(trueLoc.end);var afterEndLine=afterEnd?afterEnd.line:lines.length;var trailingGap=afterEndLine-trueLoc.end.line;trailingSpace=Array(trailingGap+1).join("\n");}else{trailingSpace=multiLine?"\n\n":"\n";}}else{trailingSpace="";}parts.push(maxSpace(prevTrailingSpace,leadingSpace),printed);if(notLast){prevTrailingSpace=trailingSpace;}else if(trailingSpace){parts.push(trailingSpace);}});return lines.concat(parts);}function maxSpace(s1,s2){if(!s1&&!s2){return lines.fromString("");}if(!s1){return lines.fromString(s2);}if(!s2){return lines.fromString(s1);}var spaceLines1=lines.fromString(s1);var spaceLines2=lines.fromString(s2);if(spaceLines2.length>spaceLines1.length){return spaceLines2;}return spaceLines1;}function printMethod(path,options,print){var node=path.getNode();var kind=node.kind;var parts=[];var nodeValue=node.value;if(!namedTypes.FunctionExpression.check(nodeValue)){nodeValue=node;}var access=node.accessibility||node.access;if(typeof access==="string"){parts.push(access," ");}if(node.static){parts.push("static ");}if(node.abstract){parts.push("abstract ");}if(node.readonly){parts.push("readonly ");}if(nodeValue.async){parts.push("async ");}if(nodeValue.generator){parts.push("*");}if(kind==="get"||kind==="set"){parts.push(kind," ");}var key=path.call(print,"key");if(node.computed){key=lines.concat(["[",key,"]"]);}parts.push(key);if(node.optional){parts.push("?");}if(node===nodeValue){parts.push(path.call(print,"typeParameters"),"(",printFunctionParams(path,options,print),")",path.call(print,"returnType"));if(node.body){parts.push(" ",path.call(print,"body"));}else{parts.push(";");}}else{parts.push(path.call(print,"value","typeParameters"),"(",path.call(function(valuePath){return printFunctionParams(valuePath,options,print);},"value"),")",path.call(print,"value","returnType"));if(nodeValue.body){parts.push(" ",path.call(print,"value","body"));}else{parts.push(";");}}return lines.concat(parts);}function printArgumentsList(path,options,print){var printed=path.map(print,"arguments");var trailingComma=util$1.isTrailingCommaEnabled(options,"parameters");var joined=lines.fromString(", ").join(printed);if(joined.getLineLength(1)>options.wrapColumn){joined=lines.fromString(",\n").join(printed);return lines.concat(["(\n",joined.indent(options.tabWidth),trailingComma?",\n)":"\n)"]);}return lines.concat(["(",joined,")"]);}function printFunctionParams(path,options,print){var fun=path.getValue();if(fun.params){var params=fun.params;var printed=path.map(print,"params");}else if(fun.parameters){params=fun.parameters;printed=path.map(print,"parameters");}if(fun.defaults){path.each(function(defExprPath){var i=defExprPath.getName();var p=printed[i];if(p&&defExprPath.getValue()){printed[i]=lines.concat([p," = ",print(defExprPath)]);}},"defaults");}if(fun.rest){printed.push(lines.concat(["...",path.call(print,"rest")]));}var joined=lines.fromString(", ").join(printed);if(joined.length>1||joined.getLineLength(1)>options.wrapColumn){joined=lines.fromString(",\n").join(printed);if(util$1.isTrailingCommaEnabled(options,"parameters")&&!fun.rest&&params[params.length-1].type!=='RestElement'){joined=lines.concat([joined,",\n"]);}else{joined=lines.concat([joined,"\n"]);}return lines.concat(["\n",joined.indent(options.tabWidth)]);}return joined;}function printExportDeclaration(path,options,print){var decl=path.getValue();var parts=["export "];if(decl.exportKind&&decl.exportKind!=="value"){parts.push(decl.exportKind+" ");}var shouldPrintSpaces=options.objectCurlySpacing;namedTypes.Declaration.assert(decl);if(decl["default"]||decl.type==="ExportDefaultDeclaration"){parts.push("default ");}if(decl.declaration){parts.push(path.call(print,"declaration"));}else if(decl.specifiers){if(decl.specifiers.length===1&&decl.specifiers[0].type==="ExportBatchSpecifier"){parts.push("*");}else if(decl.specifiers.length===0){parts.push("{}");}else if(decl.specifiers[0].type==='ExportDefaultSpecifier'){var unbracedSpecifiers_2=[];var bracedSpecifiers_2=[];path.each(function(specifierPath){var spec=specifierPath.getValue();if(spec.type==="ExportDefaultSpecifier"){unbracedSpecifiers_2.push(print(specifierPath));}else{bracedSpecifiers_2.push(print(specifierPath));}},"specifiers");unbracedSpecifiers_2.forEach(function(lines,i){if(i>0){parts.push(", ");}parts.push(lines);});if(bracedSpecifiers_2.length>0){var lines_3=lines.fromString(", ").join(bracedSpecifiers_2);if(lines_3.getLineLength(1)>options.wrapColumn){lines_3=lines.concat([lines.fromString(",\n").join(bracedSpecifiers_2).indent(options.tabWidth),","]);}if(unbracedSpecifiers_2.length>0){parts.push(", ");}if(lines_3.length>1){parts.push("{\n",lines_3,"\n}");}else if(options.objectCurlySpacing){parts.push("{ ",lines_3," }");}else{parts.push("{",lines_3,"}");}}}else{parts.push(shouldPrintSpaces?"{ ":"{",lines.fromString(", ").join(path.map(print,"specifiers")),shouldPrintSpaces?" }":"}");}if(decl.source){parts.push(" from ",path.call(print,"source"));}}var lines$1=lines.concat(parts);if(lastNonSpaceCharacter(lines$1)!==";"&&!(decl.declaration&&(decl.declaration.type==="FunctionDeclaration"||decl.declaration.type==="ClassDeclaration"||decl.declaration.type==="TSModuleDeclaration"||decl.declaration.type==="TSInterfaceDeclaration"||decl.declaration.type==="TSEnumDeclaration"))){lines$1=lines.concat([lines$1,";"]);}return lines$1;}function printFlowDeclaration(path,parts){var parentExportDecl=util$1.getParentExportDeclaration(path);if(parentExportDecl){assert_1.default.strictEqual(parentExportDecl.type,"DeclareExportDeclaration");}else{// If the parent node has type DeclareExportDeclaration, then it
  // will be responsible for printing the "declare" token. Otherwise
  // it needs to be printed with this non-exported declaration node.
  parts.unshift("declare ");}return lines.concat(parts);}function printVariance(path,print){return path.call(function(variancePath){var value=variancePath.getValue();if(value){if(value==="plus"){return lines.fromString("+");}if(value==="minus"){return lines.fromString("-");}return print(variancePath);}return lines.fromString("");},"variance");}function adjustClause(clause,options){if(clause.length>1)return lines.concat([" ",clause]);return lines.concat(["\n",maybeAddSemicolon(clause).indent(options.tabWidth)]);}function lastNonSpaceCharacter(lines){var pos=lines.lastPos();do{var ch=lines.charAt(pos);if(/\S/.test(ch))return ch;}while(lines.prevPos(pos));}function endsWithBrace(lines){return lastNonSpaceCharacter(lines)==="}";}function swapQuotes(str){return str.replace(/['"]/g,function(m){return m==='"'?'\'':'"';});}function nodeStr(str,options){isString.assert(str);switch(options.quote){case"auto":var double=JSON.stringify(str);var single=swapQuotes(JSON.stringify(swapQuotes(str)));return double.length>single.length?single:double;case"single":return swapQuotes(JSON.stringify(swapQuotes(str)));case"double":default:return JSON.stringify(str);}}function maybeAddSemicolon(lines$1){var eoc=lastNonSpaceCharacter(lines$1);if(!eoc||"\n};".indexOf(eoc)<0)return lines.concat([lines$1,";"]);return lines$1;}});unwrapExports(printer);var printer_1=printer.Printer;var main$1=createCommonjsModule(function(module,exports){var __importDefault=this&&this.__importDefault||function(mod){return mod&&mod.__esModule?mod:{"default":mod};};var __importStar=this&&this.__importStar||function(mod){if(mod&&mod.__esModule)return mod;var result={};if(mod!=null)for(var k in mod)if(Object.hasOwnProperty.call(mod,k))result[k]=mod[k];result["default"]=mod;return result;};Object.defineProperty(exports,"__esModule",{value:true});var fs_1=__importDefault(fs);var types=__importStar(main);exports.types=types;exports.parse=parser.parse;/**
  	 * Traverse and potentially modify an abstract syntax tree using a
  	 * convenient visitor syntax:
  	 *
  	 *   recast.visit(ast, {
  	 *     names: [],
  	 *     visitIdentifier: function(path) {
  	 *       var node = path.value;
  	 *       this.visitor.names.push(node.name);
  	 *       this.traverse(path);
  	 *     }
  	 *   });
  	 */var ast_types_1=main;exports.visit=ast_types_1.visit;/**
  	 * Reprint a modified syntax tree using as much of the original source
  	 * code as possible.
  	 */function print(node,options){return new printer.Printer(options).print(node);}exports.print=print;/**
  	 * Print without attempting to reuse any original source code.
  	 */function prettyPrint(node,options){return new printer.Printer(options).printGenerically(node);}exports.prettyPrint=prettyPrint;/**
  	 * Convenient command-line interface (see e.g. example/add-braces).
  	 */function run(transformer,options){return runFile(process.argv[2],transformer,options);}exports.run=run;function runFile(path,transformer,options){fs_1.default.readFile(path,"utf-8",function(err,code){if(err){console.error(err);return;}runString(code,transformer,options);});}function defaultWriteback(output){process.stdout.write(output);}function runString(code,transformer,options){var writeback=options&&options.writeback||defaultWriteback;transformer(parser.parse(code,options),function(node){writeback(print(node,options).code);});}});unwrapExports(main$1);var main_1$1=main$1.types;var main_2$1=main$1.parse;var main_3$1=main$1.visit;var main_4$1=main$1.print;var main_5$1=main$1.prettyPrint;var main_6$1=main$1.run;const types$1=main_1$1;const builders=main_1$1.builders;const namedTypes=main_1$1.namedTypes;function nullNode(){return builders.literal(null);}function simplePropertyNode(key,value){return builders.property('init',builders.literal(key),value,false);}/**
  	 * Return a source map as JSON, it it has not the toJSON method it means it can
  	 * be used right the way
  	 * @param   { SourceMapGenerator|Object } map - a sourcemap generator or simply an json object
  	 * @returns { Object } the source map as JSON
  	 */function sourcemapAsJSON(map){if(map&&map.toJSON)return map.toJSON();return map;}/**
  	 * Detect node js environements
  	 * @returns { boolean } true if the runtime is node
  	 */function isNode(){return typeof process!=='undefined';}/**
  	 * Compose two sourcemaps
  	 * @param   { SourceMapGenerator } formerMap - original sourcemap
  	 * @param   { SourceMapGenerator } latterMap - target sourcemap
  	 * @returns { Object } sourcemap json
  	 */function composeSourcemaps(formerMap,latterMap){if(isNode()&&formerMap&&latterMap&&latterMap.mappings){return util_5(sourcemapAsJSON(formerMap),sourcemapAsJSON(latterMap));}else if(isNode()&&formerMap){return sourcemapAsJSON(formerMap);}return {};}/**
  	 * Create a new sourcemap generator
  	 * @param   { Object } options - sourcemap options
  	 * @returns { SourceMapGenerator } SourceMapGenerator instance
  	 */function createSourcemap(options){return new sourceMap_1(options);}const Output=Object.freeze({code:'',ast:[],meta:{},map:null});/**
  	 * Create the right output data result of a parsing
  	 * @param   { Object } data - output data
  	 * @param   { string } data.code - code generated
  	 * @param   { AST } data.ast - ast representing the code
  	 * @param   { SourceMapGenerator } data.map - source map generated along with the code
  	 * @param   { Object } meta - compilation meta infomration
  	 * @returns { Output } output container object
  	 */function createOutput(data,meta){const output=Object.assign({},Output,{},data,{meta});if(!output.map&&meta&&meta.options&&meta.options.file)return Object.assign({},output,{map:createSourcemap({file:meta.options.file})});return output;}/**
  	 * Transform the source code received via a compiler function
  	 * @param   { Function } compiler - function needed to generate the output code
  	 * @param   { Object } meta - compilation meta information
  	 * @param   { string } source - source code
  	 * @returns { Output } output - the result of the compiler
  	 */function transform(compiler,meta,source){const result=compiler?compiler(source,meta):{code:source};return createOutput(result,meta);}/**
  	 * Throw an error with a descriptive message
  	 * @param   { string } message - error message
  	 * @returns { undefined } hoppla.. at this point the program should stop working
  	 */function panic(message){throw new Error(message);}const postprocessors=new Set();/**
  	 * Register a postprocessor that will be used after the parsing and compilation of the riot tags
  	 * @param { Function } postprocessor - transformer that will receive the output code ans sourcemap
  	 * @returns { Set } the postprocessors collection
  	 */function register(postprocessor){if(postprocessors.has(postprocessor)){panic(`This postprocessor "${postprocessor.name||postprocessor.toString()}" was already registered`);}postprocessors.add(postprocessor);return postprocessors;}/**
  	 * Exec all the postprocessors in sequence combining the sourcemaps generated
  	 * @param   { Output } compilerOutput - output generated by the compiler
  	 * @param   { Object } meta - compiling meta information
  	 * @returns { Output } object containing output code and source map
  	 */function execute(compilerOutput,meta){return Array.from(postprocessors).reduce(function(acc,postprocessor){const{code,map}=acc;const output=postprocessor(code,meta);return {code:output.code,map:composeSourcemaps(map,output.map)};},createOutput(compilerOutput,meta));}/**
  	 * Parsers that can be registered by users to preparse components fragments
  	 * @type { Object }
  	 */const preprocessors=Object.freeze({javascript:new Map(),css:new Map(),template:new Map().set('default',code=>({code}))});// throw a processor type error
  function preprocessorTypeError(type){panic(`No preprocessor of type "${type}" was found, please make sure to use one of these: 'javascript', 'css' or 'template'`);}// throw an error if the preprocessor was not registered
  function preprocessorNameNotFoundError(name){panic(`No preprocessor named "${name}" was found, are you sure you have registered it?'`);}/**
  	 * Register a custom preprocessor
  	 * @param   { string } type - preprocessor type either 'js', 'css' or 'template'
  	 * @param   { string } name - unique preprocessor id
  	 * @param   { Function } preprocessor - preprocessor function
  	 * @returns { Map } - the preprocessors map
  	 */function register$1(type,name,preprocessor){if(!type)panic('Please define the type of preprocessor you want to register \'javascript\', \'css\' or \'template\'');if(!name)panic('Please define a name for your preprocessor');if(!preprocessor)panic('Please provide a preprocessor function');if(!preprocessors[type])preprocessorTypeError(type);if(preprocessors[type].has(name))panic(`The preprocessor ${name} was already registered before`);preprocessors[type].set(name,preprocessor);return preprocessors;}/**
  	 * Exec the compilation of a preprocessor
  	 * @param   { string } type - preprocessor type either 'js', 'css' or 'template'
  	 * @param   { string } name - unique preprocessor id
  	 * @param   { Object } meta - preprocessor meta information
  	 * @param   { string } source - source code
  	 * @returns { Output } object containing a sourcemap and a code string
  	 */function execute$1(type,name,meta,source){if(!preprocessors[type])preprocessorTypeError(type);if(!preprocessors[type].has(name))preprocessorNameNotFoundError(name);return transform(preprocessors[type].get(name),meta,source);}/**
  	 * Similar to compose but performs from left-to-right function composition.<br/>
  	 * {@link https://30secondsofcode.org/function#composeright see also}
  	 * @param   {...[function]} fns) - list of unary function
  	 * @returns {*} result of the computation
  	 */ /**
  	 * Performs right-to-left function composition.<br/>
  	 * Use Array.prototype.reduce() to perform right-to-left function composition.<br/>
  	 * The last (rightmost) function can accept one or more arguments; the remaining functions must be unary.<br/>
  	 * {@link https://30secondsofcode.org/function#compose original source code}
  	 * @param   {...[function]} fns) - list of unary function
  	 * @returns {*} result of the computation
  	 */function compose(){for(var _len=arguments.length,fns=new Array(_len),_key=0;_key<_len;_key++){fns[_key]=arguments[_key];}return fns.reduce((f,g)=>function(){return f(g(...arguments));});}/*! https://mths.be/cssesc v3.0.0 by @mathias */var object={};var hasOwnProperty$1=object.hasOwnProperty;var merge=function merge(options,defaults){if(!options){return defaults;}var result={};for(var key in defaults){// `if (defaults.hasOwnProperty(key) { … }` is not needed here, since
  // only recognized option names are used.
  result[key]=hasOwnProperty$1.call(options,key)?options[key]:defaults[key];}return result;};var regexAnySingleEscape=/[ -,\.\/:-@\[-\^`\{-~]/;var regexSingleEscape=/[ -,\.\/:-@\[\]\^`\{-~]/;var regexExcessiveSpaces=/(^|\\+)?(\\[A-F0-9]{1,6})\x20(?![a-fA-F0-9\x20])/g;// https://mathiasbynens.be/notes/css-escapes#css
  var cssesc=function cssesc(string,options){options=merge(options,cssesc.options);if(options.quotes!='single'&&options.quotes!='double'){options.quotes='single';}var quote=options.quotes=='double'?'"':'\'';var isIdentifier=options.isIdentifier;var firstChar=string.charAt(0);var output='';var counter=0;var length=string.length;while(counter<length){var character=string.charAt(counter++);var codePoint=character.charCodeAt();var value=void 0;// If it’s not a printable ASCII character…
  if(codePoint<0x20||codePoint>0x7E){if(codePoint>=0xD800&&codePoint<=0xDBFF&&counter<length){// It’s a high surrogate, and there is a next character.
  var extra=string.charCodeAt(counter++);if((extra&0xFC00)==0xDC00){// next character is low surrogate
  codePoint=((codePoint&0x3FF)<<10)+(extra&0x3FF)+0x10000;}else{// It’s an unmatched surrogate; only append this code unit, in case
  // the next code unit is the high surrogate of a surrogate pair.
  counter--;}}value='\\'+codePoint.toString(16).toUpperCase()+' ';}else{if(options.escapeEverything){if(regexAnySingleEscape.test(character)){value='\\'+character;}else{value='\\'+codePoint.toString(16).toUpperCase()+' ';}}else if(/[\t\n\f\r\x0B]/.test(character)){value='\\'+codePoint.toString(16).toUpperCase()+' ';}else if(character=='\\'||!isIdentifier&&(character=='"'&&quote==character||character=='\''&&quote==character)||isIdentifier&&regexSingleEscape.test(character)){value='\\'+character;}else{value=character;}}output+=value;}if(isIdentifier){if(/^-[-\d]/.test(output)){output='\\-'+output.slice(1);}else if(/\d/.test(firstChar)){output='\\3'+firstChar+' '+output.slice(1);}}// Remove spaces after `\HEX` escapes that are not followed by a hex digit,
  // since they’re redundant. Note that this is only possible if the escape
  // sequence isn’t preceded by an odd number of backslashes.
  output=output.replace(regexExcessiveSpaces,function($0,$1,$2){if($1&&$1.length%2){// It’s not safe to remove the space, so don’t.
  return $0;}// Strip the space.
  return ($1||'')+$2;});if(!isIdentifier&&options.wrap){return quote+output+quote;}return output;};// Expose default options (so they can be overridden globally).
  cssesc.options={'escapeEverything':false,'isIdentifier':false,'quotes':'single','wrap':false};cssesc.version='3.0.0';var cssesc_1=cssesc;const ATTRIBUTE_TYPE_NAME='type';/**
  	 * Get the type attribute from a node generated by the riot parser
  	 * @param   { Object} sourceNode - riot parser node
  	 * @returns { string|null } a valid type to identify the preprocessor to use or nothing
  	 */function getPreprocessorTypeByAttribute(sourceNode){const typeAttribute=sourceNode.attributes?sourceNode.attributes.find(attribute=>attribute.name===ATTRIBUTE_TYPE_NAME):null;return typeAttribute?normalize(typeAttribute.value):null;}/**
  	 * Remove the noise in case a user has defined the preprocessor type='text/scss'
  	 * @param   { string } value - input string
  	 * @returns { string } normalized string
  	 */function normalize(value){return value.replace('text/','');}/**
  	 * Preprocess a riot parser node
  	 * @param   { string } preprocessorType - either css, js
  	 * @param   { string } preprocessorName - preprocessor id
  	 * @param   { Object } meta - compilation meta information
  	 * @param   { RiotParser.nodeTypes } node - css node detected by the parser
  	 * @returns { Output } code and sourcemap generated by the preprocessor
  	 */function preprocess(preprocessorType,preprocessorName,meta,node){const code=node.text;return preprocessorName?execute$1(preprocessorType,preprocessorName,meta,code):{code};}/**
  	 * Matches valid, multiline JavaScript comments in almost all its forms.
  	 * @const {RegExp}
  	 * @static
  	 */const R_MLCOMMS=/\/\*[^*]*\*+(?:[^*/][^*]*\*+)*\//g;/**
  	 * Source for creating regexes matching valid quoted, single-line JavaScript strings.
  	 * It recognizes escape characters, including nested quotes and line continuation.
  	 * @const {string}
  	 */const S_LINESTR=/"[^"\n\\]*(?:\\[\S\s][^"\n\\]*)*"|'[^'\n\\]*(?:\\[\S\s][^'\n\\]*)*'/.source;/**
  	 * Matches CSS selectors, excluding those beginning with '@' and quoted strings.
  	 * @const {RegExp}
  	 */const CSS_SELECTOR=RegExp(`([{}]|^)[; ]*((?:[^@ ;{}][^{}]*)?[^@ ;{}:] ?)(?={)|${S_LINESTR}`,'g');/**
  	 * Parses styles enclosed in a "scoped" tag
  	 * The "css" string is received without comments or surrounding spaces.
  	 *
  	 * @param   {string} tag - Tag name of the root element
  	 * @param   {string} css - The CSS code
  	 * @returns {string} CSS with the styles scoped to the root element
  	 */function scopedCSS(tag,css){const host=':host';const selectorsBlacklist=['from','to'];return css.replace(CSS_SELECTOR,function(m,p1,p2){// skip quoted strings
  if(!p2)return m;// we have a selector list, parse each individually
  p2=p2.replace(/[^,]+/g,function(sel){const s=sel.trim();// skip selectors already using the tag name
  if(s.indexOf(tag)===0){return sel;}// skips the keywords and percents of css animations
  if(!s||selectorsBlacklist.indexOf(s)>-1||s.slice(-1)==='%'){return sel;}// replace the `:host` pseudo-selector, where it is, with the root tag name;
  // if `:host` was not included, add the tag name as prefix, and mirror all
  // `[data-is]`
  if(s.indexOf(host)<0){return `${tag} ${s},[is="${tag}"] ${s}`;}else{return `${s.replace(host,tag)},${s.replace(host,`[is="${tag}"]`)}`;}});// add the danling bracket char and return the processed selector list
  return p1?`${p1} ${p2}`:p2;});}/**
  	 * Remove comments, compact and trim whitespace
  	 * @param {RiotParser.Node} cssNode - css node
  	 * @returns {RiotParser.Node} css node normalized
  	 */function compactCss(cssNode){return Object.assign({},cssNode,{text:cssNode.text.replace(R_MLCOMMS,'').replace(/\s+/g,' ').trim()});}const escapeIdentifier=identifier=>JSON.stringify(cssesc_1(identifier,{isIdentifier:true})).replace(/"$|^"/g,'');/**
  	 * Generate the component css
  	 * @param   { Object } sourceNode - node generated by the riot compiler
  	 * @param   { string } source - original component source code
  	 * @param   { Object } meta - compilation meta information
  	 * @param   { AST } ast - current AST output
  	 * @returns { AST } the AST generated
  	 */function css(sourceNode,source,meta,ast){const preprocessorName=getPreprocessorTypeByAttribute(sourceNode);const{options}=meta;const cssNode=compactCss(sourceNode.text);const preprocessorOutput=preprocess('css',preprocessorName,meta,cssNode);const escapedCssIdentifier=escapeIdentifier(meta.tagName);const cssCode=(options.scopedCss?scopedCSS(escapedCssIdentifier,cssesc_1(preprocessorOutput.code)):cssesc_1(preprocessorOutput.code)).trim();types$1.visit(ast,{visitProperty(path){if(path.value.key.value===TAG_CSS_PROPERTY){path.value.value=builders.templateLiteral([builders.templateElement({raw:cssCode,cooked:''},false)],[]);return false;}this.traverse(path);}});return ast;}/**
  	 * Function to curry any javascript method
  	 * @param   {Function}  fn - the target function we want to curry
  	 * @param   {...[args]} acc - initial arguments
  	 * @returns {Function|*} it will return a function until the target function
  	 *                       will receive all of its arguments
  	 */function curry(fn){for(var _len2=arguments.length,acc=new Array(_len2>1?_len2-1:0),_key2=1;_key2<_len2;_key2++){acc[_key2-1]=arguments[_key2];}return function(){for(var _len3=arguments.length,args=new Array(_len3),_key3=0;_key3<_len3;_key3++){args[_key3]=arguments[_key3];}args=[...acc,...args];return args.length<fn.length?curry(fn,...args):fn(...args);};}/**
  	 * Generate the javascript from an ast source
  	 * @param   {AST} ast - ast object
  	 * @param   {Object} options - printer options
  	 * @returns {Object} code + map
  	 */function generateJavascript(ast,options){return main_4$1(ast,Object.assign({},options,{tabWidth:2,quote:'single'}));}/**
  	 * True if the sourcemap has no mappings, it is empty
  	 * @param   {Object}  map - sourcemap json
  	 * @returns {boolean} true if empty
  	 */function isEmptySourcemap(map){return !map||!map.mappings||!map.mappings.length;}const LINES_RE=/\r\n?|\n/g;/**
  	 * Split a string into a rows array generated from its EOL matches
  	 * @param   { string } string [description]
  	 * @returns { Array } array containing all the string rows
  	 */function splitStringByEOL(string){return string.split(LINES_RE);}/**
  	 * Get the line and the column of a source text based on its position in the string
  	 * @param   { string } string - target string
  	 * @param   { number } position - target position
  	 * @returns { Object } object containing the source text line and column
  	 */function getLineAndColumnByPosition(string,position){const lines=splitStringByEOL(string.slice(0,position));return {line:lines.length,column:lines[lines.length-1].length};}/**
  	 * Add the offset to the code that must be parsed in order to generate properly the sourcemaps
  	 * @param {string} input - input string
  	 * @param {string} source - original source code
  	 * @param {RiotParser.Node} node - node that we are going to transform
  	 * @return {string} the input string with the offset properly set
  	 */function addLineOffset(input,source,node){const{column,line}=getLineAndColumnByPosition(source,node.start);return `${'\n'.repeat(line-1)}${' '.repeat(column+1)}${input}`;}// Reserved word lists for various dialects of the language
  var reservedWords={3:"abstract boolean byte char class double enum export extends final float goto implements import int interface long native package private protected public short static super synchronized throws transient volatile",5:"class enum extends super const export import",6:"enum",strict:"implements interface let package private protected public static yield",strictBind:"eval arguments"};// And the keywords
  var ecma5AndLessKeywords="break case catch continue debugger default do else finally for function if return switch throw try var while with null true false instanceof typeof void delete new in this";var keywords={5:ecma5AndLessKeywords,6:ecma5AndLessKeywords+" const class extends export import super"};var keywordRelationalOperator=/^in(stanceof)?$/;// ## Character categories
  // Big ugly regular expressions that match characters in the
  // whitespace, identifier, and identifier-start categories. These
  // are only applied when a character is found to actually have a
  // code point above 128.
  // Generated by `bin/generate-identifier-regex.js`.
  var nonASCIIidentifierStartChars="aab5bac0-d6d8-f6f8-\u02c1\u02c6-\u02d1\u02e0-\u02e4\u02ec\u02ee\u0370-\u0374\u0376\u0377\u037a-\u037d\u037f\u0386\u0388-\u038a\u038c\u038e-\u03a1\u03a3-\u03f5\u03f7-\u0481\u048a-\u052f\u0531-\u0556\u0559\u0560-\u0588\u05d0-\u05ea\u05ef-\u05f2\u0620-\u064a\u066e\u066f\u0671-\u06d3\u06d5\u06e5\u06e6\u06ee\u06ef\u06fa-\u06fc\u06ff\u0710\u0712-\u072f\u074d-\u07a5\u07b1\u07ca-\u07ea\u07f4\u07f5\u07fa\u0800-\u0815\u081a\u0824\u0828\u0840-\u0858\u0860-\u086a\u08a0-\u08b4\u08b6-\u08bd\u0904-\u0939\u093d\u0950\u0958-\u0961\u0971-\u0980\u0985-\u098c\u098f\u0990\u0993-\u09a8\u09aa-\u09b0\u09b2\u09b6-\u09b9\u09bd\u09ce\u09dc\u09dd\u09df-\u09e1\u09f0\u09f1\u09fc\u0a05-\u0a0a\u0a0f\u0a10\u0a13-\u0a28\u0a2a-\u0a30\u0a32\u0a33\u0a35\u0a36\u0a38\u0a39\u0a59-\u0a5c\u0a5e\u0a72-\u0a74\u0a85-\u0a8d\u0a8f-\u0a91\u0a93-\u0aa8\u0aaa-\u0ab0\u0ab2\u0ab3\u0ab5-\u0ab9\u0abd\u0ad0\u0ae0\u0ae1\u0af9\u0b05-\u0b0c\u0b0f\u0b10\u0b13-\u0b28\u0b2a-\u0b30\u0b32\u0b33\u0b35-\u0b39\u0b3d\u0b5c\u0b5d\u0b5f-\u0b61\u0b71\u0b83\u0b85-\u0b8a\u0b8e-\u0b90\u0b92-\u0b95\u0b99\u0b9a\u0b9c\u0b9e\u0b9f\u0ba3\u0ba4\u0ba8-\u0baa\u0bae-\u0bb9\u0bd0\u0c05-\u0c0c\u0c0e-\u0c10\u0c12-\u0c28\u0c2a-\u0c39\u0c3d\u0c58-\u0c5a\u0c60\u0c61\u0c80\u0c85-\u0c8c\u0c8e-\u0c90\u0c92-\u0ca8\u0caa-\u0cb3\u0cb5-\u0cb9\u0cbd\u0cde\u0ce0\u0ce1\u0cf1\u0cf2\u0d05-\u0d0c\u0d0e-\u0d10\u0d12-\u0d3a\u0d3d\u0d4e\u0d54-\u0d56\u0d5f-\u0d61\u0d7a-\u0d7f\u0d85-\u0d96\u0d9a-\u0db1\u0db3-\u0dbb\u0dbd\u0dc0-\u0dc6\u0e01-\u0e30\u0e32\u0e33\u0e40-\u0e46\u0e81\u0e82\u0e84\u0e86-\u0e8a\u0e8c-\u0ea3\u0ea5\u0ea7-\u0eb0\u0eb2\u0eb3\u0ebd\u0ec0-\u0ec4\u0ec6\u0edc-\u0edf\u0f00\u0f40-\u0f47\u0f49-\u0f6c\u0f88-\u0f8c\u1000-\u102a\u103f\u1050-\u1055\u105a-\u105d\u1061\u1065\u1066\u106e-\u1070\u1075-\u1081\u108e\u10a0-\u10c5\u10c7\u10cd\u10d0-\u10fa\u10fc-\u1248\u124a-\u124d\u1250-\u1256\u1258\u125a-\u125d\u1260-\u1288\u128a-\u128d\u1290-\u12b0\u12b2-\u12b5\u12b8-\u12be\u12c0\u12c2-\u12c5\u12c8-\u12d6\u12d8-\u1310\u1312-\u1315\u1318-\u135a\u1380-\u138f\u13a0-\u13f5\u13f8-\u13fd\u1401-\u166c\u166f-\u167f\u1681-\u169a\u16a0-\u16ea\u16ee-\u16f8\u1700-\u170c\u170e-\u1711\u1720-\u1731\u1740-\u1751\u1760-\u176c\u176e-\u1770\u1780-\u17b3\u17d7\u17dc\u1820-\u1878\u1880-\u18a8\u18aa\u18b0-\u18f5\u1900-\u191e\u1950-\u196d\u1970-\u1974\u1980-\u19ab\u19b0-\u19c9\u1a00-\u1a16\u1a20-\u1a54\u1aa7\u1b05-\u1b33\u1b45-\u1b4b\u1b83-\u1ba0\u1bae\u1baf\u1bba-\u1be5\u1c00-\u1c23\u1c4d-\u1c4f\u1c5a-\u1c7d\u1c80-\u1c88\u1c90-\u1cba\u1cbd-\u1cbf\u1ce9-\u1cec\u1cee-\u1cf3\u1cf5\u1cf6\u1cfa\u1d00-\u1dbf\u1e00-\u1f15\u1f18-\u1f1d\u1f20-\u1f45\u1f48-\u1f4d\u1f50-\u1f57\u1f59\u1f5b\u1f5d\u1f5f-\u1f7d\u1f80-\u1fb4\u1fb6-\u1fbc\u1fbe\u1fc2-\u1fc4\u1fc6-\u1fcc\u1fd0-\u1fd3\u1fd6-\u1fdb\u1fe0-\u1fec\u1ff2-\u1ff4\u1ff6-\u1ffc\u2071\u207f\u2090-\u209c\u2102\u2107\u210a-\u2113\u2115\u2118-\u211d\u2124\u2126\u2128\u212a-\u2139\u213c-\u213f\u2145-\u2149\u214e\u2160-\u2188\u2c00-\u2c2e\u2c30-\u2c5e\u2c60-\u2ce4\u2ceb-\u2cee\u2cf2\u2cf3\u2d00-\u2d25\u2d27\u2d2d\u2d30-\u2d67\u2d6f\u2d80-\u2d96\u2da0-\u2da6\u2da8-\u2dae\u2db0-\u2db6\u2db8-\u2dbe\u2dc0-\u2dc6\u2dc8-\u2dce\u2dd0-\u2dd6\u2dd8-\u2dde\u3005-\u3007\u3021-\u3029\u3031-\u3035\u3038-\u303c\u3041-\u3096\u309b-\u309f\u30a1-\u30fa\u30fc-\u30ff\u3105-\u312f\u3131-\u318e\u31a0-\u31ba\u31f0-\u31ff\u3400-\u4db5\u4e00-\u9fef\ua000-\ua48c\ua4d0-\ua4fd\ua500-\ua60c\ua610-\ua61f\ua62a\ua62b\ua640-\ua66e\ua67f-\ua69d\ua6a0-\ua6ef\ua717-\ua71f\ua722-\ua788\ua78b-\ua7bf\ua7c2-\ua7c6\ua7f7-\ua801\ua803-\ua805\ua807-\ua80a\ua80c-\ua822\ua840-\ua873\ua882-\ua8b3\ua8f2-\ua8f7\ua8fb\ua8fd\ua8fe\ua90a-\ua925\ua930-\ua946\ua960-\ua97c\ua984-\ua9b2\ua9cf\ua9e0-\ua9e4\ua9e6-\ua9ef\ua9fa-\ua9fe\uaa00-\uaa28\uaa40-\uaa42\uaa44-\uaa4b\uaa60-\uaa76\uaa7a\uaa7e-\uaaaf\uaab1\uaab5\uaab6\uaab9-\uaabd\uaac0\uaac2\uaadb-\uaadd\uaae0-\uaaea\uaaf2-\uaaf4\uab01-\uab06\uab09-\uab0e\uab11-\uab16\uab20-\uab26\uab28-\uab2e\uab30-\uab5a\uab5c-\uab67\uab70-\uabe2\uac00-\ud7a3\ud7b0-\ud7c6\ud7cb-\ud7fb\uf900-\ufa6d\ufa70-\ufad9\ufb00-\ufb06\ufb13-\ufb17\ufb1d\ufb1f-\ufb28\ufb2a-\ufb36\ufb38-\ufb3c\ufb3e\ufb40\ufb41\ufb43\ufb44\ufb46-\ufbb1\ufbd3-\ufd3d\ufd50-\ufd8f\ufd92-\ufdc7\ufdf0-\ufdfb\ufe70-\ufe74\ufe76-\ufefc\uff21-\uff3a\uff41-\uff5a\uff66-\uffbe\uffc2-\uffc7\uffca-\uffcf\uffd2-\uffd7\uffda-\uffdc";var nonASCIIidentifierChars="\u200c\u200db7\u0300-\u036f\u0387\u0483-\u0487\u0591-\u05bd\u05bf\u05c1\u05c2\u05c4\u05c5\u05c7\u0610-\u061a\u064b-\u0669\u0670\u06d6-\u06dc\u06df-\u06e4\u06e7\u06e8\u06ea-\u06ed\u06f0-\u06f9\u0711\u0730-\u074a\u07a6-\u07b0\u07c0-\u07c9\u07eb-\u07f3\u07fd\u0816-\u0819\u081b-\u0823\u0825-\u0827\u0829-\u082d\u0859-\u085b\u08d3-\u08e1\u08e3-\u0903\u093a-\u093c\u093e-\u094f\u0951-\u0957\u0962\u0963\u0966-\u096f\u0981-\u0983\u09bc\u09be-\u09c4\u09c7\u09c8\u09cb-\u09cd\u09d7\u09e2\u09e3\u09e6-\u09ef\u09fe\u0a01-\u0a03\u0a3c\u0a3e-\u0a42\u0a47\u0a48\u0a4b-\u0a4d\u0a51\u0a66-\u0a71\u0a75\u0a81-\u0a83\u0abc\u0abe-\u0ac5\u0ac7-\u0ac9\u0acb-\u0acd\u0ae2\u0ae3\u0ae6-\u0aef\u0afa-\u0aff\u0b01-\u0b03\u0b3c\u0b3e-\u0b44\u0b47\u0b48\u0b4b-\u0b4d\u0b56\u0b57\u0b62\u0b63\u0b66-\u0b6f\u0b82\u0bbe-\u0bc2\u0bc6-\u0bc8\u0bca-\u0bcd\u0bd7\u0be6-\u0bef\u0c00-\u0c04\u0c3e-\u0c44\u0c46-\u0c48\u0c4a-\u0c4d\u0c55\u0c56\u0c62\u0c63\u0c66-\u0c6f\u0c81-\u0c83\u0cbc\u0cbe-\u0cc4\u0cc6-\u0cc8\u0cca-\u0ccd\u0cd5\u0cd6\u0ce2\u0ce3\u0ce6-\u0cef\u0d00-\u0d03\u0d3b\u0d3c\u0d3e-\u0d44\u0d46-\u0d48\u0d4a-\u0d4d\u0d57\u0d62\u0d63\u0d66-\u0d6f\u0d82\u0d83\u0dca\u0dcf-\u0dd4\u0dd6\u0dd8-\u0ddf\u0de6-\u0def\u0df2\u0df3\u0e31\u0e34-\u0e3a\u0e47-\u0e4e\u0e50-\u0e59\u0eb1\u0eb4-\u0ebc\u0ec8-\u0ecd\u0ed0-\u0ed9\u0f18\u0f19\u0f20-\u0f29\u0f35\u0f37\u0f39\u0f3e\u0f3f\u0f71-\u0f84\u0f86\u0f87\u0f8d-\u0f97\u0f99-\u0fbc\u0fc6\u102b-\u103e\u1040-\u1049\u1056-\u1059\u105e-\u1060\u1062-\u1064\u1067-\u106d\u1071-\u1074\u1082-\u108d\u108f-\u109d\u135d-\u135f\u1369-\u1371\u1712-\u1714\u1732-\u1734\u1752\u1753\u1772\u1773\u17b4-\u17d3\u17dd\u17e0-\u17e9\u180b-\u180d\u1810-\u1819\u18a9\u1920-\u192b\u1930-\u193b\u1946-\u194f\u19d0-\u19da\u1a17-\u1a1b\u1a55-\u1a5e\u1a60-\u1a7c\u1a7f-\u1a89\u1a90-\u1a99\u1ab0-\u1abd\u1b00-\u1b04\u1b34-\u1b44\u1b50-\u1b59\u1b6b-\u1b73\u1b80-\u1b82\u1ba1-\u1bad\u1bb0-\u1bb9\u1be6-\u1bf3\u1c24-\u1c37\u1c40-\u1c49\u1c50-\u1c59\u1cd0-\u1cd2\u1cd4-\u1ce8\u1ced\u1cf4\u1cf7-\u1cf9\u1dc0-\u1df9\u1dfb-\u1dff\u203f\u2040\u2054\u20d0-\u20dc\u20e1\u20e5-\u20f0\u2cef-\u2cf1\u2d7f\u2de0-\u2dff\u302a-\u302f\u3099\u309a\ua620-\ua629\ua66f\ua674-\ua67d\ua69e\ua69f\ua6f0\ua6f1\ua802\ua806\ua80b\ua823-\ua827\ua880\ua881\ua8b4-\ua8c5\ua8d0-\ua8d9\ua8e0-\ua8f1\ua8ff-\ua909\ua926-\ua92d\ua947-\ua953\ua980-\ua983\ua9b3-\ua9c0\ua9d0-\ua9d9\ua9e5\ua9f0-\ua9f9\uaa29-\uaa36\uaa43\uaa4c\uaa4d\uaa50-\uaa59\uaa7b-\uaa7d\uaab0\uaab2-\uaab4\uaab7\uaab8\uaabe\uaabf\uaac1\uaaeb-\uaaef\uaaf5\uaaf6\uabe3-\uabea\uabec\uabed\uabf0-\uabf9\ufb1e\ufe00-\ufe0f\ufe20-\ufe2f\ufe33\ufe34\ufe4d-\ufe4f\uff10-\uff19\uff3f";var nonASCIIidentifierStart=new RegExp("["+nonASCIIidentifierStartChars+"]");var nonASCIIidentifier=new RegExp("["+nonASCIIidentifierStartChars+nonASCIIidentifierChars+"]");nonASCIIidentifierStartChars=nonASCIIidentifierChars=null;// These are a run-length and offset encoded representation of the
  // >0xffff code points that are a valid part of identifiers. The
  // offset starts at 0x10000, and each pair of numbers represents an
  // offset to the next range, and then a size of the range. They were
  // generated by bin/generate-identifier-regex.js
  // eslint-disable-next-line comma-spacing
  var astralIdentifierStartCodes=[0,11,2,25,2,18,2,1,2,14,3,13,35,122,70,52,268,28,4,48,48,31,14,29,6,37,11,29,3,35,5,7,2,4,43,157,19,35,5,35,5,39,9,51,157,310,10,21,11,7,153,5,3,0,2,43,2,1,4,0,3,22,11,22,10,30,66,18,2,1,11,21,11,25,71,55,7,1,65,0,16,3,2,2,2,28,43,28,4,28,36,7,2,27,28,53,11,21,11,18,14,17,111,72,56,50,14,50,14,35,477,28,11,0,9,21,155,22,13,52,76,44,33,24,27,35,30,0,12,34,4,0,13,47,15,3,22,0,2,0,36,17,2,24,85,6,2,0,2,3,2,14,2,9,8,46,39,7,3,1,3,21,2,6,2,1,2,4,4,0,19,0,13,4,159,52,19,3,21,0,33,47,21,1,2,0,185,46,42,3,37,47,21,0,60,42,14,0,72,26,230,43,117,63,32,0,161,7,3,38,17,0,2,0,29,0,11,39,8,0,22,0,12,45,20,0,35,56,264,8,2,36,18,0,50,29,113,6,2,1,2,37,22,0,26,5,2,1,2,31,15,0,328,18,270,921,103,110,18,195,2749,1070,4050,582,8634,568,8,30,114,29,19,47,17,3,32,20,6,18,689,63,129,74,6,0,67,12,65,1,2,0,29,6135,9,754,9486,286,50,2,18,3,9,395,2309,106,6,12,4,8,8,9,5991,84,2,70,2,1,3,0,3,1,3,3,2,11,2,0,2,6,2,64,2,3,3,7,2,6,2,27,2,3,2,4,2,0,4,6,2,339,3,24,2,24,2,30,2,24,2,30,2,24,2,30,2,24,2,30,2,24,2,7,2357,44,11,6,17,0,370,43,1301,196,60,67,8,0,1205,3,2,26,2,1,2,0,3,0,2,9,2,3,2,0,2,0,7,0,5,0,2,0,2,0,2,2,2,1,2,0,3,0,2,0,2,0,2,0,2,0,2,1,2,0,3,3,2,6,2,3,2,3,2,0,2,9,2,16,6,2,2,4,2,16,4421,42710,42,4148,12,221,3,5761,15,7472,3104,541];// eslint-disable-next-line comma-spacing
  var astralIdentifierCodes=[509,0,227,0,150,4,294,9,1368,2,2,1,6,3,41,2,5,0,166,1,574,3,9,9,525,10,176,2,54,14,32,9,16,3,46,10,54,9,7,2,37,13,2,9,6,1,45,0,13,2,49,13,9,3,4,9,83,11,7,0,161,11,6,9,7,3,56,1,2,6,3,1,3,2,10,0,11,1,3,6,4,4,193,17,10,9,5,0,82,19,13,9,214,6,3,8,28,1,83,16,16,9,82,12,9,9,84,14,5,9,243,14,166,9,232,6,3,6,4,0,29,9,41,6,2,3,9,0,10,10,47,15,406,7,2,7,17,9,57,21,2,13,123,5,4,0,2,1,2,6,2,0,9,9,49,4,2,1,2,4,9,9,330,3,19306,9,135,4,60,6,26,9,1014,0,2,54,8,3,19723,1,5319,4,4,5,9,7,3,6,31,3,149,2,1418,49,513,54,5,49,9,0,15,0,23,4,2,14,1361,6,2,16,3,6,2,1,2,4,262,6,10,9,419,13,1495,6,110,6,6,9,792487,239];// This has a complexity linear to the value of the code. The
  // assumption is that looking up astral identifier characters is
  // rare.
  function isInAstralSet(code,set){var pos=0x10000;for(var i=0;i<set.length;i+=2){pos+=set[i];if(pos>code){return false;}pos+=set[i+1];if(pos>=code){return true;}}}// Test whether a given character code starts an identifier.
  function isIdentifierStart(code,astral){if(code<65){return code===36;}if(code<91){return true;}if(code<97){return code===95;}if(code<123){return true;}if(code<=0xffff){return code>=0xaa&&nonASCIIidentifierStart.test(String.fromCharCode(code));}if(astral===false){return false;}return isInAstralSet(code,astralIdentifierStartCodes);}// Test whether a given character is part of an identifier.
  function isIdentifierChar(code,astral){if(code<48){return code===36;}if(code<58){return true;}if(code<65){return false;}if(code<91){return true;}if(code<97){return code===95;}if(code<123){return true;}if(code<=0xffff){return code>=0xaa&&nonASCIIidentifier.test(String.fromCharCode(code));}if(astral===false){return false;}return isInAstralSet(code,astralIdentifierStartCodes)||isInAstralSet(code,astralIdentifierCodes);}// ## Token types
  // The assignment of fine-grained, information-carrying type objects
  // allows the tokenizer to store the information it has about a
  // token in a way that is very cheap for the parser to look up.
  // All token type variables start with an underscore, to make them
  // easy to recognize.
  // The `beforeExpr` property is used to disambiguate between regular
  // expressions and divisions. It is set on all token types that can
  // be followed by an expression (thus, a slash after them would be a
  // regular expression).
  //
  // The `startsExpr` property is used to check if the token ends a
  // `yield` expression. It is set on all token types that either can
  // directly start an expression (like a quotation mark) or can
  // continue an expression (like the body of a string).
  //
  // `isLoop` marks a keyword as starting a loop, which is important
  // to know when parsing a label, in order to allow or disallow
  // continue jumps to that label.
  var TokenType=function TokenType(label,conf){if(conf===void 0)conf={};this.label=label;this.keyword=conf.keyword;this.beforeExpr=!!conf.beforeExpr;this.startsExpr=!!conf.startsExpr;this.isLoop=!!conf.isLoop;this.isAssign=!!conf.isAssign;this.prefix=!!conf.prefix;this.postfix=!!conf.postfix;this.binop=conf.binop||null;this.updateContext=null;};function binop(name,prec){return new TokenType(name,{beforeExpr:true,binop:prec});}var beforeExpr={beforeExpr:true},startsExpr={startsExpr:true};// Map keyword names to token types.
  var keywords$1={};// Succinct definitions of keyword token types
  function kw(name,options){if(options===void 0)options={};options.keyword=name;return keywords$1[name]=new TokenType(name,options);}var types$2={num:new TokenType("num",startsExpr),regexp:new TokenType("regexp",startsExpr),string:new TokenType("string",startsExpr),name:new TokenType("name",startsExpr),eof:new TokenType("eof"),// Punctuation token types.
  bracketL:new TokenType("[",{beforeExpr:true,startsExpr:true}),bracketR:new TokenType("]"),braceL:new TokenType("{",{beforeExpr:true,startsExpr:true}),braceR:new TokenType("}"),parenL:new TokenType("(",{beforeExpr:true,startsExpr:true}),parenR:new TokenType(")"),comma:new TokenType(",",beforeExpr),semi:new TokenType(";",beforeExpr),colon:new TokenType(":",beforeExpr),dot:new TokenType("."),question:new TokenType("?",beforeExpr),arrow:new TokenType("=>",beforeExpr),template:new TokenType("template"),invalidTemplate:new TokenType("invalidTemplate"),ellipsis:new TokenType("...",beforeExpr),backQuote:new TokenType("`",startsExpr),dollarBraceL:new TokenType("${",{beforeExpr:true,startsExpr:true}),// Operators. These carry several kinds of properties to help the
  // parser use them properly (the presence of these properties is
  // what categorizes them as operators).
  //
  // `binop`, when present, specifies that this operator is a binary
  // operator, and will refer to its precedence.
  //
  // `prefix` and `postfix` mark the operator as a prefix or postfix
  // unary operator.
  //
  // `isAssign` marks all of `=`, `+=`, `-=` etcetera, which act as
  // binary operators with a very low precedence, that should result
  // in AssignmentExpression nodes.
  eq:new TokenType("=",{beforeExpr:true,isAssign:true}),assign:new TokenType("_=",{beforeExpr:true,isAssign:true}),incDec:new TokenType("++/--",{prefix:true,postfix:true,startsExpr:true}),prefix:new TokenType("!/~",{beforeExpr:true,prefix:true,startsExpr:true}),logicalOR:binop("||",1),logicalAND:binop("&&",2),bitwiseOR:binop("|",3),bitwiseXOR:binop("^",4),bitwiseAND:binop("&",5),equality:binop("==/!=/===/!==",6),relational:binop("</>/<=/>=",7),bitShift:binop("<</>>/>>>",8),plusMin:new TokenType("+/-",{beforeExpr:true,binop:9,prefix:true,startsExpr:true}),modulo:binop("%",10),star:binop("*",10),slash:binop("/",10),starstar:new TokenType("**",{beforeExpr:true}),// Keyword token types.
  _break:kw("break"),_case:kw("case",beforeExpr),_catch:kw("catch"),_continue:kw("continue"),_debugger:kw("debugger"),_default:kw("default",beforeExpr),_do:kw("do",{isLoop:true,beforeExpr:true}),_else:kw("else",beforeExpr),_finally:kw("finally"),_for:kw("for",{isLoop:true}),_function:kw("function",startsExpr),_if:kw("if"),_return:kw("return",beforeExpr),_switch:kw("switch"),_throw:kw("throw",beforeExpr),_try:kw("try"),_var:kw("var"),_const:kw("const"),_while:kw("while",{isLoop:true}),_with:kw("with"),_new:kw("new",{beforeExpr:true,startsExpr:true}),_this:kw("this",startsExpr),_super:kw("super",startsExpr),_class:kw("class",startsExpr),_extends:kw("extends",beforeExpr),_export:kw("export"),_import:kw("import",startsExpr),_null:kw("null",startsExpr),_true:kw("true",startsExpr),_false:kw("false",startsExpr),_in:kw("in",{beforeExpr:true,binop:7}),_instanceof:kw("instanceof",{beforeExpr:true,binop:7}),_typeof:kw("typeof",{beforeExpr:true,prefix:true,startsExpr:true}),_void:kw("void",{beforeExpr:true,prefix:true,startsExpr:true}),_delete:kw("delete",{beforeExpr:true,prefix:true,startsExpr:true})};// Matches a whole line break (where CRLF is considered a single
  // line break). Used to count lines.
  var lineBreak=/\r\n?|\n|\u2028|\u2029/;var lineBreakG=new RegExp(lineBreak.source,"g");function isNewLine(code,ecma2019String){return code===10||code===13||!ecma2019String&&(code===0x2028||code===0x2029);}var nonASCIIwhitespace=/[\u1680\u2000-\u200a\u202f\u205f\u3000\ufeff]/;var skipWhiteSpace=/(?:\s|\/\/.*|\/\*[^]*?\*\/)*/g;var ref=Object.prototype;var hasOwnProperty$2=ref.hasOwnProperty;var toString$1=ref.toString;// Checks if an object has a property.
  function has(obj,propName){return hasOwnProperty$2.call(obj,propName);}var isArray$2=Array.isArray||function(obj){return toString$1.call(obj)==="[object Array]";};function wordsRegexp(words){return new RegExp("^(?:"+words.replace(/ /g,"|")+")$");}// These are used when `options.locations` is on, for the
  // `startLoc` and `endLoc` properties.
  var Position=function Position(line,col){this.line=line;this.column=col;};Position.prototype.offset=function offset(n){return new Position(this.line,this.column+n);};var SourceLocation=function SourceLocation(p,start,end){this.start=start;this.end=end;if(p.sourceFile!==null){this.source=p.sourceFile;}};// The `getLineInfo` function is mostly useful when the
  // `locations` option is off (for performance reasons) and you
  // want to find the line/column position for a given character
  // offset. `input` should be the code string that the offset refers
  // into.
  function getLineInfo(input,offset){for(var line=1,cur=0;;){lineBreakG.lastIndex=cur;var match=lineBreakG.exec(input);if(match&&match.index<offset){++line;cur=match.index+match[0].length;}else{return new Position(line,offset-cur);}}}// A second optional argument can be given to further configure
  // the parser process. These options are recognized:
  var defaultOptions={// `ecmaVersion` indicates the ECMAScript version to parse. Must be
  // either 3, 5, 6 (2015), 7 (2016), 8 (2017), 9 (2018), or 10
  // (2019). This influences support for strict mode, the set of
  // reserved words, and support for new syntax features. The default
  // is 9.
  ecmaVersion:9,// `sourceType` indicates the mode the code should be parsed in.
  // Can be either `"script"` or `"module"`. This influences global
  // strict mode and parsing of `import` and `export` declarations.
  sourceType:"script",// `onInsertedSemicolon` can be a callback that will be called
  // when a semicolon is automatically inserted. It will be passed
  // the position of the comma as an offset, and if `locations` is
  // enabled, it is given the location as a `{line, column}` object
  // as second argument.
  onInsertedSemicolon:null,// `onTrailingComma` is similar to `onInsertedSemicolon`, but for
  // trailing commas.
  onTrailingComma:null,// By default, reserved words are only enforced if ecmaVersion >= 5.
  // Set `allowReserved` to a boolean value to explicitly turn this on
  // an off. When this option has the value "never", reserved words
  // and keywords can also not be used as property names.
  allowReserved:null,// When enabled, a return at the top level is not considered an
  // error.
  allowReturnOutsideFunction:false,// When enabled, import/export statements are not constrained to
  // appearing at the top of the program.
  allowImportExportEverywhere:false,// When enabled, await identifiers are allowed to appear at the top-level scope,
  // but they are still not allowed in non-async functions.
  allowAwaitOutsideFunction:false,// When enabled, hashbang directive in the beginning of file
  // is allowed and treated as a line comment.
  allowHashBang:false,// When `locations` is on, `loc` properties holding objects with
  // `start` and `end` properties in `{line, column}` form (with
  // line being 1-based and column 0-based) will be attached to the
  // nodes.
  locations:false,// A function can be passed as `onToken` option, which will
  // cause Acorn to call that function with object in the same
  // format as tokens returned from `tokenizer().getToken()`. Note
  // that you are not allowed to call the parser from the
  // callback—that will corrupt its internal state.
  onToken:null,// A function can be passed as `onComment` option, which will
  // cause Acorn to call that function with `(block, text, start,
  // end)` parameters whenever a comment is skipped. `block` is a
  // boolean indicating whether this is a block (`/* */`) comment,
  // `text` is the content of the comment, and `start` and `end` are
  // character offsets that denote the start and end of the comment.
  // When the `locations` option is on, two more parameters are
  // passed, the full `{line, column}` locations of the start and
  // end of the comments. Note that you are not allowed to call the
  // parser from the callback—that will corrupt its internal state.
  onComment:null,// Nodes have their start and end characters offsets recorded in
  // `start` and `end` properties (directly on the node, rather than
  // the `loc` object, which holds line/column data. To also add a
  // [semi-standardized][range] `range` property holding a `[start,
  // end]` array with the same numbers, set the `ranges` option to
  // `true`.
  //
  // [range]: https://bugzilla.mozilla.org/show_bug.cgi?id=745678
  ranges:false,// It is possible to parse multiple files into a single AST by
  // passing the tree produced by parsing the first file as
  // `program` option in subsequent parses. This will add the
  // toplevel forms of the parsed file to the `Program` (top) node
  // of an existing parse tree.
  program:null,// When `locations` is on, you can pass this to record the source
  // file in every node's `loc` object.
  sourceFile:null,// This value, if given, is stored in every node, whether
  // `locations` is on or off.
  directSourceFile:null,// When enabled, parenthesized expressions are represented by
  // (non-standard) ParenthesizedExpression nodes
  preserveParens:false};// Interpret and default an options object
  function getOptions(opts){var options={};for(var opt in defaultOptions){options[opt]=opts&&has(opts,opt)?opts[opt]:defaultOptions[opt];}if(options.ecmaVersion>=2015){options.ecmaVersion-=2009;}if(options.allowReserved==null){options.allowReserved=options.ecmaVersion<5;}if(isArray$2(options.onToken)){var tokens=options.onToken;options.onToken=function(token){return tokens.push(token);};}if(isArray$2(options.onComment)){options.onComment=pushComment(options,options.onComment);}return options;}function pushComment(options,array){return function(block,text,start,end,startLoc,endLoc){var comment={type:block?"Block":"Line",value:text,start:start,end:end};if(options.locations){comment.loc=new SourceLocation(this,startLoc,endLoc);}if(options.ranges){comment.range=[start,end];}array.push(comment);};}// Each scope gets a bitset that may contain these flags
  var SCOPE_TOP=1,SCOPE_FUNCTION=2,SCOPE_VAR=SCOPE_TOP|SCOPE_FUNCTION,SCOPE_ASYNC=4,SCOPE_GENERATOR=8,SCOPE_ARROW=16,SCOPE_SIMPLE_CATCH=32,SCOPE_SUPER=64,SCOPE_DIRECT_SUPER=128;function functionFlags(async,generator){return SCOPE_FUNCTION|(async?SCOPE_ASYNC:0)|(generator?SCOPE_GENERATOR:0);}// Used in checkLVal and declareName to determine the type of a binding
  var BIND_NONE=0,// Not a binding
  BIND_VAR=1,// Var-style binding
  BIND_LEXICAL=2,// Let- or const-style binding
  BIND_FUNCTION=3,// Function declaration
  BIND_SIMPLE_CATCH=4,// Simple (identifier pattern) catch binding
  BIND_OUTSIDE=5;// Special case for function names as bound inside the function
  var Parser=function Parser(options,input,startPos){this.options=options=getOptions(options);this.sourceFile=options.sourceFile;this.keywords=wordsRegexp(keywords[options.ecmaVersion>=6?6:5]);var reserved="";if(!options.allowReserved){for(var v=options.ecmaVersion;;v--){if(reserved=reservedWords[v]){break;}}if(options.sourceType==="module"){reserved+=" await";}}this.reservedWords=wordsRegexp(reserved);var reservedStrict=(reserved?reserved+" ":"")+reservedWords.strict;this.reservedWordsStrict=wordsRegexp(reservedStrict);this.reservedWordsStrictBind=wordsRegexp(reservedStrict+" "+reservedWords.strictBind);this.input=String(input);// Used to signal to callers of `readWord1` whether the word
  // contained any escape sequences. This is needed because words with
  // escape sequences must not be interpreted as keywords.
  this.containsEsc=false;// Set up token state
  // The current position of the tokenizer in the input.
  if(startPos){this.pos=startPos;this.lineStart=this.input.lastIndexOf("\n",startPos-1)+1;this.curLine=this.input.slice(0,this.lineStart).split(lineBreak).length;}else{this.pos=this.lineStart=0;this.curLine=1;}// Properties of the current token:
  // Its type
  this.type=types$2.eof;// For tokens that include more information than their type, the value
  this.value=null;// Its start and end offset
  this.start=this.end=this.pos;// And, if locations are used, the {line, column} object
  // corresponding to those offsets
  this.startLoc=this.endLoc=this.curPosition();// Position information for the previous token
  this.lastTokEndLoc=this.lastTokStartLoc=null;this.lastTokStart=this.lastTokEnd=this.pos;// The context stack is used to superficially track syntactic
  // context to predict whether a regular expression is allowed in a
  // given position.
  this.context=this.initialContext();this.exprAllowed=true;// Figure out if it's a module code.
  this.inModule=options.sourceType==="module";this.strict=this.inModule||this.strictDirective(this.pos);// Used to signify the start of a potential arrow function
  this.potentialArrowAt=-1;// Positions to delayed-check that yield/await does not exist in default parameters.
  this.yieldPos=this.awaitPos=this.awaitIdentPos=0;// Labels in scope.
  this.labels=[];// Thus-far undefined exports.
  this.undefinedExports={};// If enabled, skip leading hashbang line.
  if(this.pos===0&&options.allowHashBang&&this.input.slice(0,2)==="#!"){this.skipLineComment(2);}// Scope tracking for duplicate variable names (see scope.js)
  this.scopeStack=[];this.enterScope(SCOPE_TOP);// For RegExp validation
  this.regexpState=null;};var prototypeAccessors={inFunction:{configurable:true},inGenerator:{configurable:true},inAsync:{configurable:true},allowSuper:{configurable:true},allowDirectSuper:{configurable:true},treatFunctionsAsVar:{configurable:true}};Parser.prototype.parse=function parse(){var node=this.options.program||this.startNode();this.nextToken();return this.parseTopLevel(node);};prototypeAccessors.inFunction.get=function(){return (this.currentVarScope().flags&SCOPE_FUNCTION)>0;};prototypeAccessors.inGenerator.get=function(){return (this.currentVarScope().flags&SCOPE_GENERATOR)>0;};prototypeAccessors.inAsync.get=function(){return (this.currentVarScope().flags&SCOPE_ASYNC)>0;};prototypeAccessors.allowSuper.get=function(){return (this.currentThisScope().flags&SCOPE_SUPER)>0;};prototypeAccessors.allowDirectSuper.get=function(){return (this.currentThisScope().flags&SCOPE_DIRECT_SUPER)>0;};prototypeAccessors.treatFunctionsAsVar.get=function(){return this.treatFunctionsAsVarInScope(this.currentScope());};// Switch to a getter for 7.0.0.
  Parser.prototype.inNonArrowFunction=function inNonArrowFunction(){return (this.currentThisScope().flags&SCOPE_FUNCTION)>0;};Parser.extend=function extend(){var plugins=[],len=arguments.length;while(len--)plugins[len]=arguments[len];var cls=this;for(var i=0;i<plugins.length;i++){cls=plugins[i](cls);}return cls;};Parser.parse=function parse(input,options){return new this(options,input).parse();};Parser.parseExpressionAt=function parseExpressionAt(input,pos,options){var parser=new this(options,input,pos);parser.nextToken();return parser.parseExpression();};Parser.tokenizer=function tokenizer(input,options){return new this(options,input);};Object.defineProperties(Parser.prototype,prototypeAccessors);var pp=Parser.prototype;// ## Parser utilities
  var literal=/^(?:'((?:\\.|[^'])*?)'|"((?:\\.|[^"])*?)")/;pp.strictDirective=function(start){for(;;){// Try to find string literal.
  skipWhiteSpace.lastIndex=start;start+=skipWhiteSpace.exec(this.input)[0].length;var match=literal.exec(this.input.slice(start));if(!match){return false;}if((match[1]||match[2])==="use strict"){return true;}start+=match[0].length;// Skip semicolon, if any.
  skipWhiteSpace.lastIndex=start;start+=skipWhiteSpace.exec(this.input)[0].length;if(this.input[start]===";"){start++;}}};// Predicate that tests whether the next token is of the given
  // type, and if yes, consumes it as a side effect.
  pp.eat=function(type){if(this.type===type){this.next();return true;}else{return false;}};// Tests whether parsed token is a contextual keyword.
  pp.isContextual=function(name){return this.type===types$2.name&&this.value===name&&!this.containsEsc;};// Consumes contextual keyword if possible.
  pp.eatContextual=function(name){if(!this.isContextual(name)){return false;}this.next();return true;};// Asserts that following token is given contextual keyword.
  pp.expectContextual=function(name){if(!this.eatContextual(name)){this.unexpected();}};// Test whether a semicolon can be inserted at the current position.
  pp.canInsertSemicolon=function(){return this.type===types$2.eof||this.type===types$2.braceR||lineBreak.test(this.input.slice(this.lastTokEnd,this.start));};pp.insertSemicolon=function(){if(this.canInsertSemicolon()){if(this.options.onInsertedSemicolon){this.options.onInsertedSemicolon(this.lastTokEnd,this.lastTokEndLoc);}return true;}};// Consume a semicolon, or, failing that, see if we are allowed to
  // pretend that there is a semicolon at this position.
  pp.semicolon=function(){if(!this.eat(types$2.semi)&&!this.insertSemicolon()){this.unexpected();}};pp.afterTrailingComma=function(tokType,notNext){if(this.type===tokType){if(this.options.onTrailingComma){this.options.onTrailingComma(this.lastTokStart,this.lastTokStartLoc);}if(!notNext){this.next();}return true;}};// Expect a token of a given type. If found, consume it, otherwise,
  // raise an unexpected token error.
  pp.expect=function(type){this.eat(type)||this.unexpected();};// Raise an unexpected token error.
  pp.unexpected=function(pos){this.raise(pos!=null?pos:this.start,"Unexpected token");};function DestructuringErrors(){this.shorthandAssign=this.trailingComma=this.parenthesizedAssign=this.parenthesizedBind=this.doubleProto=-1;}pp.checkPatternErrors=function(refDestructuringErrors,isAssign){if(!refDestructuringErrors){return;}if(refDestructuringErrors.trailingComma>-1){this.raiseRecoverable(refDestructuringErrors.trailingComma,"Comma is not permitted after the rest element");}var parens=isAssign?refDestructuringErrors.parenthesizedAssign:refDestructuringErrors.parenthesizedBind;if(parens>-1){this.raiseRecoverable(parens,"Parenthesized pattern");}};pp.checkExpressionErrors=function(refDestructuringErrors,andThrow){if(!refDestructuringErrors){return false;}var shorthandAssign=refDestructuringErrors.shorthandAssign;var doubleProto=refDestructuringErrors.doubleProto;if(!andThrow){return shorthandAssign>=0||doubleProto>=0;}if(shorthandAssign>=0){this.raise(shorthandAssign,"Shorthand property assignments are valid only in destructuring patterns");}if(doubleProto>=0){this.raiseRecoverable(doubleProto,"Redefinition of __proto__ property");}};pp.checkYieldAwaitInDefaultParams=function(){if(this.yieldPos&&(!this.awaitPos||this.yieldPos<this.awaitPos)){this.raise(this.yieldPos,"Yield expression cannot be a default value");}if(this.awaitPos){this.raise(this.awaitPos,"Await expression cannot be a default value");}};pp.isSimpleAssignTarget=function(expr){if(expr.type==="ParenthesizedExpression"){return this.isSimpleAssignTarget(expr.expression);}return expr.type==="Identifier"||expr.type==="MemberExpression";};var pp$1=Parser.prototype;// ### Statement parsing
  // Parse a program. Initializes the parser, reads any number of
  // statements, and wraps them in a Program node.  Optionally takes a
  // `program` argument.  If present, the statements will be appended
  // to its body instead of creating a new node.
  pp$1.parseTopLevel=function(node){var exports={};if(!node.body){node.body=[];}while(this.type!==types$2.eof){var stmt=this.parseStatement(null,true,exports);node.body.push(stmt);}if(this.inModule){for(var i=0,list=Object.keys(this.undefinedExports);i<list.length;i+=1){var name=list[i];this.raiseRecoverable(this.undefinedExports[name].start,"Export '"+name+"' is not defined");}}this.adaptDirectivePrologue(node.body);this.next();if(this.options.ecmaVersion>=6){node.sourceType=this.options.sourceType;}return this.finishNode(node,"Program");};var loopLabel={kind:"loop"},switchLabel={kind:"switch"};pp$1.isLet=function(context){if(this.options.ecmaVersion<6||!this.isContextual("let")){return false;}skipWhiteSpace.lastIndex=this.pos;var skip=skipWhiteSpace.exec(this.input);var next=this.pos+skip[0].length,nextCh=this.input.charCodeAt(next);// For ambiguous cases, determine if a LexicalDeclaration (or only a
  // Statement) is allowed here. If context is not empty then only a Statement
  // is allowed. However, `let [` is an explicit negative lookahead for
  // ExpressionStatement, so special-case it first.
  if(nextCh===91){return true;}// '['
  if(context){return false;}if(nextCh===123){return true;}// '{'
  if(isIdentifierStart(nextCh,true)){var pos=next+1;while(isIdentifierChar(this.input.charCodeAt(pos),true)){++pos;}var ident=this.input.slice(next,pos);if(!keywordRelationalOperator.test(ident)){return true;}}return false;};// check 'async [no LineTerminator here] function'
  // - 'async /*foo*/ function' is OK.
  // - 'async /*\n*/ function' is invalid.
  pp$1.isAsyncFunction=function(){if(this.options.ecmaVersion<8||!this.isContextual("async")){return false;}skipWhiteSpace.lastIndex=this.pos;var skip=skipWhiteSpace.exec(this.input);var next=this.pos+skip[0].length;return !lineBreak.test(this.input.slice(this.pos,next))&&this.input.slice(next,next+8)==="function"&&(next+8===this.input.length||!isIdentifierChar(this.input.charAt(next+8)));};// Parse a single statement.
  //
  // If expecting a statement and finding a slash operator, parse a
  // regular expression literal. This is to handle cases like
  // `if (foo) /blah/.exec(foo)`, where looking at the previous token
  // does not help.
  pp$1.parseStatement=function(context,topLevel,exports){var starttype=this.type,node=this.startNode(),kind;if(this.isLet(context)){starttype=types$2._var;kind="let";}// Most types of statements are recognized by the keyword they
  // start with. Many are trivial to parse, some require a bit of
  // complexity.
  switch(starttype){case types$2._break:case types$2._continue:return this.parseBreakContinueStatement(node,starttype.keyword);case types$2._debugger:return this.parseDebuggerStatement(node);case types$2._do:return this.parseDoStatement(node);case types$2._for:return this.parseForStatement(node);case types$2._function:// Function as sole body of either an if statement or a labeled statement
  // works, but not when it is part of a labeled statement that is the sole
  // body of an if statement.
  if(context&&(this.strict||context!=="if"&&context!=="label")&&this.options.ecmaVersion>=6){this.unexpected();}return this.parseFunctionStatement(node,false,!context);case types$2._class:if(context){this.unexpected();}return this.parseClass(node,true);case types$2._if:return this.parseIfStatement(node);case types$2._return:return this.parseReturnStatement(node);case types$2._switch:return this.parseSwitchStatement(node);case types$2._throw:return this.parseThrowStatement(node);case types$2._try:return this.parseTryStatement(node);case types$2._const:case types$2._var:kind=kind||this.value;if(context&&kind!=="var"){this.unexpected();}return this.parseVarStatement(node,kind);case types$2._while:return this.parseWhileStatement(node);case types$2._with:return this.parseWithStatement(node);case types$2.braceL:return this.parseBlock(true,node);case types$2.semi:return this.parseEmptyStatement(node);case types$2._export:case types$2._import:if(this.options.ecmaVersion>10&&starttype===types$2._import){skipWhiteSpace.lastIndex=this.pos;var skip=skipWhiteSpace.exec(this.input);var next=this.pos+skip[0].length,nextCh=this.input.charCodeAt(next);if(nextCh===40)// '('
  {return this.parseExpressionStatement(node,this.parseExpression());}}if(!this.options.allowImportExportEverywhere){if(!topLevel){this.raise(this.start,"'import' and 'export' may only appear at the top level");}if(!this.inModule){this.raise(this.start,"'import' and 'export' may appear only with 'sourceType: module'");}}return starttype===types$2._import?this.parseImport(node):this.parseExport(node,exports);// If the statement does not start with a statement keyword or a
  // brace, it's an ExpressionStatement or LabeledStatement. We
  // simply start parsing an expression, and afterwards, if the
  // next token is a colon and the expression was a simple
  // Identifier node, we switch to interpreting it as a label.
  default:if(this.isAsyncFunction()){if(context){this.unexpected();}this.next();return this.parseFunctionStatement(node,true,!context);}var maybeName=this.value,expr=this.parseExpression();if(starttype===types$2.name&&expr.type==="Identifier"&&this.eat(types$2.colon)){return this.parseLabeledStatement(node,maybeName,expr,context);}else{return this.parseExpressionStatement(node,expr);}}};pp$1.parseBreakContinueStatement=function(node,keyword){var isBreak=keyword==="break";this.next();if(this.eat(types$2.semi)||this.insertSemicolon()){node.label=null;}else if(this.type!==types$2.name){this.unexpected();}else{node.label=this.parseIdent();this.semicolon();}// Verify that there is an actual destination to break or
  // continue to.
  var i=0;for(;i<this.labels.length;++i){var lab=this.labels[i];if(node.label==null||lab.name===node.label.name){if(lab.kind!=null&&(isBreak||lab.kind==="loop")){break;}if(node.label&&isBreak){break;}}}if(i===this.labels.length){this.raise(node.start,"Unsyntactic "+keyword);}return this.finishNode(node,isBreak?"BreakStatement":"ContinueStatement");};pp$1.parseDebuggerStatement=function(node){this.next();this.semicolon();return this.finishNode(node,"DebuggerStatement");};pp$1.parseDoStatement=function(node){this.next();this.labels.push(loopLabel);node.body=this.parseStatement("do");this.labels.pop();this.expect(types$2._while);node.test=this.parseParenExpression();if(this.options.ecmaVersion>=6){this.eat(types$2.semi);}else{this.semicolon();}return this.finishNode(node,"DoWhileStatement");};// Disambiguating between a `for` and a `for`/`in` or `for`/`of`
  // loop is non-trivial. Basically, we have to parse the init `var`
  // statement or expression, disallowing the `in` operator (see
  // the second parameter to `parseExpression`), and then check
  // whether the next token is `in` or `of`. When there is no init
  // part (semicolon immediately after the opening parenthesis), it
  // is a regular `for` loop.
  pp$1.parseForStatement=function(node){this.next();var awaitAt=this.options.ecmaVersion>=9&&(this.inAsync||!this.inFunction&&this.options.allowAwaitOutsideFunction)&&this.eatContextual("await")?this.lastTokStart:-1;this.labels.push(loopLabel);this.enterScope(0);this.expect(types$2.parenL);if(this.type===types$2.semi){if(awaitAt>-1){this.unexpected(awaitAt);}return this.parseFor(node,null);}var isLet=this.isLet();if(this.type===types$2._var||this.type===types$2._const||isLet){var init$1=this.startNode(),kind=isLet?"let":this.value;this.next();this.parseVar(init$1,true,kind);this.finishNode(init$1,"VariableDeclaration");if((this.type===types$2._in||this.options.ecmaVersion>=6&&this.isContextual("of"))&&init$1.declarations.length===1){if(this.options.ecmaVersion>=9){if(this.type===types$2._in){if(awaitAt>-1){this.unexpected(awaitAt);}}else{node.await=awaitAt>-1;}}return this.parseForIn(node,init$1);}if(awaitAt>-1){this.unexpected(awaitAt);}return this.parseFor(node,init$1);}var refDestructuringErrors=new DestructuringErrors();var init=this.parseExpression(true,refDestructuringErrors);if(this.type===types$2._in||this.options.ecmaVersion>=6&&this.isContextual("of")){if(this.options.ecmaVersion>=9){if(this.type===types$2._in){if(awaitAt>-1){this.unexpected(awaitAt);}}else{node.await=awaitAt>-1;}}this.toAssignable(init,false,refDestructuringErrors);this.checkLVal(init);return this.parseForIn(node,init);}else{this.checkExpressionErrors(refDestructuringErrors,true);}if(awaitAt>-1){this.unexpected(awaitAt);}return this.parseFor(node,init);};pp$1.parseFunctionStatement=function(node,isAsync,declarationPosition){this.next();return this.parseFunction(node,FUNC_STATEMENT|(declarationPosition?0:FUNC_HANGING_STATEMENT),false,isAsync);};pp$1.parseIfStatement=function(node){this.next();node.test=this.parseParenExpression();// allow function declarations in branches, but only in non-strict mode
  node.consequent=this.parseStatement("if");node.alternate=this.eat(types$2._else)?this.parseStatement("if"):null;return this.finishNode(node,"IfStatement");};pp$1.parseReturnStatement=function(node){if(!this.inFunction&&!this.options.allowReturnOutsideFunction){this.raise(this.start,"'return' outside of function");}this.next();// In `return` (and `break`/`continue`), the keywords with
  // optional arguments, we eagerly look for a semicolon or the
  // possibility to insert one.
  if(this.eat(types$2.semi)||this.insertSemicolon()){node.argument=null;}else{node.argument=this.parseExpression();this.semicolon();}return this.finishNode(node,"ReturnStatement");};pp$1.parseSwitchStatement=function(node){this.next();node.discriminant=this.parseParenExpression();node.cases=[];this.expect(types$2.braceL);this.labels.push(switchLabel);this.enterScope(0);// Statements under must be grouped (by label) in SwitchCase
  // nodes. `cur` is used to keep the node that we are currently
  // adding statements to.
  var cur;for(var sawDefault=false;this.type!==types$2.braceR;){if(this.type===types$2._case||this.type===types$2._default){var isCase=this.type===types$2._case;if(cur){this.finishNode(cur,"SwitchCase");}node.cases.push(cur=this.startNode());cur.consequent=[];this.next();if(isCase){cur.test=this.parseExpression();}else{if(sawDefault){this.raiseRecoverable(this.lastTokStart,"Multiple default clauses");}sawDefault=true;cur.test=null;}this.expect(types$2.colon);}else{if(!cur){this.unexpected();}cur.consequent.push(this.parseStatement(null));}}this.exitScope();if(cur){this.finishNode(cur,"SwitchCase");}this.next();// Closing brace
  this.labels.pop();return this.finishNode(node,"SwitchStatement");};pp$1.parseThrowStatement=function(node){this.next();if(lineBreak.test(this.input.slice(this.lastTokEnd,this.start))){this.raise(this.lastTokEnd,"Illegal newline after throw");}node.argument=this.parseExpression();this.semicolon();return this.finishNode(node,"ThrowStatement");};// Reused empty array added for node fields that are always empty.
  var empty=[];pp$1.parseTryStatement=function(node){this.next();node.block=this.parseBlock();node.handler=null;if(this.type===types$2._catch){var clause=this.startNode();this.next();if(this.eat(types$2.parenL)){clause.param=this.parseBindingAtom();var simple=clause.param.type==="Identifier";this.enterScope(simple?SCOPE_SIMPLE_CATCH:0);this.checkLVal(clause.param,simple?BIND_SIMPLE_CATCH:BIND_LEXICAL);this.expect(types$2.parenR);}else{if(this.options.ecmaVersion<10){this.unexpected();}clause.param=null;this.enterScope(0);}clause.body=this.parseBlock(false);this.exitScope();node.handler=this.finishNode(clause,"CatchClause");}node.finalizer=this.eat(types$2._finally)?this.parseBlock():null;if(!node.handler&&!node.finalizer){this.raise(node.start,"Missing catch or finally clause");}return this.finishNode(node,"TryStatement");};pp$1.parseVarStatement=function(node,kind){this.next();this.parseVar(node,false,kind);this.semicolon();return this.finishNode(node,"VariableDeclaration");};pp$1.parseWhileStatement=function(node){this.next();node.test=this.parseParenExpression();this.labels.push(loopLabel);node.body=this.parseStatement("while");this.labels.pop();return this.finishNode(node,"WhileStatement");};pp$1.parseWithStatement=function(node){if(this.strict){this.raise(this.start,"'with' in strict mode");}this.next();node.object=this.parseParenExpression();node.body=this.parseStatement("with");return this.finishNode(node,"WithStatement");};pp$1.parseEmptyStatement=function(node){this.next();return this.finishNode(node,"EmptyStatement");};pp$1.parseLabeledStatement=function(node,maybeName,expr,context){for(var i$1=0,list=this.labels;i$1<list.length;i$1+=1){var label=list[i$1];if(label.name===maybeName){this.raise(expr.start,"Label '"+maybeName+"' is already declared");}}var kind=this.type.isLoop?"loop":this.type===types$2._switch?"switch":null;for(var i=this.labels.length-1;i>=0;i--){var label$1=this.labels[i];if(label$1.statementStart===node.start){// Update information about previous labels on this node
  label$1.statementStart=this.start;label$1.kind=kind;}else{break;}}this.labels.push({name:maybeName,kind:kind,statementStart:this.start});node.body=this.parseStatement(context?context.indexOf("label")===-1?context+"label":context:"label");this.labels.pop();node.label=expr;return this.finishNode(node,"LabeledStatement");};pp$1.parseExpressionStatement=function(node,expr){node.expression=expr;this.semicolon();return this.finishNode(node,"ExpressionStatement");};// Parse a semicolon-enclosed block of statements, handling `"use
  // strict"` declarations when `allowStrict` is true (used for
  // function bodies).
  pp$1.parseBlock=function(createNewLexicalScope,node){if(createNewLexicalScope===void 0)createNewLexicalScope=true;if(node===void 0)node=this.startNode();node.body=[];this.expect(types$2.braceL);if(createNewLexicalScope){this.enterScope(0);}while(!this.eat(types$2.braceR)){var stmt=this.parseStatement(null);node.body.push(stmt);}if(createNewLexicalScope){this.exitScope();}return this.finishNode(node,"BlockStatement");};// Parse a regular `for` loop. The disambiguation code in
  // `parseStatement` will already have parsed the init statement or
  // expression.
  pp$1.parseFor=function(node,init){node.init=init;this.expect(types$2.semi);node.test=this.type===types$2.semi?null:this.parseExpression();this.expect(types$2.semi);node.update=this.type===types$2.parenR?null:this.parseExpression();this.expect(types$2.parenR);node.body=this.parseStatement("for");this.exitScope();this.labels.pop();return this.finishNode(node,"ForStatement");};// Parse a `for`/`in` and `for`/`of` loop, which are almost
  // same from parser's perspective.
  pp$1.parseForIn=function(node,init){var isForIn=this.type===types$2._in;this.next();if(init.type==="VariableDeclaration"&&init.declarations[0].init!=null&&(!isForIn||this.options.ecmaVersion<8||this.strict||init.kind!=="var"||init.declarations[0].id.type!=="Identifier")){this.raise(init.start,(isForIn?"for-in":"for-of")+" loop variable declaration may not have an initializer");}else if(init.type==="AssignmentPattern"){this.raise(init.start,"Invalid left-hand side in for-loop");}node.left=init;node.right=isForIn?this.parseExpression():this.parseMaybeAssign();this.expect(types$2.parenR);node.body=this.parseStatement("for");this.exitScope();this.labels.pop();return this.finishNode(node,isForIn?"ForInStatement":"ForOfStatement");};// Parse a list of variable declarations.
  pp$1.parseVar=function(node,isFor,kind){node.declarations=[];node.kind=kind;for(;;){var decl=this.startNode();this.parseVarId(decl,kind);if(this.eat(types$2.eq)){decl.init=this.parseMaybeAssign(isFor);}else if(kind==="const"&&!(this.type===types$2._in||this.options.ecmaVersion>=6&&this.isContextual("of"))){this.unexpected();}else if(decl.id.type!=="Identifier"&&!(isFor&&(this.type===types$2._in||this.isContextual("of")))){this.raise(this.lastTokEnd,"Complex binding patterns require an initialization value");}else{decl.init=null;}node.declarations.push(this.finishNode(decl,"VariableDeclarator"));if(!this.eat(types$2.comma)){break;}}return node;};pp$1.parseVarId=function(decl,kind){decl.id=this.parseBindingAtom();this.checkLVal(decl.id,kind==="var"?BIND_VAR:BIND_LEXICAL,false);};var FUNC_STATEMENT=1,FUNC_HANGING_STATEMENT=2,FUNC_NULLABLE_ID=4;// Parse a function declaration or literal (depending on the
  // `statement & FUNC_STATEMENT`).
  // Remove `allowExpressionBody` for 7.0.0, as it is only called with false
  pp$1.parseFunction=function(node,statement,allowExpressionBody,isAsync){this.initFunction(node);if(this.options.ecmaVersion>=9||this.options.ecmaVersion>=6&&!isAsync){if(this.type===types$2.star&&statement&FUNC_HANGING_STATEMENT){this.unexpected();}node.generator=this.eat(types$2.star);}if(this.options.ecmaVersion>=8){node.async=!!isAsync;}if(statement&FUNC_STATEMENT){node.id=statement&FUNC_NULLABLE_ID&&this.type!==types$2.name?null:this.parseIdent();if(node.id&&!(statement&FUNC_HANGING_STATEMENT))// If it is a regular function declaration in sloppy mode, then it is
  // subject to Annex B semantics (BIND_FUNCTION). Otherwise, the binding
  // mode depends on properties of the current scope (see
  // treatFunctionsAsVar).
  {this.checkLVal(node.id,this.strict||node.generator||node.async?this.treatFunctionsAsVar?BIND_VAR:BIND_LEXICAL:BIND_FUNCTION);}}var oldYieldPos=this.yieldPos,oldAwaitPos=this.awaitPos,oldAwaitIdentPos=this.awaitIdentPos;this.yieldPos=0;this.awaitPos=0;this.awaitIdentPos=0;this.enterScope(functionFlags(node.async,node.generator));if(!(statement&FUNC_STATEMENT)){node.id=this.type===types$2.name?this.parseIdent():null;}this.parseFunctionParams(node);this.parseFunctionBody(node,allowExpressionBody,false);this.yieldPos=oldYieldPos;this.awaitPos=oldAwaitPos;this.awaitIdentPos=oldAwaitIdentPos;return this.finishNode(node,statement&FUNC_STATEMENT?"FunctionDeclaration":"FunctionExpression");};pp$1.parseFunctionParams=function(node){this.expect(types$2.parenL);node.params=this.parseBindingList(types$2.parenR,false,this.options.ecmaVersion>=8);this.checkYieldAwaitInDefaultParams();};// Parse a class declaration or literal (depending on the
  // `isStatement` parameter).
  pp$1.parseClass=function(node,isStatement){this.next();// ecma-262 14.6 Class Definitions
  // A class definition is always strict mode code.
  var oldStrict=this.strict;this.strict=true;this.parseClassId(node,isStatement);this.parseClassSuper(node);var classBody=this.startNode();var hadConstructor=false;classBody.body=[];this.expect(types$2.braceL);while(!this.eat(types$2.braceR)){var element=this.parseClassElement(node.superClass!==null);if(element){classBody.body.push(element);if(element.type==="MethodDefinition"&&element.kind==="constructor"){if(hadConstructor){this.raise(element.start,"Duplicate constructor in the same class");}hadConstructor=true;}}}node.body=this.finishNode(classBody,"ClassBody");this.strict=oldStrict;return this.finishNode(node,isStatement?"ClassDeclaration":"ClassExpression");};pp$1.parseClassElement=function(constructorAllowsSuper){var this$1=this;if(this.eat(types$2.semi)){return null;}var method=this.startNode();var tryContextual=function tryContextual(k,noLineBreak){if(noLineBreak===void 0)noLineBreak=false;var start=this$1.start,startLoc=this$1.startLoc;if(!this$1.eatContextual(k)){return false;}if(this$1.type!==types$2.parenL&&(!noLineBreak||!this$1.canInsertSemicolon())){return true;}if(method.key){this$1.unexpected();}method.computed=false;method.key=this$1.startNodeAt(start,startLoc);method.key.name=k;this$1.finishNode(method.key,"Identifier");return false;};method.kind="method";method.static=tryContextual("static");var isGenerator=this.eat(types$2.star);var isAsync=false;if(!isGenerator){if(this.options.ecmaVersion>=8&&tryContextual("async",true)){isAsync=true;isGenerator=this.options.ecmaVersion>=9&&this.eat(types$2.star);}else if(tryContextual("get")){method.kind="get";}else if(tryContextual("set")){method.kind="set";}}if(!method.key){this.parsePropertyName(method);}var key=method.key;var allowsDirectSuper=false;if(!method.computed&&!method.static&&(key.type==="Identifier"&&key.name==="constructor"||key.type==="Literal"&&key.value==="constructor")){if(method.kind!=="method"){this.raise(key.start,"Constructor can't have get/set modifier");}if(isGenerator){this.raise(key.start,"Constructor can't be a generator");}if(isAsync){this.raise(key.start,"Constructor can't be an async method");}method.kind="constructor";allowsDirectSuper=constructorAllowsSuper;}else if(method.static&&key.type==="Identifier"&&key.name==="prototype"){this.raise(key.start,"Classes may not have a static property named prototype");}this.parseClassMethod(method,isGenerator,isAsync,allowsDirectSuper);if(method.kind==="get"&&method.value.params.length!==0){this.raiseRecoverable(method.value.start,"getter should have no params");}if(method.kind==="set"&&method.value.params.length!==1){this.raiseRecoverable(method.value.start,"setter should have exactly one param");}if(method.kind==="set"&&method.value.params[0].type==="RestElement"){this.raiseRecoverable(method.value.params[0].start,"Setter cannot use rest params");}return method;};pp$1.parseClassMethod=function(method,isGenerator,isAsync,allowsDirectSuper){method.value=this.parseMethod(isGenerator,isAsync,allowsDirectSuper);return this.finishNode(method,"MethodDefinition");};pp$1.parseClassId=function(node,isStatement){if(this.type===types$2.name){node.id=this.parseIdent();if(isStatement){this.checkLVal(node.id,BIND_LEXICAL,false);}}else{if(isStatement===true){this.unexpected();}node.id=null;}};pp$1.parseClassSuper=function(node){node.superClass=this.eat(types$2._extends)?this.parseExprSubscripts():null;};// Parses module export declaration.
  pp$1.parseExport=function(node,exports){this.next();// export * from '...'
  if(this.eat(types$2.star)){this.expectContextual("from");if(this.type!==types$2.string){this.unexpected();}node.source=this.parseExprAtom();this.semicolon();return this.finishNode(node,"ExportAllDeclaration");}if(this.eat(types$2._default)){// export default ...
  this.checkExport(exports,"default",this.lastTokStart);var isAsync;if(this.type===types$2._function||(isAsync=this.isAsyncFunction())){var fNode=this.startNode();this.next();if(isAsync){this.next();}node.declaration=this.parseFunction(fNode,FUNC_STATEMENT|FUNC_NULLABLE_ID,false,isAsync);}else if(this.type===types$2._class){var cNode=this.startNode();node.declaration=this.parseClass(cNode,"nullableID");}else{node.declaration=this.parseMaybeAssign();this.semicolon();}return this.finishNode(node,"ExportDefaultDeclaration");}// export var|const|let|function|class ...
  if(this.shouldParseExportStatement()){node.declaration=this.parseStatement(null);if(node.declaration.type==="VariableDeclaration"){this.checkVariableExport(exports,node.declaration.declarations);}else{this.checkExport(exports,node.declaration.id.name,node.declaration.id.start);}node.specifiers=[];node.source=null;}else{// export { x, y as z } [from '...']
  node.declaration=null;node.specifiers=this.parseExportSpecifiers(exports);if(this.eatContextual("from")){if(this.type!==types$2.string){this.unexpected();}node.source=this.parseExprAtom();}else{for(var i=0,list=node.specifiers;i<list.length;i+=1){// check for keywords used as local names
  var spec=list[i];this.checkUnreserved(spec.local);// check if export is defined
  this.checkLocalExport(spec.local);}node.source=null;}this.semicolon();}return this.finishNode(node,"ExportNamedDeclaration");};pp$1.checkExport=function(exports,name,pos){if(!exports){return;}if(has(exports,name)){this.raiseRecoverable(pos,"Duplicate export '"+name+"'");}exports[name]=true;};pp$1.checkPatternExport=function(exports,pat){var type=pat.type;if(type==="Identifier"){this.checkExport(exports,pat.name,pat.start);}else if(type==="ObjectPattern"){for(var i=0,list=pat.properties;i<list.length;i+=1){var prop=list[i];this.checkPatternExport(exports,prop);}}else if(type==="ArrayPattern"){for(var i$1=0,list$1=pat.elements;i$1<list$1.length;i$1+=1){var elt=list$1[i$1];if(elt){this.checkPatternExport(exports,elt);}}}else if(type==="Property"){this.checkPatternExport(exports,pat.value);}else if(type==="AssignmentPattern"){this.checkPatternExport(exports,pat.left);}else if(type==="RestElement"){this.checkPatternExport(exports,pat.argument);}else if(type==="ParenthesizedExpression"){this.checkPatternExport(exports,pat.expression);}};pp$1.checkVariableExport=function(exports,decls){if(!exports){return;}for(var i=0,list=decls;i<list.length;i+=1){var decl=list[i];this.checkPatternExport(exports,decl.id);}};pp$1.shouldParseExportStatement=function(){return this.type.keyword==="var"||this.type.keyword==="const"||this.type.keyword==="class"||this.type.keyword==="function"||this.isLet()||this.isAsyncFunction();};// Parses a comma-separated list of module exports.
  pp$1.parseExportSpecifiers=function(exports){var nodes=[],first=true;// export { x, y as z } [from '...']
  this.expect(types$2.braceL);while(!this.eat(types$2.braceR)){if(!first){this.expect(types$2.comma);if(this.afterTrailingComma(types$2.braceR)){break;}}else{first=false;}var node=this.startNode();node.local=this.parseIdent(true);node.exported=this.eatContextual("as")?this.parseIdent(true):node.local;this.checkExport(exports,node.exported.name,node.exported.start);nodes.push(this.finishNode(node,"ExportSpecifier"));}return nodes;};// Parses import declaration.
  pp$1.parseImport=function(node){this.next();// import '...'
  if(this.type===types$2.string){node.specifiers=empty;node.source=this.parseExprAtom();}else{node.specifiers=this.parseImportSpecifiers();this.expectContextual("from");node.source=this.type===types$2.string?this.parseExprAtom():this.unexpected();}this.semicolon();return this.finishNode(node,"ImportDeclaration");};// Parses a comma-separated list of module imports.
  pp$1.parseImportSpecifiers=function(){var nodes=[],first=true;if(this.type===types$2.name){// import defaultObj, { x, y as z } from '...'
  var node=this.startNode();node.local=this.parseIdent();this.checkLVal(node.local,BIND_LEXICAL);nodes.push(this.finishNode(node,"ImportDefaultSpecifier"));if(!this.eat(types$2.comma)){return nodes;}}if(this.type===types$2.star){var node$1=this.startNode();this.next();this.expectContextual("as");node$1.local=this.parseIdent();this.checkLVal(node$1.local,BIND_LEXICAL);nodes.push(this.finishNode(node$1,"ImportNamespaceSpecifier"));return nodes;}this.expect(types$2.braceL);while(!this.eat(types$2.braceR)){if(!first){this.expect(types$2.comma);if(this.afterTrailingComma(types$2.braceR)){break;}}else{first=false;}var node$2=this.startNode();node$2.imported=this.parseIdent(true);if(this.eatContextual("as")){node$2.local=this.parseIdent();}else{this.checkUnreserved(node$2.imported);node$2.local=node$2.imported;}this.checkLVal(node$2.local,BIND_LEXICAL);nodes.push(this.finishNode(node$2,"ImportSpecifier"));}return nodes;};// Set `ExpressionStatement#directive` property for directive prologues.
  pp$1.adaptDirectivePrologue=function(statements){for(var i=0;i<statements.length&&this.isDirectiveCandidate(statements[i]);++i){statements[i].directive=statements[i].expression.raw.slice(1,-1);}};pp$1.isDirectiveCandidate=function(statement){return statement.type==="ExpressionStatement"&&statement.expression.type==="Literal"&&typeof statement.expression.value==="string"&&(// Reject parenthesized strings.
  this.input[statement.start]==="\""||this.input[statement.start]==="'");};var pp$2=Parser.prototype;// Convert existing expression atom to assignable pattern
  // if possible.
  pp$2.toAssignable=function(node,isBinding,refDestructuringErrors){if(this.options.ecmaVersion>=6&&node){switch(node.type){case"Identifier":if(this.inAsync&&node.name==="await"){this.raise(node.start,"Cannot use 'await' as identifier inside an async function");}break;case"ObjectPattern":case"ArrayPattern":case"RestElement":break;case"ObjectExpression":node.type="ObjectPattern";if(refDestructuringErrors){this.checkPatternErrors(refDestructuringErrors,true);}for(var i=0,list=node.properties;i<list.length;i+=1){var prop=list[i];this.toAssignable(prop,isBinding);// Early error:
  //   AssignmentRestProperty[Yield, Await] :
  //     `...` DestructuringAssignmentTarget[Yield, Await]
  //
  //   It is a Syntax Error if |DestructuringAssignmentTarget| is an |ArrayLiteral| or an |ObjectLiteral|.
  if(prop.type==="RestElement"&&(prop.argument.type==="ArrayPattern"||prop.argument.type==="ObjectPattern")){this.raise(prop.argument.start,"Unexpected token");}}break;case"Property":// AssignmentProperty has type === "Property"
  if(node.kind!=="init"){this.raise(node.key.start,"Object pattern can't contain getter or setter");}this.toAssignable(node.value,isBinding);break;case"ArrayExpression":node.type="ArrayPattern";if(refDestructuringErrors){this.checkPatternErrors(refDestructuringErrors,true);}this.toAssignableList(node.elements,isBinding);break;case"SpreadElement":node.type="RestElement";this.toAssignable(node.argument,isBinding);if(node.argument.type==="AssignmentPattern"){this.raise(node.argument.start,"Rest elements cannot have a default value");}break;case"AssignmentExpression":if(node.operator!=="="){this.raise(node.left.end,"Only '=' operator can be used for specifying default value.");}node.type="AssignmentPattern";delete node.operator;this.toAssignable(node.left,isBinding);// falls through to AssignmentPattern
  case"AssignmentPattern":break;case"ParenthesizedExpression":this.toAssignable(node.expression,isBinding,refDestructuringErrors);break;case"MemberExpression":if(!isBinding){break;}default:this.raise(node.start,"Assigning to rvalue");}}else if(refDestructuringErrors){this.checkPatternErrors(refDestructuringErrors,true);}return node;};// Convert list of expression atoms to binding list.
  pp$2.toAssignableList=function(exprList,isBinding){var end=exprList.length;for(var i=0;i<end;i++){var elt=exprList[i];if(elt){this.toAssignable(elt,isBinding);}}if(end){var last=exprList[end-1];if(this.options.ecmaVersion===6&&isBinding&&last&&last.type==="RestElement"&&last.argument.type!=="Identifier"){this.unexpected(last.argument.start);}}return exprList;};// Parses spread element.
  pp$2.parseSpread=function(refDestructuringErrors){var node=this.startNode();this.next();node.argument=this.parseMaybeAssign(false,refDestructuringErrors);return this.finishNode(node,"SpreadElement");};pp$2.parseRestBinding=function(){var node=this.startNode();this.next();// RestElement inside of a function parameter must be an identifier
  if(this.options.ecmaVersion===6&&this.type!==types$2.name){this.unexpected();}node.argument=this.parseBindingAtom();return this.finishNode(node,"RestElement");};// Parses lvalue (assignable) atom.
  pp$2.parseBindingAtom=function(){if(this.options.ecmaVersion>=6){switch(this.type){case types$2.bracketL:var node=this.startNode();this.next();node.elements=this.parseBindingList(types$2.bracketR,true,true);return this.finishNode(node,"ArrayPattern");case types$2.braceL:return this.parseObj(true);}}return this.parseIdent();};pp$2.parseBindingList=function(close,allowEmpty,allowTrailingComma){var elts=[],first=true;while(!this.eat(close)){if(first){first=false;}else{this.expect(types$2.comma);}if(allowEmpty&&this.type===types$2.comma){elts.push(null);}else if(allowTrailingComma&&this.afterTrailingComma(close)){break;}else if(this.type===types$2.ellipsis){var rest=this.parseRestBinding();this.parseBindingListItem(rest);elts.push(rest);if(this.type===types$2.comma){this.raise(this.start,"Comma is not permitted after the rest element");}this.expect(close);break;}else{var elem=this.parseMaybeDefault(this.start,this.startLoc);this.parseBindingListItem(elem);elts.push(elem);}}return elts;};pp$2.parseBindingListItem=function(param){return param;};// Parses assignment pattern around given atom if possible.
  pp$2.parseMaybeDefault=function(startPos,startLoc,left){left=left||this.parseBindingAtom();if(this.options.ecmaVersion<6||!this.eat(types$2.eq)){return left;}var node=this.startNodeAt(startPos,startLoc);node.left=left;node.right=this.parseMaybeAssign();return this.finishNode(node,"AssignmentPattern");};// Verify that a node is an lval — something that can be assigned
  // to.
  // bindingType can be either:
  // 'var' indicating that the lval creates a 'var' binding
  // 'let' indicating that the lval creates a lexical ('let' or 'const') binding
  // 'none' indicating that the binding should be checked for illegal identifiers, but not for duplicate references
  pp$2.checkLVal=function(expr,bindingType,checkClashes){if(bindingType===void 0)bindingType=BIND_NONE;switch(expr.type){case"Identifier":if(bindingType===BIND_LEXICAL&&expr.name==="let"){this.raiseRecoverable(expr.start,"let is disallowed as a lexically bound name");}if(this.strict&&this.reservedWordsStrictBind.test(expr.name)){this.raiseRecoverable(expr.start,(bindingType?"Binding ":"Assigning to ")+expr.name+" in strict mode");}if(checkClashes){if(has(checkClashes,expr.name)){this.raiseRecoverable(expr.start,"Argument name clash");}checkClashes[expr.name]=true;}if(bindingType!==BIND_NONE&&bindingType!==BIND_OUTSIDE){this.declareName(expr.name,bindingType,expr.start);}break;case"MemberExpression":if(bindingType){this.raiseRecoverable(expr.start,"Binding member expression");}break;case"ObjectPattern":for(var i=0,list=expr.properties;i<list.length;i+=1){var prop=list[i];this.checkLVal(prop,bindingType,checkClashes);}break;case"Property":// AssignmentProperty has type === "Property"
  this.checkLVal(expr.value,bindingType,checkClashes);break;case"ArrayPattern":for(var i$1=0,list$1=expr.elements;i$1<list$1.length;i$1+=1){var elem=list$1[i$1];if(elem){this.checkLVal(elem,bindingType,checkClashes);}}break;case"AssignmentPattern":this.checkLVal(expr.left,bindingType,checkClashes);break;case"RestElement":this.checkLVal(expr.argument,bindingType,checkClashes);break;case"ParenthesizedExpression":this.checkLVal(expr.expression,bindingType,checkClashes);break;default:this.raise(expr.start,(bindingType?"Binding":"Assigning to")+" rvalue");}};// A recursive descent parser operates by defining functions for all
  var pp$3=Parser.prototype;// Check if property name clashes with already added.
  // Object/class getters and setters are not allowed to clash —
  // either with each other or with an init property — and in
  // strict mode, init properties are also not allowed to be repeated.
  pp$3.checkPropClash=function(prop,propHash,refDestructuringErrors){if(this.options.ecmaVersion>=9&&prop.type==="SpreadElement"){return;}if(this.options.ecmaVersion>=6&&(prop.computed||prop.method||prop.shorthand)){return;}var key=prop.key;var name;switch(key.type){case"Identifier":name=key.name;break;case"Literal":name=String(key.value);break;default:return;}var kind=prop.kind;if(this.options.ecmaVersion>=6){if(name==="__proto__"&&kind==="init"){if(propHash.proto){if(refDestructuringErrors&&refDestructuringErrors.doubleProto<0){refDestructuringErrors.doubleProto=key.start;}// Backwards-compat kludge. Can be removed in version 6.0
  else{this.raiseRecoverable(key.start,"Redefinition of __proto__ property");}}propHash.proto=true;}return;}name="$"+name;var other=propHash[name];if(other){var redefinition;if(kind==="init"){redefinition=this.strict&&other.init||other.get||other.set;}else{redefinition=other.init||other[kind];}if(redefinition){this.raiseRecoverable(key.start,"Redefinition of property");}}else{other=propHash[name]={init:false,get:false,set:false};}other[kind]=true;};// ### Expression parsing
  // These nest, from the most general expression type at the top to
  // 'atomic', nondivisible expression types at the bottom. Most of
  // the functions will simply let the function(s) below them parse,
  // and, *if* the syntactic construct they handle is present, wrap
  // the AST node that the inner parser gave them in another node.
  // Parse a full expression. The optional arguments are used to
  // forbid the `in` operator (in for loops initalization expressions)
  // and provide reference for storing '=' operator inside shorthand
  // property assignment in contexts where both object expression
  // and object pattern might appear (so it's possible to raise
  // delayed syntax error at correct position).
  pp$3.parseExpression=function(noIn,refDestructuringErrors){var startPos=this.start,startLoc=this.startLoc;var expr=this.parseMaybeAssign(noIn,refDestructuringErrors);if(this.type===types$2.comma){var node=this.startNodeAt(startPos,startLoc);node.expressions=[expr];while(this.eat(types$2.comma)){node.expressions.push(this.parseMaybeAssign(noIn,refDestructuringErrors));}return this.finishNode(node,"SequenceExpression");}return expr;};// Parse an assignment expression. This includes applications of
  // operators like `+=`.
  pp$3.parseMaybeAssign=function(noIn,refDestructuringErrors,afterLeftParse){if(this.isContextual("yield")){if(this.inGenerator){return this.parseYield(noIn);}// The tokenizer will assume an expression is allowed after
  // `yield`, but this isn't that kind of yield
  else{this.exprAllowed=false;}}var ownDestructuringErrors=false,oldParenAssign=-1,oldTrailingComma=-1,oldShorthandAssign=-1;if(refDestructuringErrors){oldParenAssign=refDestructuringErrors.parenthesizedAssign;oldTrailingComma=refDestructuringErrors.trailingComma;oldShorthandAssign=refDestructuringErrors.shorthandAssign;refDestructuringErrors.parenthesizedAssign=refDestructuringErrors.trailingComma=refDestructuringErrors.shorthandAssign=-1;}else{refDestructuringErrors=new DestructuringErrors();ownDestructuringErrors=true;}var startPos=this.start,startLoc=this.startLoc;if(this.type===types$2.parenL||this.type===types$2.name){this.potentialArrowAt=this.start;}var left=this.parseMaybeConditional(noIn,refDestructuringErrors);if(afterLeftParse){left=afterLeftParse.call(this,left,startPos,startLoc);}if(this.type.isAssign){var node=this.startNodeAt(startPos,startLoc);node.operator=this.value;node.left=this.type===types$2.eq?this.toAssignable(left,false,refDestructuringErrors):left;if(!ownDestructuringErrors){DestructuringErrors.call(refDestructuringErrors);}refDestructuringErrors.shorthandAssign=-1;// reset because shorthand default was used correctly
  this.checkLVal(left);this.next();node.right=this.parseMaybeAssign(noIn);return this.finishNode(node,"AssignmentExpression");}else{if(ownDestructuringErrors){this.checkExpressionErrors(refDestructuringErrors,true);}}if(oldParenAssign>-1){refDestructuringErrors.parenthesizedAssign=oldParenAssign;}if(oldTrailingComma>-1){refDestructuringErrors.trailingComma=oldTrailingComma;}if(oldShorthandAssign>-1){refDestructuringErrors.shorthandAssign=oldShorthandAssign;}return left;};// Parse a ternary conditional (`?:`) operator.
  pp$3.parseMaybeConditional=function(noIn,refDestructuringErrors){var startPos=this.start,startLoc=this.startLoc;var expr=this.parseExprOps(noIn,refDestructuringErrors);if(this.checkExpressionErrors(refDestructuringErrors)){return expr;}if(this.eat(types$2.question)){var node=this.startNodeAt(startPos,startLoc);node.test=expr;node.consequent=this.parseMaybeAssign();this.expect(types$2.colon);node.alternate=this.parseMaybeAssign(noIn);return this.finishNode(node,"ConditionalExpression");}return expr;};// Start the precedence parser.
  pp$3.parseExprOps=function(noIn,refDestructuringErrors){var startPos=this.start,startLoc=this.startLoc;var expr=this.parseMaybeUnary(refDestructuringErrors,false);if(this.checkExpressionErrors(refDestructuringErrors)){return expr;}return expr.start===startPos&&expr.type==="ArrowFunctionExpression"?expr:this.parseExprOp(expr,startPos,startLoc,-1,noIn);};// Parse binary operators with the operator precedence parsing
  // algorithm. `left` is the left-hand side of the operator.
  // `minPrec` provides context that allows the function to stop and
  // defer further parser to one of its callers when it encounters an
  // operator that has a lower precedence than the set it is parsing.
  pp$3.parseExprOp=function(left,leftStartPos,leftStartLoc,minPrec,noIn){var prec=this.type.binop;if(prec!=null&&(!noIn||this.type!==types$2._in)){if(prec>minPrec){var logical=this.type===types$2.logicalOR||this.type===types$2.logicalAND;var op=this.value;this.next();var startPos=this.start,startLoc=this.startLoc;var right=this.parseExprOp(this.parseMaybeUnary(null,false),startPos,startLoc,prec,noIn);var node=this.buildBinary(leftStartPos,leftStartLoc,left,right,op,logical);return this.parseExprOp(node,leftStartPos,leftStartLoc,minPrec,noIn);}}return left;};pp$3.buildBinary=function(startPos,startLoc,left,right,op,logical){var node=this.startNodeAt(startPos,startLoc);node.left=left;node.operator=op;node.right=right;return this.finishNode(node,logical?"LogicalExpression":"BinaryExpression");};// Parse unary operators, both prefix and postfix.
  pp$3.parseMaybeUnary=function(refDestructuringErrors,sawUnary){var startPos=this.start,startLoc=this.startLoc,expr;if(this.isContextual("await")&&(this.inAsync||!this.inFunction&&this.options.allowAwaitOutsideFunction)){expr=this.parseAwait();sawUnary=true;}else if(this.type.prefix){var node=this.startNode(),update=this.type===types$2.incDec;node.operator=this.value;node.prefix=true;this.next();node.argument=this.parseMaybeUnary(null,true);this.checkExpressionErrors(refDestructuringErrors,true);if(update){this.checkLVal(node.argument);}else if(this.strict&&node.operator==="delete"&&node.argument.type==="Identifier"){this.raiseRecoverable(node.start,"Deleting local variable in strict mode");}else{sawUnary=true;}expr=this.finishNode(node,update?"UpdateExpression":"UnaryExpression");}else{expr=this.parseExprSubscripts(refDestructuringErrors);if(this.checkExpressionErrors(refDestructuringErrors)){return expr;}while(this.type.postfix&&!this.canInsertSemicolon()){var node$1=this.startNodeAt(startPos,startLoc);node$1.operator=this.value;node$1.prefix=false;node$1.argument=expr;this.checkLVal(expr);this.next();expr=this.finishNode(node$1,"UpdateExpression");}}if(!sawUnary&&this.eat(types$2.starstar)){return this.buildBinary(startPos,startLoc,expr,this.parseMaybeUnary(null,false),"**",false);}else{return expr;}};// Parse call, dot, and `[]`-subscript expressions.
  pp$3.parseExprSubscripts=function(refDestructuringErrors){var startPos=this.start,startLoc=this.startLoc;var expr=this.parseExprAtom(refDestructuringErrors);var skipArrowSubscripts=expr.type==="ArrowFunctionExpression"&&this.input.slice(this.lastTokStart,this.lastTokEnd)!==")";if(this.checkExpressionErrors(refDestructuringErrors)||skipArrowSubscripts){return expr;}var result=this.parseSubscripts(expr,startPos,startLoc);if(refDestructuringErrors&&result.type==="MemberExpression"){if(refDestructuringErrors.parenthesizedAssign>=result.start){refDestructuringErrors.parenthesizedAssign=-1;}if(refDestructuringErrors.parenthesizedBind>=result.start){refDestructuringErrors.parenthesizedBind=-1;}}return result;};pp$3.parseSubscripts=function(base,startPos,startLoc,noCalls){var maybeAsyncArrow=this.options.ecmaVersion>=8&&base.type==="Identifier"&&base.name==="async"&&this.lastTokEnd===base.end&&!this.canInsertSemicolon()&&this.input.slice(base.start,base.end)==="async";while(true){var element=this.parseSubscript(base,startPos,startLoc,noCalls,maybeAsyncArrow);if(element===base||element.type==="ArrowFunctionExpression"){return element;}base=element;}};pp$3.parseSubscript=function(base,startPos,startLoc,noCalls,maybeAsyncArrow){var computed=this.eat(types$2.bracketL);if(computed||this.eat(types$2.dot)){var node=this.startNodeAt(startPos,startLoc);node.object=base;node.property=computed?this.parseExpression():this.parseIdent(true);node.computed=!!computed;if(computed){this.expect(types$2.bracketR);}base=this.finishNode(node,"MemberExpression");}else if(!noCalls&&this.eat(types$2.parenL)){var refDestructuringErrors=new DestructuringErrors(),oldYieldPos=this.yieldPos,oldAwaitPos=this.awaitPos,oldAwaitIdentPos=this.awaitIdentPos;this.yieldPos=0;this.awaitPos=0;this.awaitIdentPos=0;var exprList=this.parseExprList(types$2.parenR,this.options.ecmaVersion>=8&&base.type!=="Import",false,refDestructuringErrors);if(maybeAsyncArrow&&!this.canInsertSemicolon()&&this.eat(types$2.arrow)){this.checkPatternErrors(refDestructuringErrors,false);this.checkYieldAwaitInDefaultParams();if(this.awaitIdentPos>0){this.raise(this.awaitIdentPos,"Cannot use 'await' as identifier inside an async function");}this.yieldPos=oldYieldPos;this.awaitPos=oldAwaitPos;this.awaitIdentPos=oldAwaitIdentPos;return this.parseArrowExpression(this.startNodeAt(startPos,startLoc),exprList,true);}this.checkExpressionErrors(refDestructuringErrors,true);this.yieldPos=oldYieldPos||this.yieldPos;this.awaitPos=oldAwaitPos||this.awaitPos;this.awaitIdentPos=oldAwaitIdentPos||this.awaitIdentPos;var node$1=this.startNodeAt(startPos,startLoc);node$1.callee=base;node$1.arguments=exprList;if(node$1.callee.type==="Import"){if(node$1.arguments.length!==1){this.raise(node$1.start,"import() requires exactly one argument");}var importArg=node$1.arguments[0];if(importArg&&importArg.type==="SpreadElement"){this.raise(importArg.start,"... is not allowed in import()");}}base=this.finishNode(node$1,"CallExpression");}else if(this.type===types$2.backQuote){var node$2=this.startNodeAt(startPos,startLoc);node$2.tag=base;node$2.quasi=this.parseTemplate({isTagged:true});base=this.finishNode(node$2,"TaggedTemplateExpression");}return base;};// Parse an atomic expression — either a single token that is an
  // expression, an expression started by a keyword like `function` or
  // `new`, or an expression wrapped in punctuation like `()`, `[]`,
  // or `{}`.
  pp$3.parseExprAtom=function(refDestructuringErrors){// If a division operator appears in an expression position, the
  // tokenizer got confused, and we force it to read a regexp instead.
  if(this.type===types$2.slash){this.readRegexp();}var node,canBeArrow=this.potentialArrowAt===this.start;switch(this.type){case types$2._super:if(!this.allowSuper){this.raise(this.start,"'super' keyword outside a method");}node=this.startNode();this.next();if(this.type===types$2.parenL&&!this.allowDirectSuper){this.raise(node.start,"super() call outside constructor of a subclass");}// The `super` keyword can appear at below:
  // SuperProperty:
  //     super [ Expression ]
  //     super . IdentifierName
  // SuperCall:
  //     super Arguments
  if(this.type!==types$2.dot&&this.type!==types$2.bracketL&&this.type!==types$2.parenL){this.unexpected();}return this.finishNode(node,"Super");case types$2._this:node=this.startNode();this.next();return this.finishNode(node,"ThisExpression");case types$2.name:var startPos=this.start,startLoc=this.startLoc,containsEsc=this.containsEsc;var id=this.parseIdent(false);if(this.options.ecmaVersion>=8&&!containsEsc&&id.name==="async"&&!this.canInsertSemicolon()&&this.eat(types$2._function)){return this.parseFunction(this.startNodeAt(startPos,startLoc),0,false,true);}if(canBeArrow&&!this.canInsertSemicolon()){if(this.eat(types$2.arrow)){return this.parseArrowExpression(this.startNodeAt(startPos,startLoc),[id],false);}if(this.options.ecmaVersion>=8&&id.name==="async"&&this.type===types$2.name&&!containsEsc){id=this.parseIdent(false);if(this.canInsertSemicolon()||!this.eat(types$2.arrow)){this.unexpected();}return this.parseArrowExpression(this.startNodeAt(startPos,startLoc),[id],true);}}return id;case types$2.regexp:var value=this.value;node=this.parseLiteral(value.value);node.regex={pattern:value.pattern,flags:value.flags};return node;case types$2.num:case types$2.string:return this.parseLiteral(this.value);case types$2._null:case types$2._true:case types$2._false:node=this.startNode();node.value=this.type===types$2._null?null:this.type===types$2._true;node.raw=this.type.keyword;this.next();return this.finishNode(node,"Literal");case types$2.parenL:var start=this.start,expr=this.parseParenAndDistinguishExpression(canBeArrow);if(refDestructuringErrors){if(refDestructuringErrors.parenthesizedAssign<0&&!this.isSimpleAssignTarget(expr)){refDestructuringErrors.parenthesizedAssign=start;}if(refDestructuringErrors.parenthesizedBind<0){refDestructuringErrors.parenthesizedBind=start;}}return expr;case types$2.bracketL:node=this.startNode();this.next();node.elements=this.parseExprList(types$2.bracketR,true,true,refDestructuringErrors);return this.finishNode(node,"ArrayExpression");case types$2.braceL:return this.parseObj(false,refDestructuringErrors);case types$2._function:node=this.startNode();this.next();return this.parseFunction(node,0);case types$2._class:return this.parseClass(this.startNode(),false);case types$2._new:return this.parseNew();case types$2.backQuote:return this.parseTemplate();case types$2._import:if(this.options.ecmaVersion>10){return this.parseDynamicImport();}else{return this.unexpected();}default:this.unexpected();}};pp$3.parseDynamicImport=function(){var node=this.startNode();this.next();if(this.type!==types$2.parenL){this.unexpected();}return this.finishNode(node,"Import");};pp$3.parseLiteral=function(value){var node=this.startNode();node.value=value;node.raw=this.input.slice(this.start,this.end);if(node.raw.charCodeAt(node.raw.length-1)===110){node.bigint=node.raw.slice(0,-1);}this.next();return this.finishNode(node,"Literal");};pp$3.parseParenExpression=function(){this.expect(types$2.parenL);var val=this.parseExpression();this.expect(types$2.parenR);return val;};pp$3.parseParenAndDistinguishExpression=function(canBeArrow){var startPos=this.start,startLoc=this.startLoc,val,allowTrailingComma=this.options.ecmaVersion>=8;if(this.options.ecmaVersion>=6){this.next();var innerStartPos=this.start,innerStartLoc=this.startLoc;var exprList=[],first=true,lastIsComma=false;var refDestructuringErrors=new DestructuringErrors(),oldYieldPos=this.yieldPos,oldAwaitPos=this.awaitPos,spreadStart;this.yieldPos=0;this.awaitPos=0;// Do not save awaitIdentPos to allow checking awaits nested in parameters
  while(this.type!==types$2.parenR){first?first=false:this.expect(types$2.comma);if(allowTrailingComma&&this.afterTrailingComma(types$2.parenR,true)){lastIsComma=true;break;}else if(this.type===types$2.ellipsis){spreadStart=this.start;exprList.push(this.parseParenItem(this.parseRestBinding()));if(this.type===types$2.comma){this.raise(this.start,"Comma is not permitted after the rest element");}break;}else{exprList.push(this.parseMaybeAssign(false,refDestructuringErrors,this.parseParenItem));}}var innerEndPos=this.start,innerEndLoc=this.startLoc;this.expect(types$2.parenR);if(canBeArrow&&!this.canInsertSemicolon()&&this.eat(types$2.arrow)){this.checkPatternErrors(refDestructuringErrors,false);this.checkYieldAwaitInDefaultParams();this.yieldPos=oldYieldPos;this.awaitPos=oldAwaitPos;return this.parseParenArrowList(startPos,startLoc,exprList);}if(!exprList.length||lastIsComma){this.unexpected(this.lastTokStart);}if(spreadStart){this.unexpected(spreadStart);}this.checkExpressionErrors(refDestructuringErrors,true);this.yieldPos=oldYieldPos||this.yieldPos;this.awaitPos=oldAwaitPos||this.awaitPos;if(exprList.length>1){val=this.startNodeAt(innerStartPos,innerStartLoc);val.expressions=exprList;this.finishNodeAt(val,"SequenceExpression",innerEndPos,innerEndLoc);}else{val=exprList[0];}}else{val=this.parseParenExpression();}if(this.options.preserveParens){var par=this.startNodeAt(startPos,startLoc);par.expression=val;return this.finishNode(par,"ParenthesizedExpression");}else{return val;}};pp$3.parseParenItem=function(item){return item;};pp$3.parseParenArrowList=function(startPos,startLoc,exprList){return this.parseArrowExpression(this.startNodeAt(startPos,startLoc),exprList);};// New's precedence is slightly tricky. It must allow its argument to
  // be a `[]` or dot subscript expression, but not a call — at least,
  // not without wrapping it in parentheses. Thus, it uses the noCalls
  // argument to parseSubscripts to prevent it from consuming the
  // argument list.
  var empty$1=[];pp$3.parseNew=function(){var node=this.startNode();var meta=this.parseIdent(true);if(this.options.ecmaVersion>=6&&this.eat(types$2.dot)){node.meta=meta;var containsEsc=this.containsEsc;node.property=this.parseIdent(true);if(node.property.name!=="target"||containsEsc){this.raiseRecoverable(node.property.start,"The only valid meta property for new is new.target");}if(!this.inNonArrowFunction()){this.raiseRecoverable(node.start,"new.target can only be used in functions");}return this.finishNode(node,"MetaProperty");}var startPos=this.start,startLoc=this.startLoc;node.callee=this.parseSubscripts(this.parseExprAtom(),startPos,startLoc,true);if(this.options.ecmaVersion>10&&node.callee.type==="Import"){this.raise(node.callee.start,"Cannot use new with import(...)");}if(this.eat(types$2.parenL)){node.arguments=this.parseExprList(types$2.parenR,this.options.ecmaVersion>=8&&node.callee.type!=="Import",false);}else{node.arguments=empty$1;}return this.finishNode(node,"NewExpression");};// Parse template expression.
  pp$3.parseTemplateElement=function(ref){var isTagged=ref.isTagged;var elem=this.startNode();if(this.type===types$2.invalidTemplate){if(!isTagged){this.raiseRecoverable(this.start,"Bad escape sequence in untagged template literal");}elem.value={raw:this.value,cooked:null};}else{elem.value={raw:this.input.slice(this.start,this.end).replace(/\r\n?/g,"\n"),cooked:this.value};}this.next();elem.tail=this.type===types$2.backQuote;return this.finishNode(elem,"TemplateElement");};pp$3.parseTemplate=function(ref){if(ref===void 0)ref={};var isTagged=ref.isTagged;if(isTagged===void 0)isTagged=false;var node=this.startNode();this.next();node.expressions=[];var curElt=this.parseTemplateElement({isTagged:isTagged});node.quasis=[curElt];while(!curElt.tail){if(this.type===types$2.eof){this.raise(this.pos,"Unterminated template literal");}this.expect(types$2.dollarBraceL);node.expressions.push(this.parseExpression());this.expect(types$2.braceR);node.quasis.push(curElt=this.parseTemplateElement({isTagged:isTagged}));}this.next();return this.finishNode(node,"TemplateLiteral");};pp$3.isAsyncProp=function(prop){return !prop.computed&&prop.key.type==="Identifier"&&prop.key.name==="async"&&(this.type===types$2.name||this.type===types$2.num||this.type===types$2.string||this.type===types$2.bracketL||this.type.keyword||this.options.ecmaVersion>=9&&this.type===types$2.star)&&!lineBreak.test(this.input.slice(this.lastTokEnd,this.start));};// Parse an object literal or binding pattern.
  pp$3.parseObj=function(isPattern,refDestructuringErrors){var node=this.startNode(),first=true,propHash={};node.properties=[];this.next();while(!this.eat(types$2.braceR)){if(!first){this.expect(types$2.comma);if(this.afterTrailingComma(types$2.braceR)){break;}}else{first=false;}var prop=this.parseProperty(isPattern,refDestructuringErrors);if(!isPattern){this.checkPropClash(prop,propHash,refDestructuringErrors);}node.properties.push(prop);}return this.finishNode(node,isPattern?"ObjectPattern":"ObjectExpression");};pp$3.parseProperty=function(isPattern,refDestructuringErrors){var prop=this.startNode(),isGenerator,isAsync,startPos,startLoc;if(this.options.ecmaVersion>=9&&this.eat(types$2.ellipsis)){if(isPattern){prop.argument=this.parseIdent(false);if(this.type===types$2.comma){this.raise(this.start,"Comma is not permitted after the rest element");}return this.finishNode(prop,"RestElement");}// To disallow parenthesized identifier via `this.toAssignable()`.
  if(this.type===types$2.parenL&&refDestructuringErrors){if(refDestructuringErrors.parenthesizedAssign<0){refDestructuringErrors.parenthesizedAssign=this.start;}if(refDestructuringErrors.parenthesizedBind<0){refDestructuringErrors.parenthesizedBind=this.start;}}// Parse argument.
  prop.argument=this.parseMaybeAssign(false,refDestructuringErrors);// To disallow trailing comma via `this.toAssignable()`.
  if(this.type===types$2.comma&&refDestructuringErrors&&refDestructuringErrors.trailingComma<0){refDestructuringErrors.trailingComma=this.start;}// Finish
  return this.finishNode(prop,"SpreadElement");}if(this.options.ecmaVersion>=6){prop.method=false;prop.shorthand=false;if(isPattern||refDestructuringErrors){startPos=this.start;startLoc=this.startLoc;}if(!isPattern){isGenerator=this.eat(types$2.star);}}var containsEsc=this.containsEsc;this.parsePropertyName(prop);if(!isPattern&&!containsEsc&&this.options.ecmaVersion>=8&&!isGenerator&&this.isAsyncProp(prop)){isAsync=true;isGenerator=this.options.ecmaVersion>=9&&this.eat(types$2.star);this.parsePropertyName(prop,refDestructuringErrors);}else{isAsync=false;}this.parsePropertyValue(prop,isPattern,isGenerator,isAsync,startPos,startLoc,refDestructuringErrors,containsEsc);return this.finishNode(prop,"Property");};pp$3.parsePropertyValue=function(prop,isPattern,isGenerator,isAsync,startPos,startLoc,refDestructuringErrors,containsEsc){if((isGenerator||isAsync)&&this.type===types$2.colon){this.unexpected();}if(this.eat(types$2.colon)){prop.value=isPattern?this.parseMaybeDefault(this.start,this.startLoc):this.parseMaybeAssign(false,refDestructuringErrors);prop.kind="init";}else if(this.options.ecmaVersion>=6&&this.type===types$2.parenL){if(isPattern){this.unexpected();}prop.kind="init";prop.method=true;prop.value=this.parseMethod(isGenerator,isAsync);}else if(!isPattern&&!containsEsc&&this.options.ecmaVersion>=5&&!prop.computed&&prop.key.type==="Identifier"&&(prop.key.name==="get"||prop.key.name==="set")&&this.type!==types$2.comma&&this.type!==types$2.braceR){if(isGenerator||isAsync){this.unexpected();}prop.kind=prop.key.name;this.parsePropertyName(prop);prop.value=this.parseMethod(false);var paramCount=prop.kind==="get"?0:1;if(prop.value.params.length!==paramCount){var start=prop.value.start;if(prop.kind==="get"){this.raiseRecoverable(start,"getter should have no params");}else{this.raiseRecoverable(start,"setter should have exactly one param");}}else{if(prop.kind==="set"&&prop.value.params[0].type==="RestElement"){this.raiseRecoverable(prop.value.params[0].start,"Setter cannot use rest params");}}}else if(this.options.ecmaVersion>=6&&!prop.computed&&prop.key.type==="Identifier"){if(isGenerator||isAsync){this.unexpected();}this.checkUnreserved(prop.key);if(prop.key.name==="await"&&!this.awaitIdentPos){this.awaitIdentPos=startPos;}prop.kind="init";if(isPattern){prop.value=this.parseMaybeDefault(startPos,startLoc,prop.key);}else if(this.type===types$2.eq&&refDestructuringErrors){if(refDestructuringErrors.shorthandAssign<0){refDestructuringErrors.shorthandAssign=this.start;}prop.value=this.parseMaybeDefault(startPos,startLoc,prop.key);}else{prop.value=prop.key;}prop.shorthand=true;}else{this.unexpected();}};pp$3.parsePropertyName=function(prop){if(this.options.ecmaVersion>=6){if(this.eat(types$2.bracketL)){prop.computed=true;prop.key=this.parseMaybeAssign();this.expect(types$2.bracketR);return prop.key;}else{prop.computed=false;}}return prop.key=this.type===types$2.num||this.type===types$2.string?this.parseExprAtom():this.parseIdent(true);};// Initialize empty function node.
  pp$3.initFunction=function(node){node.id=null;if(this.options.ecmaVersion>=6){node.generator=node.expression=false;}if(this.options.ecmaVersion>=8){node.async=false;}};// Parse object or class method.
  pp$3.parseMethod=function(isGenerator,isAsync,allowDirectSuper){var node=this.startNode(),oldYieldPos=this.yieldPos,oldAwaitPos=this.awaitPos,oldAwaitIdentPos=this.awaitIdentPos;this.initFunction(node);if(this.options.ecmaVersion>=6){node.generator=isGenerator;}if(this.options.ecmaVersion>=8){node.async=!!isAsync;}this.yieldPos=0;this.awaitPos=0;this.awaitIdentPos=0;this.enterScope(functionFlags(isAsync,node.generator)|SCOPE_SUPER|(allowDirectSuper?SCOPE_DIRECT_SUPER:0));this.expect(types$2.parenL);node.params=this.parseBindingList(types$2.parenR,false,this.options.ecmaVersion>=8);this.checkYieldAwaitInDefaultParams();this.parseFunctionBody(node,false,true);this.yieldPos=oldYieldPos;this.awaitPos=oldAwaitPos;this.awaitIdentPos=oldAwaitIdentPos;return this.finishNode(node,"FunctionExpression");};// Parse arrow function expression with given parameters.
  pp$3.parseArrowExpression=function(node,params,isAsync){var oldYieldPos=this.yieldPos,oldAwaitPos=this.awaitPos,oldAwaitIdentPos=this.awaitIdentPos;this.enterScope(functionFlags(isAsync,false)|SCOPE_ARROW);this.initFunction(node);if(this.options.ecmaVersion>=8){node.async=!!isAsync;}this.yieldPos=0;this.awaitPos=0;this.awaitIdentPos=0;node.params=this.toAssignableList(params,true);this.parseFunctionBody(node,true,false);this.yieldPos=oldYieldPos;this.awaitPos=oldAwaitPos;this.awaitIdentPos=oldAwaitIdentPos;return this.finishNode(node,"ArrowFunctionExpression");};// Parse function body and check parameters.
  pp$3.parseFunctionBody=function(node,isArrowFunction,isMethod){var isExpression=isArrowFunction&&this.type!==types$2.braceL;var oldStrict=this.strict,useStrict=false;if(isExpression){node.body=this.parseMaybeAssign();node.expression=true;this.checkParams(node,false);}else{var nonSimple=this.options.ecmaVersion>=7&&!this.isSimpleParamList(node.params);if(!oldStrict||nonSimple){useStrict=this.strictDirective(this.end);// If this is a strict mode function, verify that argument names
  // are not repeated, and it does not try to bind the words `eval`
  // or `arguments`.
  if(useStrict&&nonSimple){this.raiseRecoverable(node.start,"Illegal 'use strict' directive in function with non-simple parameter list");}}// Start a new scope with regard to labels and the `inFunction`
  // flag (restore them to their old value afterwards).
  var oldLabels=this.labels;this.labels=[];if(useStrict){this.strict=true;}// Add the params to varDeclaredNames to ensure that an error is thrown
  // if a let/const declaration in the function clashes with one of the params.
  this.checkParams(node,!oldStrict&&!useStrict&&!isArrowFunction&&!isMethod&&this.isSimpleParamList(node.params));node.body=this.parseBlock(false);node.expression=false;this.adaptDirectivePrologue(node.body.body);this.labels=oldLabels;}this.exitScope();// Ensure the function name isn't a forbidden identifier in strict mode, e.g. 'eval'
  if(this.strict&&node.id){this.checkLVal(node.id,BIND_OUTSIDE);}this.strict=oldStrict;};pp$3.isSimpleParamList=function(params){for(var i=0,list=params;i<list.length;i+=1){var param=list[i];if(param.type!=="Identifier"){return false;}}return true;};// Checks function params for various disallowed patterns such as using "eval"
  // or "arguments" and duplicate parameters.
  pp$3.checkParams=function(node,allowDuplicates){var nameHash={};for(var i=0,list=node.params;i<list.length;i+=1){var param=list[i];this.checkLVal(param,BIND_VAR,allowDuplicates?null:nameHash);}};// Parses a comma-separated list of expressions, and returns them as
  // an array. `close` is the token type that ends the list, and
  // `allowEmpty` can be turned on to allow subsequent commas with
  // nothing in between them to be parsed as `null` (which is needed
  // for array literals).
  pp$3.parseExprList=function(close,allowTrailingComma,allowEmpty,refDestructuringErrors){var elts=[],first=true;while(!this.eat(close)){if(!first){this.expect(types$2.comma);if(allowTrailingComma&&this.afterTrailingComma(close)){break;}}else{first=false;}var elt=void 0;if(allowEmpty&&this.type===types$2.comma){elt=null;}else if(this.type===types$2.ellipsis){elt=this.parseSpread(refDestructuringErrors);if(refDestructuringErrors&&this.type===types$2.comma&&refDestructuringErrors.trailingComma<0){refDestructuringErrors.trailingComma=this.start;}}else{elt=this.parseMaybeAssign(false,refDestructuringErrors);}elts.push(elt);}return elts;};pp$3.checkUnreserved=function(ref){var start=ref.start;var end=ref.end;var name=ref.name;if(this.inGenerator&&name==="yield"){this.raiseRecoverable(start,"Cannot use 'yield' as identifier inside a generator");}if(this.inAsync&&name==="await"){this.raiseRecoverable(start,"Cannot use 'await' as identifier inside an async function");}if(this.keywords.test(name)){this.raise(start,"Unexpected keyword '"+name+"'");}if(this.options.ecmaVersion<6&&this.input.slice(start,end).indexOf("\\")!==-1){return;}var re=this.strict?this.reservedWordsStrict:this.reservedWords;if(re.test(name)){if(!this.inAsync&&name==="await"){this.raiseRecoverable(start,"Cannot use keyword 'await' outside an async function");}this.raiseRecoverable(start,"The keyword '"+name+"' is reserved");}};// Parse the next token as an identifier. If `liberal` is true (used
  // when parsing properties), it will also convert keywords into
  // identifiers.
  pp$3.parseIdent=function(liberal,isBinding){var node=this.startNode();if(liberal&&this.options.allowReserved==="never"){liberal=false;}if(this.type===types$2.name){node.name=this.value;}else if(this.type.keyword){node.name=this.type.keyword;// To fix https://github.com/acornjs/acorn/issues/575
  // `class` and `function` keywords push new context into this.context.
  // But there is no chance to pop the context if the keyword is consumed as an identifier such as a property name.
  // If the previous token is a dot, this does not apply because the context-managing code already ignored the keyword
  if((node.name==="class"||node.name==="function")&&(this.lastTokEnd!==this.lastTokStart+1||this.input.charCodeAt(this.lastTokStart)!==46)){this.context.pop();}}else{this.unexpected();}this.next();this.finishNode(node,"Identifier");if(!liberal){this.checkUnreserved(node);if(node.name==="await"&&!this.awaitIdentPos){this.awaitIdentPos=node.start;}}return node;};// Parses yield expression inside generator.
  pp$3.parseYield=function(noIn){if(!this.yieldPos){this.yieldPos=this.start;}var node=this.startNode();this.next();if(this.type===types$2.semi||this.canInsertSemicolon()||this.type!==types$2.star&&!this.type.startsExpr){node.delegate=false;node.argument=null;}else{node.delegate=this.eat(types$2.star);node.argument=this.parseMaybeAssign(noIn);}return this.finishNode(node,"YieldExpression");};pp$3.parseAwait=function(){if(!this.awaitPos){this.awaitPos=this.start;}var node=this.startNode();this.next();node.argument=this.parseMaybeUnary(null,true);return this.finishNode(node,"AwaitExpression");};var pp$4=Parser.prototype;// This function is used to raise exceptions on parse errors. It
  // takes an offset integer (into the current `input`) to indicate
  // the location of the error, attaches the position to the end
  // of the error message, and then raises a `SyntaxError` with that
  // message.
  pp$4.raise=function(pos,message){var loc=getLineInfo(this.input,pos);message+=" ("+loc.line+":"+loc.column+")";var err=new SyntaxError(message);err.pos=pos;err.loc=loc;err.raisedAt=this.pos;throw err;};pp$4.raiseRecoverable=pp$4.raise;pp$4.curPosition=function(){if(this.options.locations){return new Position(this.curLine,this.pos-this.lineStart);}};var pp$5=Parser.prototype;var Scope=function Scope(flags){this.flags=flags;// A list of var-declared names in the current lexical scope
  this.var=[];// A list of lexically-declared names in the current lexical scope
  this.lexical=[];// A list of lexically-declared FunctionDeclaration names in the current lexical scope
  this.functions=[];};// The functions in this module keep track of declared variables in the current scope in order to detect duplicate variable names.
  pp$5.enterScope=function(flags){this.scopeStack.push(new Scope(flags));};pp$5.exitScope=function(){this.scopeStack.pop();};// The spec says:
  // > At the top level of a function, or script, function declarations are
  // > treated like var declarations rather than like lexical declarations.
  pp$5.treatFunctionsAsVarInScope=function(scope){return scope.flags&SCOPE_FUNCTION||!this.inModule&&scope.flags&SCOPE_TOP;};pp$5.declareName=function(name,bindingType,pos){var redeclared=false;if(bindingType===BIND_LEXICAL){var scope=this.currentScope();redeclared=scope.lexical.indexOf(name)>-1||scope.functions.indexOf(name)>-1||scope.var.indexOf(name)>-1;scope.lexical.push(name);if(this.inModule&&scope.flags&SCOPE_TOP){delete this.undefinedExports[name];}}else if(bindingType===BIND_SIMPLE_CATCH){var scope$1=this.currentScope();scope$1.lexical.push(name);}else if(bindingType===BIND_FUNCTION){var scope$2=this.currentScope();if(this.treatFunctionsAsVar){redeclared=scope$2.lexical.indexOf(name)>-1;}else{redeclared=scope$2.lexical.indexOf(name)>-1||scope$2.var.indexOf(name)>-1;}scope$2.functions.push(name);}else{for(var i=this.scopeStack.length-1;i>=0;--i){var scope$3=this.scopeStack[i];if(scope$3.lexical.indexOf(name)>-1&&!(scope$3.flags&SCOPE_SIMPLE_CATCH&&scope$3.lexical[0]===name)||!this.treatFunctionsAsVarInScope(scope$3)&&scope$3.functions.indexOf(name)>-1){redeclared=true;break;}scope$3.var.push(name);if(this.inModule&&scope$3.flags&SCOPE_TOP){delete this.undefinedExports[name];}if(scope$3.flags&SCOPE_VAR){break;}}}if(redeclared){this.raiseRecoverable(pos,"Identifier '"+name+"' has already been declared");}};pp$5.checkLocalExport=function(id){// scope.functions must be empty as Module code is always strict.
  if(this.scopeStack[0].lexical.indexOf(id.name)===-1&&this.scopeStack[0].var.indexOf(id.name)===-1){this.undefinedExports[id.name]=id;}};pp$5.currentScope=function(){return this.scopeStack[this.scopeStack.length-1];};pp$5.currentVarScope=function(){for(var i=this.scopeStack.length-1;;i--){var scope=this.scopeStack[i];if(scope.flags&SCOPE_VAR){return scope;}}};// Could be useful for `this`, `new.target`, `super()`, `super.property`, and `super[property]`.
  pp$5.currentThisScope=function(){for(var i=this.scopeStack.length-1;;i--){var scope=this.scopeStack[i];if(scope.flags&SCOPE_VAR&&!(scope.flags&SCOPE_ARROW)){return scope;}}};var Node=function Node(parser,pos,loc){this.type="";this.start=pos;this.end=0;if(parser.options.locations){this.loc=new SourceLocation(parser,loc);}if(parser.options.directSourceFile){this.sourceFile=parser.options.directSourceFile;}if(parser.options.ranges){this.range=[pos,0];}};// Start an AST node, attaching a start offset.
  var pp$6=Parser.prototype;pp$6.startNode=function(){return new Node(this,this.start,this.startLoc);};pp$6.startNodeAt=function(pos,loc){return new Node(this,pos,loc);};// Finish an AST node, adding `type` and `end` properties.
  function finishNodeAt(node,type,pos,loc){node.type=type;node.end=pos;if(this.options.locations){node.loc.end=loc;}if(this.options.ranges){node.range[1]=pos;}return node;}pp$6.finishNode=function(node,type){return finishNodeAt.call(this,node,type,this.lastTokEnd,this.lastTokEndLoc);};// Finish node at given position
  pp$6.finishNodeAt=function(node,type,pos,loc){return finishNodeAt.call(this,node,type,pos,loc);};// The algorithm used to determine whether a regexp can appear at a
  var TokContext=function TokContext(token,isExpr,preserveSpace,override,generator){this.token=token;this.isExpr=!!isExpr;this.preserveSpace=!!preserveSpace;this.override=override;this.generator=!!generator;};var types$1$1={b_stat:new TokContext("{",false),b_expr:new TokContext("{",true),b_tmpl:new TokContext("${",false),p_stat:new TokContext("(",false),p_expr:new TokContext("(",true),q_tmpl:new TokContext("`",true,true,function(p){return p.tryReadTemplateToken();}),f_stat:new TokContext("function",false),f_expr:new TokContext("function",true),f_expr_gen:new TokContext("function",true,false,null,true),f_gen:new TokContext("function",false,false,null,true)};var pp$7=Parser.prototype;pp$7.initialContext=function(){return [types$1$1.b_stat];};pp$7.braceIsBlock=function(prevType){var parent=this.curContext();if(parent===types$1$1.f_expr||parent===types$1$1.f_stat){return true;}if(prevType===types$2.colon&&(parent===types$1$1.b_stat||parent===types$1$1.b_expr)){return !parent.isExpr;}// The check for `tt.name && exprAllowed` detects whether we are
  // after a `yield` or `of` construct. See the `updateContext` for
  // `tt.name`.
  if(prevType===types$2._return||prevType===types$2.name&&this.exprAllowed){return lineBreak.test(this.input.slice(this.lastTokEnd,this.start));}if(prevType===types$2._else||prevType===types$2.semi||prevType===types$2.eof||prevType===types$2.parenR||prevType===types$2.arrow){return true;}if(prevType===types$2.braceL){return parent===types$1$1.b_stat;}if(prevType===types$2._var||prevType===types$2._const||prevType===types$2.name){return false;}return !this.exprAllowed;};pp$7.inGeneratorContext=function(){for(var i=this.context.length-1;i>=1;i--){var context=this.context[i];if(context.token==="function"){return context.generator;}}return false;};pp$7.updateContext=function(prevType){var update,type=this.type;if(type.keyword&&prevType===types$2.dot){this.exprAllowed=false;}else if(update=type.updateContext){update.call(this,prevType);}else{this.exprAllowed=type.beforeExpr;}};// Token-specific context update code
  types$2.parenR.updateContext=types$2.braceR.updateContext=function(){if(this.context.length===1){this.exprAllowed=true;return;}var out=this.context.pop();if(out===types$1$1.b_stat&&this.curContext().token==="function"){out=this.context.pop();}this.exprAllowed=!out.isExpr;};types$2.braceL.updateContext=function(prevType){this.context.push(this.braceIsBlock(prevType)?types$1$1.b_stat:types$1$1.b_expr);this.exprAllowed=true;};types$2.dollarBraceL.updateContext=function(){this.context.push(types$1$1.b_tmpl);this.exprAllowed=true;};types$2.parenL.updateContext=function(prevType){var statementParens=prevType===types$2._if||prevType===types$2._for||prevType===types$2._with||prevType===types$2._while;this.context.push(statementParens?types$1$1.p_stat:types$1$1.p_expr);this.exprAllowed=true;};types$2.incDec.updateContext=function(){// tokExprAllowed stays unchanged
  };types$2._function.updateContext=types$2._class.updateContext=function(prevType){if(prevType.beforeExpr&&prevType!==types$2.semi&&prevType!==types$2._else&&!(prevType===types$2._return&&lineBreak.test(this.input.slice(this.lastTokEnd,this.start)))&&!((prevType===types$2.colon||prevType===types$2.braceL)&&this.curContext()===types$1$1.b_stat)){this.context.push(types$1$1.f_expr);}else{this.context.push(types$1$1.f_stat);}this.exprAllowed=false;};types$2.backQuote.updateContext=function(){if(this.curContext()===types$1$1.q_tmpl){this.context.pop();}else{this.context.push(types$1$1.q_tmpl);}this.exprAllowed=false;};types$2.star.updateContext=function(prevType){if(prevType===types$2._function){var index=this.context.length-1;if(this.context[index]===types$1$1.f_expr){this.context[index]=types$1$1.f_expr_gen;}else{this.context[index]=types$1$1.f_gen;}}this.exprAllowed=true;};types$2.name.updateContext=function(prevType){var allowed=false;if(this.options.ecmaVersion>=6&&prevType!==types$2.dot){if(this.value==="of"&&!this.exprAllowed||this.value==="yield"&&this.inGeneratorContext()){allowed=true;}}this.exprAllowed=allowed;};// This file contains Unicode properties extracted from the ECMAScript
  // specification. The lists are extracted like so:
  // $$('#table-binary-unicode-properties > figure > table > tbody > tr > td:nth-child(1) code').map(el => el.innerText)
  // #table-binary-unicode-properties
  var ecma9BinaryProperties="ASCII ASCII_Hex_Digit AHex Alphabetic Alpha Any Assigned Bidi_Control Bidi_C Bidi_Mirrored Bidi_M Case_Ignorable CI Cased Changes_When_Casefolded CWCF Changes_When_Casemapped CWCM Changes_When_Lowercased CWL Changes_When_NFKC_Casefolded CWKCF Changes_When_Titlecased CWT Changes_When_Uppercased CWU Dash Default_Ignorable_Code_Point DI Deprecated Dep Diacritic Dia Emoji Emoji_Component Emoji_Modifier Emoji_Modifier_Base Emoji_Presentation Extender Ext Grapheme_Base Gr_Base Grapheme_Extend Gr_Ext Hex_Digit Hex IDS_Binary_Operator IDSB IDS_Trinary_Operator IDST ID_Continue IDC ID_Start IDS Ideographic Ideo Join_Control Join_C Logical_Order_Exception LOE Lowercase Lower Math Noncharacter_Code_Point NChar Pattern_Syntax Pat_Syn Pattern_White_Space Pat_WS Quotation_Mark QMark Radical Regional_Indicator RI Sentence_Terminal STerm Soft_Dotted SD Terminal_Punctuation Term Unified_Ideograph UIdeo Uppercase Upper Variation_Selector VS White_Space space XID_Continue XIDC XID_Start XIDS";var unicodeBinaryProperties={9:ecma9BinaryProperties,10:ecma9BinaryProperties+" Extended_Pictographic"};// #table-unicode-general-category-values
  var unicodeGeneralCategoryValues="Cased_Letter LC Close_Punctuation Pe Connector_Punctuation Pc Control Cc cntrl Currency_Symbol Sc Dash_Punctuation Pd Decimal_Number Nd digit Enclosing_Mark Me Final_Punctuation Pf Format Cf Initial_Punctuation Pi Letter L Letter_Number Nl Line_Separator Zl Lowercase_Letter Ll Mark M Combining_Mark Math_Symbol Sm Modifier_Letter Lm Modifier_Symbol Sk Nonspacing_Mark Mn Number N Open_Punctuation Ps Other C Other_Letter Lo Other_Number No Other_Punctuation Po Other_Symbol So Paragraph_Separator Zp Private_Use Co Punctuation P punct Separator Z Space_Separator Zs Spacing_Mark Mc Surrogate Cs Symbol S Titlecase_Letter Lt Unassigned Cn Uppercase_Letter Lu";// #table-unicode-script-values
  var ecma9ScriptValues="Adlam Adlm Ahom Ahom Anatolian_Hieroglyphs Hluw Arabic Arab Armenian Armn Avestan Avst Balinese Bali Bamum Bamu Bassa_Vah Bass Batak Batk Bengali Beng Bhaiksuki Bhks Bopomofo Bopo Brahmi Brah Braille Brai Buginese Bugi Buhid Buhd Canadian_Aboriginal Cans Carian Cari Caucasian_Albanian Aghb Chakma Cakm Cham Cham Cherokee Cher Common Zyyy Coptic Copt Qaac Cuneiform Xsux Cypriot Cprt Cyrillic Cyrl Deseret Dsrt Devanagari Deva Duployan Dupl Egyptian_Hieroglyphs Egyp Elbasan Elba Ethiopic Ethi Georgian Geor Glagolitic Glag Gothic Goth Grantha Gran Greek Grek Gujarati Gujr Gurmukhi Guru Han Hani Hangul Hang Hanunoo Hano Hatran Hatr Hebrew Hebr Hiragana Hira Imperial_Aramaic Armi Inherited Zinh Qaai Inscriptional_Pahlavi Phli Inscriptional_Parthian Prti Javanese Java Kaithi Kthi Kannada Knda Katakana Kana Kayah_Li Kali Kharoshthi Khar Khmer Khmr Khojki Khoj Khudawadi Sind Lao Laoo Latin Latn Lepcha Lepc Limbu Limb Linear_A Lina Linear_B Linb Lisu Lisu Lycian Lyci Lydian Lydi Mahajani Mahj Malayalam Mlym Mandaic Mand Manichaean Mani Marchen Marc Masaram_Gondi Gonm Meetei_Mayek Mtei Mende_Kikakui Mend Meroitic_Cursive Merc Meroitic_Hieroglyphs Mero Miao Plrd Modi Modi Mongolian Mong Mro Mroo Multani Mult Myanmar Mymr Nabataean Nbat New_Tai_Lue Talu Newa Newa Nko Nkoo Nushu Nshu Ogham Ogam Ol_Chiki Olck Old_Hungarian Hung Old_Italic Ital Old_North_Arabian Narb Old_Permic Perm Old_Persian Xpeo Old_South_Arabian Sarb Old_Turkic Orkh Oriya Orya Osage Osge Osmanya Osma Pahawh_Hmong Hmng Palmyrene Palm Pau_Cin_Hau Pauc Phags_Pa Phag Phoenician Phnx Psalter_Pahlavi Phlp Rejang Rjng Runic Runr Samaritan Samr Saurashtra Saur Sharada Shrd Shavian Shaw Siddham Sidd SignWriting Sgnw Sinhala Sinh Sora_Sompeng Sora Soyombo Soyo Sundanese Sund Syloti_Nagri Sylo Syriac Syrc Tagalog Tglg Tagbanwa Tagb Tai_Le Tale Tai_Tham Lana Tai_Viet Tavt Takri Takr Tamil Taml Tangut Tang Telugu Telu Thaana Thaa Thai Thai Tibetan Tibt Tifinagh Tfng Tirhuta Tirh Ugaritic Ugar Vai Vaii Warang_Citi Wara Yi Yiii Zanabazar_Square Zanb";var unicodeScriptValues={9:ecma9ScriptValues,10:ecma9ScriptValues+" Dogra Dogr Elymaic Elym Gunjala_Gondi Gong Hanifi_Rohingya Rohg Makasar Maka Medefaidrin Medf Nandinagari Nand Nyiakeng_Puachue_Hmong Hmnp Old_Sogdian Sogo Sogdian Sogd Wancho Wcho"};var data={};function buildUnicodeData(ecmaVersion){var d=data[ecmaVersion]={binary:wordsRegexp(unicodeBinaryProperties[ecmaVersion]+" "+unicodeGeneralCategoryValues),nonBinary:{General_Category:wordsRegexp(unicodeGeneralCategoryValues),Script:wordsRegexp(unicodeScriptValues[ecmaVersion])}};d.nonBinary.Script_Extensions=d.nonBinary.Script;d.nonBinary.gc=d.nonBinary.General_Category;d.nonBinary.sc=d.nonBinary.Script;d.nonBinary.scx=d.nonBinary.Script_Extensions;}buildUnicodeData(9);buildUnicodeData(10);var pp$8=Parser.prototype;var RegExpValidationState=function RegExpValidationState(parser){this.parser=parser;this.validFlags="gim"+(parser.options.ecmaVersion>=6?"uy":"")+(parser.options.ecmaVersion>=9?"s":"");this.unicodeProperties=data[parser.options.ecmaVersion>=10?10:parser.options.ecmaVersion];this.source="";this.flags="";this.start=0;this.switchU=false;this.switchN=false;this.pos=0;this.lastIntValue=0;this.lastStringValue="";this.lastAssertionIsQuantifiable=false;this.numCapturingParens=0;this.maxBackReference=0;this.groupNames=[];this.backReferenceNames=[];};RegExpValidationState.prototype.reset=function reset(start,pattern,flags){var unicode=flags.indexOf("u")!==-1;this.start=start|0;this.source=pattern+"";this.flags=flags;this.switchU=unicode&&this.parser.options.ecmaVersion>=6;this.switchN=unicode&&this.parser.options.ecmaVersion>=9;};RegExpValidationState.prototype.raise=function raise(message){this.parser.raiseRecoverable(this.start,"Invalid regular expression: /"+this.source+"/: "+message);};// If u flag is given, this returns the code point at the index (it combines a surrogate pair).
  // Otherwise, this returns the code unit of the index (can be a part of a surrogate pair).
  RegExpValidationState.prototype.at=function at(i){var s=this.source;var l=s.length;if(i>=l){return -1;}var c=s.charCodeAt(i);if(!this.switchU||c<=0xD7FF||c>=0xE000||i+1>=l){return c;}return (c<<10)+s.charCodeAt(i+1)-0x35FDC00;};RegExpValidationState.prototype.nextIndex=function nextIndex(i){var s=this.source;var l=s.length;if(i>=l){return l;}var c=s.charCodeAt(i);if(!this.switchU||c<=0xD7FF||c>=0xE000||i+1>=l){return i+1;}return i+2;};RegExpValidationState.prototype.current=function current(){return this.at(this.pos);};RegExpValidationState.prototype.lookahead=function lookahead(){return this.at(this.nextIndex(this.pos));};RegExpValidationState.prototype.advance=function advance(){this.pos=this.nextIndex(this.pos);};RegExpValidationState.prototype.eat=function eat(ch){if(this.current()===ch){this.advance();return true;}return false;};function codePointToString(ch){if(ch<=0xFFFF){return String.fromCharCode(ch);}ch-=0x10000;return String.fromCharCode((ch>>10)+0xD800,(ch&0x03FF)+0xDC00);}/**
  	 * Validate the flags part of a given RegExpLiteral.
  	 *
  	 * @param {RegExpValidationState} state The state to validate RegExp.
  	 * @returns {void}
  	 */pp$8.validateRegExpFlags=function(state){var validFlags=state.validFlags;var flags=state.flags;for(var i=0;i<flags.length;i++){var flag=flags.charAt(i);if(validFlags.indexOf(flag)===-1){this.raise(state.start,"Invalid regular expression flag");}if(flags.indexOf(flag,i+1)>-1){this.raise(state.start,"Duplicate regular expression flag");}}};/**
  	 * Validate the pattern part of a given RegExpLiteral.
  	 *
  	 * @param {RegExpValidationState} state The state to validate RegExp.
  	 * @returns {void}
  	 */pp$8.validateRegExpPattern=function(state){this.regexp_pattern(state);// The goal symbol for the parse is |Pattern[~U, ~N]|. If the result of
  // parsing contains a |GroupName|, reparse with the goal symbol
  // |Pattern[~U, +N]| and use this result instead. Throw a *SyntaxError*
  // exception if _P_ did not conform to the grammar, if any elements of _P_
  // were not matched by the parse, or if any Early Error conditions exist.
  if(!state.switchN&&this.options.ecmaVersion>=9&&state.groupNames.length>0){state.switchN=true;this.regexp_pattern(state);}};// https://www.ecma-international.org/ecma-262/8.0/#prod-Pattern
  pp$8.regexp_pattern=function(state){state.pos=0;state.lastIntValue=0;state.lastStringValue="";state.lastAssertionIsQuantifiable=false;state.numCapturingParens=0;state.maxBackReference=0;state.groupNames.length=0;state.backReferenceNames.length=0;this.regexp_disjunction(state);if(state.pos!==state.source.length){// Make the same messages as V8.
  if(state.eat(0x29/* ) */)){state.raise("Unmatched ')'");}if(state.eat(0x5D/* [ */)||state.eat(0x7D/* } */)){state.raise("Lone quantifier brackets");}}if(state.maxBackReference>state.numCapturingParens){state.raise("Invalid escape");}for(var i=0,list=state.backReferenceNames;i<list.length;i+=1){var name=list[i];if(state.groupNames.indexOf(name)===-1){state.raise("Invalid named capture referenced");}}};// https://www.ecma-international.org/ecma-262/8.0/#prod-Disjunction
  pp$8.regexp_disjunction=function(state){this.regexp_alternative(state);while(state.eat(0x7C/* | */)){this.regexp_alternative(state);}// Make the same message as V8.
  if(this.regexp_eatQuantifier(state,true)){state.raise("Nothing to repeat");}if(state.eat(0x7B/* { */)){state.raise("Lone quantifier brackets");}};// https://www.ecma-international.org/ecma-262/8.0/#prod-Alternative
  pp$8.regexp_alternative=function(state){while(state.pos<state.source.length&&this.regexp_eatTerm(state)){}};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-Term
  pp$8.regexp_eatTerm=function(state){if(this.regexp_eatAssertion(state)){// Handle `QuantifiableAssertion Quantifier` alternative.
  // `state.lastAssertionIsQuantifiable` is true if the last eaten Assertion
  // is a QuantifiableAssertion.
  if(state.lastAssertionIsQuantifiable&&this.regexp_eatQuantifier(state)){// Make the same message as V8.
  if(state.switchU){state.raise("Invalid quantifier");}}return true;}if(state.switchU?this.regexp_eatAtom(state):this.regexp_eatExtendedAtom(state)){this.regexp_eatQuantifier(state);return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-Assertion
  pp$8.regexp_eatAssertion=function(state){var start=state.pos;state.lastAssertionIsQuantifiable=false;// ^, $
  if(state.eat(0x5E/* ^ */)||state.eat(0x24/* $ */)){return true;}// \b \B
  if(state.eat(0x5C/* \ */)){if(state.eat(0x42/* B */)||state.eat(0x62/* b */)){return true;}state.pos=start;}// Lookahead / Lookbehind
  if(state.eat(0x28/* ( */)&&state.eat(0x3F/* ? */)){var lookbehind=false;if(this.options.ecmaVersion>=9){lookbehind=state.eat(0x3C/* < */);}if(state.eat(0x3D/* = */)||state.eat(0x21/* ! */)){this.regexp_disjunction(state);if(!state.eat(0x29/* ) */)){state.raise("Unterminated group");}state.lastAssertionIsQuantifiable=!lookbehind;return true;}}state.pos=start;return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-Quantifier
  pp$8.regexp_eatQuantifier=function(state,noError){if(noError===void 0)noError=false;if(this.regexp_eatQuantifierPrefix(state,noError)){state.eat(0x3F/* ? */);return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-QuantifierPrefix
  pp$8.regexp_eatQuantifierPrefix=function(state,noError){return state.eat(0x2A/* * */)||state.eat(0x2B/* + */)||state.eat(0x3F/* ? */)||this.regexp_eatBracedQuantifier(state,noError);};pp$8.regexp_eatBracedQuantifier=function(state,noError){var start=state.pos;if(state.eat(0x7B/* { */)){var min=0,max=-1;if(this.regexp_eatDecimalDigits(state)){min=state.lastIntValue;if(state.eat(0x2C/* , */)&&this.regexp_eatDecimalDigits(state)){max=state.lastIntValue;}if(state.eat(0x7D/* } */)){// SyntaxError in https://www.ecma-international.org/ecma-262/8.0/#sec-term
  if(max!==-1&&max<min&&!noError){state.raise("numbers out of order in {} quantifier");}return true;}}if(state.switchU&&!noError){state.raise("Incomplete quantifier");}state.pos=start;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-Atom
  pp$8.regexp_eatAtom=function(state){return this.regexp_eatPatternCharacters(state)||state.eat(0x2E/* . */)||this.regexp_eatReverseSolidusAtomEscape(state)||this.regexp_eatCharacterClass(state)||this.regexp_eatUncapturingGroup(state)||this.regexp_eatCapturingGroup(state);};pp$8.regexp_eatReverseSolidusAtomEscape=function(state){var start=state.pos;if(state.eat(0x5C/* \ */)){if(this.regexp_eatAtomEscape(state)){return true;}state.pos=start;}return false;};pp$8.regexp_eatUncapturingGroup=function(state){var start=state.pos;if(state.eat(0x28/* ( */)){if(state.eat(0x3F/* ? */)&&state.eat(0x3A/* : */)){this.regexp_disjunction(state);if(state.eat(0x29/* ) */)){return true;}state.raise("Unterminated group");}state.pos=start;}return false;};pp$8.regexp_eatCapturingGroup=function(state){if(state.eat(0x28/* ( */)){if(this.options.ecmaVersion>=9){this.regexp_groupSpecifier(state);}else if(state.current()===0x3F/* ? */){state.raise("Invalid group");}this.regexp_disjunction(state);if(state.eat(0x29/* ) */)){state.numCapturingParens+=1;return true;}state.raise("Unterminated group");}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-ExtendedAtom
  pp$8.regexp_eatExtendedAtom=function(state){return state.eat(0x2E/* . */)||this.regexp_eatReverseSolidusAtomEscape(state)||this.regexp_eatCharacterClass(state)||this.regexp_eatUncapturingGroup(state)||this.regexp_eatCapturingGroup(state)||this.regexp_eatInvalidBracedQuantifier(state)||this.regexp_eatExtendedPatternCharacter(state);};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-InvalidBracedQuantifier
  pp$8.regexp_eatInvalidBracedQuantifier=function(state){if(this.regexp_eatBracedQuantifier(state,true)){state.raise("Nothing to repeat");}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-SyntaxCharacter
  pp$8.regexp_eatSyntaxCharacter=function(state){var ch=state.current();if(isSyntaxCharacter(ch)){state.lastIntValue=ch;state.advance();return true;}return false;};function isSyntaxCharacter(ch){return ch===0x24/* $ */||ch>=0x28/* ( */&&ch<=0x2B/* + */||ch===0x2E/* . */||ch===0x3F/* ? */||ch>=0x5B/* [ */&&ch<=0x5E/* ^ */||ch>=0x7B/* { */&&ch<=0x7D/* } */;}// https://www.ecma-international.org/ecma-262/8.0/#prod-PatternCharacter
  // But eat eager.
  pp$8.regexp_eatPatternCharacters=function(state){var start=state.pos;var ch=0;while((ch=state.current())!==-1&&!isSyntaxCharacter(ch)){state.advance();}return state.pos!==start;};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-ExtendedPatternCharacter
  pp$8.regexp_eatExtendedPatternCharacter=function(state){var ch=state.current();if(ch!==-1&&ch!==0x24/* $ */&&!(ch>=0x28/* ( */&&ch<=0x2B/* + */)&&ch!==0x2E/* . */&&ch!==0x3F/* ? */&&ch!==0x5B/* [ */&&ch!==0x5E/* ^ */&&ch!==0x7C/* | */){state.advance();return true;}return false;};// GroupSpecifier[U] ::
  //   [empty]
  //   `?` GroupName[?U]
  pp$8.regexp_groupSpecifier=function(state){if(state.eat(0x3F/* ? */)){if(this.regexp_eatGroupName(state)){if(state.groupNames.indexOf(state.lastStringValue)!==-1){state.raise("Duplicate capture group name");}state.groupNames.push(state.lastStringValue);return;}state.raise("Invalid group");}};// GroupName[U] ::
  //   `<` RegExpIdentifierName[?U] `>`
  // Note: this updates `state.lastStringValue` property with the eaten name.
  pp$8.regexp_eatGroupName=function(state){state.lastStringValue="";if(state.eat(0x3C/* < */)){if(this.regexp_eatRegExpIdentifierName(state)&&state.eat(0x3E/* > */)){return true;}state.raise("Invalid capture group name");}return false;};// RegExpIdentifierName[U] ::
  //   RegExpIdentifierStart[?U]
  //   RegExpIdentifierName[?U] RegExpIdentifierPart[?U]
  // Note: this updates `state.lastStringValue` property with the eaten name.
  pp$8.regexp_eatRegExpIdentifierName=function(state){state.lastStringValue="";if(this.regexp_eatRegExpIdentifierStart(state)){state.lastStringValue+=codePointToString(state.lastIntValue);while(this.regexp_eatRegExpIdentifierPart(state)){state.lastStringValue+=codePointToString(state.lastIntValue);}return true;}return false;};// RegExpIdentifierStart[U] ::
  //   UnicodeIDStart
  //   `$`
  //   `_`
  //   `\` RegExpUnicodeEscapeSequence[?U]
  pp$8.regexp_eatRegExpIdentifierStart=function(state){var start=state.pos;var ch=state.current();state.advance();if(ch===0x5C/* \ */&&this.regexp_eatRegExpUnicodeEscapeSequence(state)){ch=state.lastIntValue;}if(isRegExpIdentifierStart(ch)){state.lastIntValue=ch;return true;}state.pos=start;return false;};function isRegExpIdentifierStart(ch){return isIdentifierStart(ch,true)||ch===0x24/* $ */||ch===0x5F;/* _ */}// RegExpIdentifierPart[U] ::
  //   UnicodeIDContinue
  //   `$`
  //   `_`
  //   `\` RegExpUnicodeEscapeSequence[?U]
  //   <ZWNJ>
  //   <ZWJ>
  pp$8.regexp_eatRegExpIdentifierPart=function(state){var start=state.pos;var ch=state.current();state.advance();if(ch===0x5C/* \ */&&this.regexp_eatRegExpUnicodeEscapeSequence(state)){ch=state.lastIntValue;}if(isRegExpIdentifierPart(ch)){state.lastIntValue=ch;return true;}state.pos=start;return false;};function isRegExpIdentifierPart(ch){return isIdentifierChar(ch,true)||ch===0x24/* $ */||ch===0x5F/* _ */||ch===0x200C/* <ZWNJ> */||ch===0x200D;/* <ZWJ> */}// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-AtomEscape
  pp$8.regexp_eatAtomEscape=function(state){if(this.regexp_eatBackReference(state)||this.regexp_eatCharacterClassEscape(state)||this.regexp_eatCharacterEscape(state)||state.switchN&&this.regexp_eatKGroupName(state)){return true;}if(state.switchU){// Make the same message as V8.
  if(state.current()===0x63/* c */){state.raise("Invalid unicode escape");}state.raise("Invalid escape");}return false;};pp$8.regexp_eatBackReference=function(state){var start=state.pos;if(this.regexp_eatDecimalEscape(state)){var n=state.lastIntValue;if(state.switchU){// For SyntaxError in https://www.ecma-international.org/ecma-262/8.0/#sec-atomescape
  if(n>state.maxBackReference){state.maxBackReference=n;}return true;}if(n<=state.numCapturingParens){return true;}state.pos=start;}return false;};pp$8.regexp_eatKGroupName=function(state){if(state.eat(0x6B/* k */)){if(this.regexp_eatGroupName(state)){state.backReferenceNames.push(state.lastStringValue);return true;}state.raise("Invalid named reference");}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-CharacterEscape
  pp$8.regexp_eatCharacterEscape=function(state){return this.regexp_eatControlEscape(state)||this.regexp_eatCControlLetter(state)||this.regexp_eatZero(state)||this.regexp_eatHexEscapeSequence(state)||this.regexp_eatRegExpUnicodeEscapeSequence(state)||!state.switchU&&this.regexp_eatLegacyOctalEscapeSequence(state)||this.regexp_eatIdentityEscape(state);};pp$8.regexp_eatCControlLetter=function(state){var start=state.pos;if(state.eat(0x63/* c */)){if(this.regexp_eatControlLetter(state)){return true;}state.pos=start;}return false;};pp$8.regexp_eatZero=function(state){if(state.current()===0x30/* 0 */&&!isDecimalDigit(state.lookahead())){state.lastIntValue=0;state.advance();return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-ControlEscape
  pp$8.regexp_eatControlEscape=function(state){var ch=state.current();if(ch===0x74/* t */){state.lastIntValue=0x09;/* \t */state.advance();return true;}if(ch===0x6E/* n */){state.lastIntValue=0x0A;/* \n */state.advance();return true;}if(ch===0x76/* v */){state.lastIntValue=0x0B;/* \v */state.advance();return true;}if(ch===0x66/* f */){state.lastIntValue=0x0C;/* \f */state.advance();return true;}if(ch===0x72/* r */){state.lastIntValue=0x0D;/* \r */state.advance();return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-ControlLetter
  pp$8.regexp_eatControlLetter=function(state){var ch=state.current();if(isControlLetter(ch)){state.lastIntValue=ch%0x20;state.advance();return true;}return false;};function isControlLetter(ch){return ch>=0x41/* A */&&ch<=0x5A/* Z */||ch>=0x61/* a */&&ch<=0x7A/* z */;}// https://www.ecma-international.org/ecma-262/8.0/#prod-RegExpUnicodeEscapeSequence
  pp$8.regexp_eatRegExpUnicodeEscapeSequence=function(state){var start=state.pos;if(state.eat(0x75/* u */)){if(this.regexp_eatFixedHexDigits(state,4)){var lead=state.lastIntValue;if(state.switchU&&lead>=0xD800&&lead<=0xDBFF){var leadSurrogateEnd=state.pos;if(state.eat(0x5C/* \ */)&&state.eat(0x75/* u */)&&this.regexp_eatFixedHexDigits(state,4)){var trail=state.lastIntValue;if(trail>=0xDC00&&trail<=0xDFFF){state.lastIntValue=(lead-0xD800)*0x400+(trail-0xDC00)+0x10000;return true;}}state.pos=leadSurrogateEnd;state.lastIntValue=lead;}return true;}if(state.switchU&&state.eat(0x7B/* { */)&&this.regexp_eatHexDigits(state)&&state.eat(0x7D/* } */)&&isValidUnicode(state.lastIntValue)){return true;}if(state.switchU){state.raise("Invalid unicode escape");}state.pos=start;}return false;};function isValidUnicode(ch){return ch>=0&&ch<=0x10FFFF;}// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-IdentityEscape
  pp$8.regexp_eatIdentityEscape=function(state){if(state.switchU){if(this.regexp_eatSyntaxCharacter(state)){return true;}if(state.eat(0x2F/* / */)){state.lastIntValue=0x2F;/* / */return true;}return false;}var ch=state.current();if(ch!==0x63/* c */&&(!state.switchN||ch!==0x6B/* k */)){state.lastIntValue=ch;state.advance();return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-DecimalEscape
  pp$8.regexp_eatDecimalEscape=function(state){state.lastIntValue=0;var ch=state.current();if(ch>=0x31/* 1 */&&ch<=0x39/* 9 */){do{state.lastIntValue=10*state.lastIntValue+(ch-0x30/* 0 */);state.advance();}while((ch=state.current())>=0x30/* 0 */&&ch<=0x39/* 9 */);return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-CharacterClassEscape
  pp$8.regexp_eatCharacterClassEscape=function(state){var ch=state.current();if(isCharacterClassEscape(ch)){state.lastIntValue=-1;state.advance();return true;}if(state.switchU&&this.options.ecmaVersion>=9&&(ch===0x50/* P */||ch===0x70/* p */)){state.lastIntValue=-1;state.advance();if(state.eat(0x7B/* { */)&&this.regexp_eatUnicodePropertyValueExpression(state)&&state.eat(0x7D/* } */)){return true;}state.raise("Invalid property name");}return false;};function isCharacterClassEscape(ch){return ch===0x64/* d */||ch===0x44/* D */||ch===0x73/* s */||ch===0x53/* S */||ch===0x77/* w */||ch===0x57/* W */;}// UnicodePropertyValueExpression ::
  //   UnicodePropertyName `=` UnicodePropertyValue
  //   LoneUnicodePropertyNameOrValue
  pp$8.regexp_eatUnicodePropertyValueExpression=function(state){var start=state.pos;// UnicodePropertyName `=` UnicodePropertyValue
  if(this.regexp_eatUnicodePropertyName(state)&&state.eat(0x3D/* = */)){var name=state.lastStringValue;if(this.regexp_eatUnicodePropertyValue(state)){var value=state.lastStringValue;this.regexp_validateUnicodePropertyNameAndValue(state,name,value);return true;}}state.pos=start;// LoneUnicodePropertyNameOrValue
  if(this.regexp_eatLoneUnicodePropertyNameOrValue(state)){var nameOrValue=state.lastStringValue;this.regexp_validateUnicodePropertyNameOrValue(state,nameOrValue);return true;}return false;};pp$8.regexp_validateUnicodePropertyNameAndValue=function(state,name,value){if(!has(state.unicodeProperties.nonBinary,name)){state.raise("Invalid property name");}if(!state.unicodeProperties.nonBinary[name].test(value)){state.raise("Invalid property value");}};pp$8.regexp_validateUnicodePropertyNameOrValue=function(state,nameOrValue){if(!state.unicodeProperties.binary.test(nameOrValue)){state.raise("Invalid property name");}};// UnicodePropertyName ::
  //   UnicodePropertyNameCharacters
  pp$8.regexp_eatUnicodePropertyName=function(state){var ch=0;state.lastStringValue="";while(isUnicodePropertyNameCharacter(ch=state.current())){state.lastStringValue+=codePointToString(ch);state.advance();}return state.lastStringValue!=="";};function isUnicodePropertyNameCharacter(ch){return isControlLetter(ch)||ch===0x5F;/* _ */}// UnicodePropertyValue ::
  //   UnicodePropertyValueCharacters
  pp$8.regexp_eatUnicodePropertyValue=function(state){var ch=0;state.lastStringValue="";while(isUnicodePropertyValueCharacter(ch=state.current())){state.lastStringValue+=codePointToString(ch);state.advance();}return state.lastStringValue!=="";};function isUnicodePropertyValueCharacter(ch){return isUnicodePropertyNameCharacter(ch)||isDecimalDigit(ch);}// LoneUnicodePropertyNameOrValue ::
  //   UnicodePropertyValueCharacters
  pp$8.regexp_eatLoneUnicodePropertyNameOrValue=function(state){return this.regexp_eatUnicodePropertyValue(state);};// https://www.ecma-international.org/ecma-262/8.0/#prod-CharacterClass
  pp$8.regexp_eatCharacterClass=function(state){if(state.eat(0x5B/* [ */)){state.eat(0x5E/* ^ */);this.regexp_classRanges(state);if(state.eat(0x5D/* [ */)){return true;}// Unreachable since it threw "unterminated regular expression" error before.
  state.raise("Unterminated character class");}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-ClassRanges
  // https://www.ecma-international.org/ecma-262/8.0/#prod-NonemptyClassRanges
  // https://www.ecma-international.org/ecma-262/8.0/#prod-NonemptyClassRangesNoDash
  pp$8.regexp_classRanges=function(state){while(this.regexp_eatClassAtom(state)){var left=state.lastIntValue;if(state.eat(0x2D/* - */)&&this.regexp_eatClassAtom(state)){var right=state.lastIntValue;if(state.switchU&&(left===-1||right===-1)){state.raise("Invalid character class");}if(left!==-1&&right!==-1&&left>right){state.raise("Range out of order in character class");}}}};// https://www.ecma-international.org/ecma-262/8.0/#prod-ClassAtom
  // https://www.ecma-international.org/ecma-262/8.0/#prod-ClassAtomNoDash
  pp$8.regexp_eatClassAtom=function(state){var start=state.pos;if(state.eat(0x5C/* \ */)){if(this.regexp_eatClassEscape(state)){return true;}if(state.switchU){// Make the same message as V8.
  var ch$1=state.current();if(ch$1===0x63/* c */||isOctalDigit(ch$1)){state.raise("Invalid class escape");}state.raise("Invalid escape");}state.pos=start;}var ch=state.current();if(ch!==0x5D/* [ */){state.lastIntValue=ch;state.advance();return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-ClassEscape
  pp$8.regexp_eatClassEscape=function(state){var start=state.pos;if(state.eat(0x62/* b */)){state.lastIntValue=0x08;/* <BS> */return true;}if(state.switchU&&state.eat(0x2D/* - */)){state.lastIntValue=0x2D;/* - */return true;}if(!state.switchU&&state.eat(0x63/* c */)){if(this.regexp_eatClassControlLetter(state)){return true;}state.pos=start;}return this.regexp_eatCharacterClassEscape(state)||this.regexp_eatCharacterEscape(state);};// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-ClassControlLetter
  pp$8.regexp_eatClassControlLetter=function(state){var ch=state.current();if(isDecimalDigit(ch)||ch===0x5F/* _ */){state.lastIntValue=ch%0x20;state.advance();return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-HexEscapeSequence
  pp$8.regexp_eatHexEscapeSequence=function(state){var start=state.pos;if(state.eat(0x78/* x */)){if(this.regexp_eatFixedHexDigits(state,2)){return true;}if(state.switchU){state.raise("Invalid escape");}state.pos=start;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-DecimalDigits
  pp$8.regexp_eatDecimalDigits=function(state){var start=state.pos;var ch=0;state.lastIntValue=0;while(isDecimalDigit(ch=state.current())){state.lastIntValue=10*state.lastIntValue+(ch-0x30/* 0 */);state.advance();}return state.pos!==start;};function isDecimalDigit(ch){return ch>=0x30/* 0 */&&ch<=0x39;/* 9 */}// https://www.ecma-international.org/ecma-262/8.0/#prod-HexDigits
  pp$8.regexp_eatHexDigits=function(state){var start=state.pos;var ch=0;state.lastIntValue=0;while(isHexDigit(ch=state.current())){state.lastIntValue=16*state.lastIntValue+hexToInt(ch);state.advance();}return state.pos!==start;};function isHexDigit(ch){return ch>=0x30/* 0 */&&ch<=0x39/* 9 */||ch>=0x41/* A */&&ch<=0x46/* F */||ch>=0x61/* a */&&ch<=0x66/* f */;}function hexToInt(ch){if(ch>=0x41/* A */&&ch<=0x46/* F */){return 10+(ch-0x41/* A */);}if(ch>=0x61/* a */&&ch<=0x66/* f */){return 10+(ch-0x61/* a */);}return ch-0x30;/* 0 */}// https://www.ecma-international.org/ecma-262/8.0/#prod-annexB-LegacyOctalEscapeSequence
  // Allows only 0-377(octal) i.e. 0-255(decimal).
  pp$8.regexp_eatLegacyOctalEscapeSequence=function(state){if(this.regexp_eatOctalDigit(state)){var n1=state.lastIntValue;if(this.regexp_eatOctalDigit(state)){var n2=state.lastIntValue;if(n1<=3&&this.regexp_eatOctalDigit(state)){state.lastIntValue=n1*64+n2*8+state.lastIntValue;}else{state.lastIntValue=n1*8+n2;}}else{state.lastIntValue=n1;}return true;}return false;};// https://www.ecma-international.org/ecma-262/8.0/#prod-OctalDigit
  pp$8.regexp_eatOctalDigit=function(state){var ch=state.current();if(isOctalDigit(ch)){state.lastIntValue=ch-0x30;/* 0 */state.advance();return true;}state.lastIntValue=0;return false;};function isOctalDigit(ch){return ch>=0x30/* 0 */&&ch<=0x37;/* 7 */}// https://www.ecma-international.org/ecma-262/8.0/#prod-Hex4Digits
  // https://www.ecma-international.org/ecma-262/8.0/#prod-HexDigit
  // And HexDigit HexDigit in https://www.ecma-international.org/ecma-262/8.0/#prod-HexEscapeSequence
  pp$8.regexp_eatFixedHexDigits=function(state,length){var start=state.pos;state.lastIntValue=0;for(var i=0;i<length;++i){var ch=state.current();if(!isHexDigit(ch)){state.pos=start;return false;}state.lastIntValue=16*state.lastIntValue+hexToInt(ch);state.advance();}return true;};// Object type used to represent tokens. Note that normally, tokens
  // simply exist as properties on the parser object. This is only
  // used for the onToken callback and the external tokenizer.
  var Token=function Token(p){this.type=p.type;this.value=p.value;this.start=p.start;this.end=p.end;if(p.options.locations){this.loc=new SourceLocation(p,p.startLoc,p.endLoc);}if(p.options.ranges){this.range=[p.start,p.end];}};// ## Tokenizer
  var pp$9=Parser.prototype;// Move to the next token
  pp$9.next=function(){if(this.options.onToken){this.options.onToken(new Token(this));}this.lastTokEnd=this.end;this.lastTokStart=this.start;this.lastTokEndLoc=this.endLoc;this.lastTokStartLoc=this.startLoc;this.nextToken();};pp$9.getToken=function(){this.next();return new Token(this);};// If we're in an ES6 environment, make parsers iterable
  if(typeof Symbol!=="undefined"){pp$9[Symbol.iterator]=function(){var this$1=this;return {next:function next(){var token=this$1.getToken();return {done:token.type===types$2.eof,value:token};}};};}// Toggle strict mode. Re-reads the next number or string to please
  // pedantic tests (`"use strict"; 010;` should fail).
  pp$9.curContext=function(){return this.context[this.context.length-1];};// Read a single token, updating the parser object's token-related
  // properties.
  pp$9.nextToken=function(){var curContext=this.curContext();if(!curContext||!curContext.preserveSpace){this.skipSpace();}this.start=this.pos;if(this.options.locations){this.startLoc=this.curPosition();}if(this.pos>=this.input.length){return this.finishToken(types$2.eof);}if(curContext.override){return curContext.override(this);}else{this.readToken(this.fullCharCodeAtPos());}};pp$9.readToken=function(code){// Identifier or keyword. '\uXXXX' sequences are allowed in
  // identifiers, so '\' also dispatches to that.
  if(isIdentifierStart(code,this.options.ecmaVersion>=6)||code===92/* '\' */){return this.readWord();}return this.getTokenFromCode(code);};pp$9.fullCharCodeAtPos=function(){var code=this.input.charCodeAt(this.pos);if(code<=0xd7ff||code>=0xe000){return code;}var next=this.input.charCodeAt(this.pos+1);return (code<<10)+next-0x35fdc00;};pp$9.skipBlockComment=function(){var startLoc=this.options.onComment&&this.curPosition();var start=this.pos,end=this.input.indexOf("*/",this.pos+=2);if(end===-1){this.raise(this.pos-2,"Unterminated comment");}this.pos=end+2;if(this.options.locations){lineBreakG.lastIndex=start;var match;while((match=lineBreakG.exec(this.input))&&match.index<this.pos){++this.curLine;this.lineStart=match.index+match[0].length;}}if(this.options.onComment){this.options.onComment(true,this.input.slice(start+2,end),start,this.pos,startLoc,this.curPosition());}};pp$9.skipLineComment=function(startSkip){var start=this.pos;var startLoc=this.options.onComment&&this.curPosition();var ch=this.input.charCodeAt(this.pos+=startSkip);while(this.pos<this.input.length&&!isNewLine(ch)){ch=this.input.charCodeAt(++this.pos);}if(this.options.onComment){this.options.onComment(false,this.input.slice(start+startSkip,this.pos),start,this.pos,startLoc,this.curPosition());}};// Called at the start of the parse and after every token. Skips
  // whitespace and comments, and.
  pp$9.skipSpace=function(){loop:while(this.pos<this.input.length){var ch=this.input.charCodeAt(this.pos);switch(ch){case 32:case 160:// ' '
  ++this.pos;break;case 13:if(this.input.charCodeAt(this.pos+1)===10){++this.pos;}case 10:case 8232:case 8233:++this.pos;if(this.options.locations){++this.curLine;this.lineStart=this.pos;}break;case 47:// '/'
  switch(this.input.charCodeAt(this.pos+1)){case 42:// '*'
  this.skipBlockComment();break;case 47:this.skipLineComment(2);break;default:break loop;}break;default:if(ch>8&&ch<14||ch>=5760&&nonASCIIwhitespace.test(String.fromCharCode(ch))){++this.pos;}else{break loop;}}}};// Called at the end of every token. Sets `end`, `val`, and
  // maintains `context` and `exprAllowed`, and skips the space after
  // the token, so that the next one's `start` will point at the
  // right position.
  pp$9.finishToken=function(type,val){this.end=this.pos;if(this.options.locations){this.endLoc=this.curPosition();}var prevType=this.type;this.type=type;this.value=val;this.updateContext(prevType);};// ### Token reading
  // This is the function that is called to fetch the next token. It
  // is somewhat obscure, because it works in character codes rather
  // than characters, and because operator parsing has been inlined
  // into it.
  //
  // All in the name of speed.
  //
  pp$9.readToken_dot=function(){var next=this.input.charCodeAt(this.pos+1);if(next>=48&&next<=57){return this.readNumber(true);}var next2=this.input.charCodeAt(this.pos+2);if(this.options.ecmaVersion>=6&&next===46&&next2===46){// 46 = dot '.'
  this.pos+=3;return this.finishToken(types$2.ellipsis);}else{++this.pos;return this.finishToken(types$2.dot);}};pp$9.readToken_slash=function(){// '/'
  var next=this.input.charCodeAt(this.pos+1);if(this.exprAllowed){++this.pos;return this.readRegexp();}if(next===61){return this.finishOp(types$2.assign,2);}return this.finishOp(types$2.slash,1);};pp$9.readToken_mult_modulo_exp=function(code){// '%*'
  var next=this.input.charCodeAt(this.pos+1);var size=1;var tokentype=code===42?types$2.star:types$2.modulo;// exponentiation operator ** and **=
  if(this.options.ecmaVersion>=7&&code===42&&next===42){++size;tokentype=types$2.starstar;next=this.input.charCodeAt(this.pos+2);}if(next===61){return this.finishOp(types$2.assign,size+1);}return this.finishOp(tokentype,size);};pp$9.readToken_pipe_amp=function(code){// '|&'
  var next=this.input.charCodeAt(this.pos+1);if(next===code){return this.finishOp(code===124?types$2.logicalOR:types$2.logicalAND,2);}if(next===61){return this.finishOp(types$2.assign,2);}return this.finishOp(code===124?types$2.bitwiseOR:types$2.bitwiseAND,1);};pp$9.readToken_caret=function(){// '^'
  var next=this.input.charCodeAt(this.pos+1);if(next===61){return this.finishOp(types$2.assign,2);}return this.finishOp(types$2.bitwiseXOR,1);};pp$9.readToken_plus_min=function(code){// '+-'
  var next=this.input.charCodeAt(this.pos+1);if(next===code){if(next===45&&!this.inModule&&this.input.charCodeAt(this.pos+2)===62&&(this.lastTokEnd===0||lineBreak.test(this.input.slice(this.lastTokEnd,this.pos)))){// A `-->` line comment
  this.skipLineComment(3);this.skipSpace();return this.nextToken();}return this.finishOp(types$2.incDec,2);}if(next===61){return this.finishOp(types$2.assign,2);}return this.finishOp(types$2.plusMin,1);};pp$9.readToken_lt_gt=function(code){// '<>'
  var next=this.input.charCodeAt(this.pos+1);var size=1;if(next===code){size=code===62&&this.input.charCodeAt(this.pos+2)===62?3:2;if(this.input.charCodeAt(this.pos+size)===61){return this.finishOp(types$2.assign,size+1);}return this.finishOp(types$2.bitShift,size);}if(next===33&&code===60&&!this.inModule&&this.input.charCodeAt(this.pos+2)===45&&this.input.charCodeAt(this.pos+3)===45){// `<!--`, an XML-style comment that should be interpreted as a line comment
  this.skipLineComment(4);this.skipSpace();return this.nextToken();}if(next===61){size=2;}return this.finishOp(types$2.relational,size);};pp$9.readToken_eq_excl=function(code){// '=!'
  var next=this.input.charCodeAt(this.pos+1);if(next===61){return this.finishOp(types$2.equality,this.input.charCodeAt(this.pos+2)===61?3:2);}if(code===61&&next===62&&this.options.ecmaVersion>=6){// '=>'
  this.pos+=2;return this.finishToken(types$2.arrow);}return this.finishOp(code===61?types$2.eq:types$2.prefix,1);};pp$9.getTokenFromCode=function(code){switch(code){// The interpretation of a dot depends on whether it is followed
  // by a digit or another two dots.
  case 46:// '.'
  return this.readToken_dot();// Punctuation tokens.
  case 40:++this.pos;return this.finishToken(types$2.parenL);case 41:++this.pos;return this.finishToken(types$2.parenR);case 59:++this.pos;return this.finishToken(types$2.semi);case 44:++this.pos;return this.finishToken(types$2.comma);case 91:++this.pos;return this.finishToken(types$2.bracketL);case 93:++this.pos;return this.finishToken(types$2.bracketR);case 123:++this.pos;return this.finishToken(types$2.braceL);case 125:++this.pos;return this.finishToken(types$2.braceR);case 58:++this.pos;return this.finishToken(types$2.colon);case 63:++this.pos;return this.finishToken(types$2.question);case 96:// '`'
  if(this.options.ecmaVersion<6){break;}++this.pos;return this.finishToken(types$2.backQuote);case 48:// '0'
  var next=this.input.charCodeAt(this.pos+1);if(next===120||next===88){return this.readRadixNumber(16);}// '0x', '0X' - hex number
  if(this.options.ecmaVersion>=6){if(next===111||next===79){return this.readRadixNumber(8);}// '0o', '0O' - octal number
  if(next===98||next===66){return this.readRadixNumber(2);}// '0b', '0B' - binary number
  }// Anything else beginning with a digit is an integer, octal
  // number, or float.
  case 49:case 50:case 51:case 52:case 53:case 54:case 55:case 56:case 57:// 1-9
  return this.readNumber(false);// Quotes produce strings.
  case 34:case 39:// '"', "'"
  return this.readString(code);// Operators are parsed inline in tiny state machines. '=' (61) is
  // often referred to. `finishOp` simply skips the amount of
  // characters it is given as second argument, and returns a token
  // of the type given by its first argument.
  case 47:// '/'
  return this.readToken_slash();case 37:case 42:// '%*'
  return this.readToken_mult_modulo_exp(code);case 124:case 38:// '|&'
  return this.readToken_pipe_amp(code);case 94:// '^'
  return this.readToken_caret();case 43:case 45:// '+-'
  return this.readToken_plus_min(code);case 60:case 62:// '<>'
  return this.readToken_lt_gt(code);case 61:case 33:// '=!'
  return this.readToken_eq_excl(code);case 126:// '~'
  return this.finishOp(types$2.prefix,1);}this.raise(this.pos,"Unexpected character '"+codePointToString$1(code)+"'");};pp$9.finishOp=function(type,size){var str=this.input.slice(this.pos,this.pos+size);this.pos+=size;return this.finishToken(type,str);};pp$9.readRegexp=function(){var escaped,inClass,start=this.pos;for(;;){if(this.pos>=this.input.length){this.raise(start,"Unterminated regular expression");}var ch=this.input.charAt(this.pos);if(lineBreak.test(ch)){this.raise(start,"Unterminated regular expression");}if(!escaped){if(ch==="["){inClass=true;}else if(ch==="]"&&inClass){inClass=false;}else if(ch==="/"&&!inClass){break;}escaped=ch==="\\";}else{escaped=false;}++this.pos;}var pattern=this.input.slice(start,this.pos);++this.pos;var flagsStart=this.pos;var flags=this.readWord1();if(this.containsEsc){this.unexpected(flagsStart);}// Validate pattern
  var state=this.regexpState||(this.regexpState=new RegExpValidationState(this));state.reset(start,pattern,flags);this.validateRegExpFlags(state);this.validateRegExpPattern(state);// Create Literal#value property value.
  var value=null;try{value=new RegExp(pattern,flags);}catch(e){// ESTree requires null if it failed to instantiate RegExp object.
  // https://github.com/estree/estree/blob/a27003adf4fd7bfad44de9cef372a2eacd527b1c/es5.md#regexpliteral
  }return this.finishToken(types$2.regexp,{pattern:pattern,flags:flags,value:value});};// Read an integer in the given radix. Return null if zero digits
  // were read, the integer value otherwise. When `len` is given, this
  // will return `null` unless the integer has exactly `len` digits.
  pp$9.readInt=function(radix,len){var start=this.pos,total=0;for(var i=0,e=len==null?Infinity:len;i<e;++i){var code=this.input.charCodeAt(this.pos),val=void 0;if(code>=97){val=code-97+10;}// a
  else if(code>=65){val=code-65+10;}// A
  else if(code>=48&&code<=57){val=code-48;}// 0-9
  else{val=Infinity;}if(val>=radix){break;}++this.pos;total=total*radix+val;}if(this.pos===start||len!=null&&this.pos-start!==len){return null;}return total;};pp$9.readRadixNumber=function(radix){var start=this.pos;this.pos+=2;// 0x
  var val=this.readInt(radix);if(val==null){this.raise(this.start+2,"Expected number in radix "+radix);}if(this.options.ecmaVersion>=11&&this.input.charCodeAt(this.pos)===110){val=typeof BigInt!=="undefined"?BigInt(this.input.slice(start,this.pos)):null;++this.pos;}else if(isIdentifierStart(this.fullCharCodeAtPos())){this.raise(this.pos,"Identifier directly after number");}return this.finishToken(types$2.num,val);};// Read an integer, octal integer, or floating-point number.
  pp$9.readNumber=function(startsWithDot){var start=this.pos;if(!startsWithDot&&this.readInt(10)===null){this.raise(start,"Invalid number");}var octal=this.pos-start>=2&&this.input.charCodeAt(start)===48;if(octal&&this.strict){this.raise(start,"Invalid number");}if(octal&&/[89]/.test(this.input.slice(start,this.pos))){octal=false;}var next=this.input.charCodeAt(this.pos);if(!octal&&!startsWithDot&&this.options.ecmaVersion>=11&&next===110){var str$1=this.input.slice(start,this.pos);var val$1=typeof BigInt!=="undefined"?BigInt(str$1):null;++this.pos;if(isIdentifierStart(this.fullCharCodeAtPos())){this.raise(this.pos,"Identifier directly after number");}return this.finishToken(types$2.num,val$1);}if(next===46&&!octal){// '.'
  ++this.pos;this.readInt(10);next=this.input.charCodeAt(this.pos);}if((next===69||next===101)&&!octal){// 'eE'
  next=this.input.charCodeAt(++this.pos);if(next===43||next===45){++this.pos;}// '+-'
  if(this.readInt(10)===null){this.raise(start,"Invalid number");}}if(isIdentifierStart(this.fullCharCodeAtPos())){this.raise(this.pos,"Identifier directly after number");}var str=this.input.slice(start,this.pos);var val=octal?parseInt(str,8):parseFloat(str);return this.finishToken(types$2.num,val);};// Read a string value, interpreting backslash-escapes.
  pp$9.readCodePoint=function(){var ch=this.input.charCodeAt(this.pos),code;if(ch===123){// '{'
  if(this.options.ecmaVersion<6){this.unexpected();}var codePos=++this.pos;code=this.readHexChar(this.input.indexOf("}",this.pos)-this.pos);++this.pos;if(code>0x10FFFF){this.invalidStringToken(codePos,"Code point out of bounds");}}else{code=this.readHexChar(4);}return code;};function codePointToString$1(code){// UTF-16 Decoding
  if(code<=0xFFFF){return String.fromCharCode(code);}code-=0x10000;return String.fromCharCode((code>>10)+0xD800,(code&1023)+0xDC00);}pp$9.readString=function(quote){var out="",chunkStart=++this.pos;for(;;){if(this.pos>=this.input.length){this.raise(this.start,"Unterminated string constant");}var ch=this.input.charCodeAt(this.pos);if(ch===quote){break;}if(ch===92){// '\'
  out+=this.input.slice(chunkStart,this.pos);out+=this.readEscapedChar(false);chunkStart=this.pos;}else{if(isNewLine(ch,this.options.ecmaVersion>=10)){this.raise(this.start,"Unterminated string constant");}++this.pos;}}out+=this.input.slice(chunkStart,this.pos++);return this.finishToken(types$2.string,out);};// Reads template string tokens.
  var INVALID_TEMPLATE_ESCAPE_ERROR={};pp$9.tryReadTemplateToken=function(){this.inTemplateElement=true;try{this.readTmplToken();}catch(err){if(err===INVALID_TEMPLATE_ESCAPE_ERROR){this.readInvalidTemplateToken();}else{throw err;}}this.inTemplateElement=false;};pp$9.invalidStringToken=function(position,message){if(this.inTemplateElement&&this.options.ecmaVersion>=9){throw INVALID_TEMPLATE_ESCAPE_ERROR;}else{this.raise(position,message);}};pp$9.readTmplToken=function(){var out="",chunkStart=this.pos;for(;;){if(this.pos>=this.input.length){this.raise(this.start,"Unterminated template");}var ch=this.input.charCodeAt(this.pos);if(ch===96||ch===36&&this.input.charCodeAt(this.pos+1)===123){// '`', '${'
  if(this.pos===this.start&&(this.type===types$2.template||this.type===types$2.invalidTemplate)){if(ch===36){this.pos+=2;return this.finishToken(types$2.dollarBraceL);}else{++this.pos;return this.finishToken(types$2.backQuote);}}out+=this.input.slice(chunkStart,this.pos);return this.finishToken(types$2.template,out);}if(ch===92){// '\'
  out+=this.input.slice(chunkStart,this.pos);out+=this.readEscapedChar(true);chunkStart=this.pos;}else if(isNewLine(ch)){out+=this.input.slice(chunkStart,this.pos);++this.pos;switch(ch){case 13:if(this.input.charCodeAt(this.pos)===10){++this.pos;}case 10:out+="\n";break;default:out+=String.fromCharCode(ch);break;}if(this.options.locations){++this.curLine;this.lineStart=this.pos;}chunkStart=this.pos;}else{++this.pos;}}};// Reads a template token to search for the end, without validating any escape sequences
  pp$9.readInvalidTemplateToken=function(){for(;this.pos<this.input.length;this.pos++){switch(this.input[this.pos]){case"\\":++this.pos;break;case"$":if(this.input[this.pos+1]!=="{"){break;}// falls through
  case"`":return this.finishToken(types$2.invalidTemplate,this.input.slice(this.start,this.pos));// no default
  }}this.raise(this.start,"Unterminated template");};// Used to read escaped characters
  pp$9.readEscapedChar=function(inTemplate){var ch=this.input.charCodeAt(++this.pos);++this.pos;switch(ch){case 110:return "\n";// 'n' -> '\n'
  case 114:return "\r";// 'r' -> '\r'
  case 120:return String.fromCharCode(this.readHexChar(2));// 'x'
  case 117:return codePointToString$1(this.readCodePoint());// 'u'
  case 116:return "\t";// 't' -> '\t'
  case 98:return "\b";// 'b' -> '\b'
  case 118:return "\u000b";// 'v' -> '\u000b'
  case 102:return "\f";// 'f' -> '\f'
  case 13:if(this.input.charCodeAt(this.pos)===10){++this.pos;}// '\r\n'
  case 10:// ' \n'
  if(this.options.locations){this.lineStart=this.pos;++this.curLine;}return "";default:if(ch>=48&&ch<=55){var octalStr=this.input.substr(this.pos-1,3).match(/^[0-7]+/)[0];var octal=parseInt(octalStr,8);if(octal>255){octalStr=octalStr.slice(0,-1);octal=parseInt(octalStr,8);}this.pos+=octalStr.length-1;ch=this.input.charCodeAt(this.pos);if((octalStr!=="0"||ch===56||ch===57)&&(this.strict||inTemplate)){this.invalidStringToken(this.pos-1-octalStr.length,inTemplate?"Octal literal in template string":"Octal literal in strict mode");}return String.fromCharCode(octal);}if(isNewLine(ch)){// Unicode new line characters after \ get removed from output in both
  // template literals and strings
  return "";}return String.fromCharCode(ch);}};// Used to read character escape sequences ('\x', '\u', '\U').
  pp$9.readHexChar=function(len){var codePos=this.pos;var n=this.readInt(16,len);if(n===null){this.invalidStringToken(codePos,"Bad character escape sequence");}return n;};// Read an identifier, and return it as a string. Sets `this.containsEsc`
  // to whether the word contained a '\u' escape.
  //
  // Incrementally adds only escaped chars, adding other chunks as-is
  // as a micro-optimization.
  pp$9.readWord1=function(){this.containsEsc=false;var word="",first=true,chunkStart=this.pos;var astral=this.options.ecmaVersion>=6;while(this.pos<this.input.length){var ch=this.fullCharCodeAtPos();if(isIdentifierChar(ch,astral)){this.pos+=ch<=0xffff?1:2;}else if(ch===92){// "\"
  this.containsEsc=true;word+=this.input.slice(chunkStart,this.pos);var escStart=this.pos;if(this.input.charCodeAt(++this.pos)!==117)// "u"
  {this.invalidStringToken(this.pos,"Expecting Unicode escape sequence \\uXXXX");}++this.pos;var esc=this.readCodePoint();if(!(first?isIdentifierStart:isIdentifierChar)(esc,astral)){this.invalidStringToken(escStart,"Invalid Unicode escape");}word+=codePointToString$1(esc);chunkStart=this.pos;}else{break;}first=false;}return word+this.input.slice(chunkStart,this.pos);};// Read an identifier or keyword token. Will check for reserved
  // words when necessary.
  pp$9.readWord=function(){var word=this.readWord1();var type=types$2.name;if(this.keywords.test(word)){if(this.containsEsc){this.raiseRecoverable(this.start,"Escape sequence in keyword "+word);}type=keywords$1[word];}return this.finishToken(type,word);};// Acorn is a tiny, fast JavaScript parser written in JavaScript.
  var version="6.2.0";// The main exported interface (under `self.acorn` when in the
  // browser) is a `parse` function that takes a code string and
  // returns an abstract syntax tree as specified by [Mozilla parser
  // API][api].
  //
  // [api]: https://developer.mozilla.org/en-US/docs/SpiderMonkey/Parser_API
  function parse(input,options){return Parser.parse(input,options);}// This function tries to parse a single expression at a given
  // offset in a string. Useful for parsing mixed-language formats
  // that embed JavaScript expressions.
  function parseExpressionAt(input,pos,options){return Parser.parseExpressionAt(input,pos,options);}// Acorn is organized as a tokenizer and a recursive-descent parser.
  // The `tokenizer` export provides an interface to the tokenizer.
  function tokenizer(input,options){return Parser.tokenizer(input,options);}var acorn=/*#__PURE__*/Object.freeze({Node:Node,Parser:Parser,Position:Position,SourceLocation:SourceLocation,TokContext:TokContext,Token:Token,TokenType:TokenType,defaultOptions:defaultOptions,getLineInfo:getLineInfo,isIdentifierChar:isIdentifierChar,isIdentifierStart:isIdentifierStart,isNewLine:isNewLine,keywordTypes:keywords$1,lineBreak:lineBreak,lineBreakG:lineBreakG,nonASCIIwhitespace:nonASCIIwhitespace,parse:parse,parseExpressionAt:parseExpressionAt,tokContexts:types$1$1,tokTypes:types$2,tokenizer:tokenizer,version:version});var _acorn=getCjsExportFromNamespace(acorn);var lib=createCommonjsModule(function(module,exports){Object.defineProperty(exports,"__esModule",{value:true});exports.DynamicImportKey=undefined;var _createClass=function(){function defineProperties(target,props){for(var i=0;i<props.length;i++){var descriptor=props[i];descriptor.enumerable=descriptor.enumerable||false;descriptor.configurable=true;if("value"in descriptor)descriptor.writable=true;Object.defineProperty(target,descriptor.key,descriptor);}}return function(Constructor,protoProps,staticProps){if(protoProps)defineProperties(Constructor.prototype,protoProps);if(staticProps)defineProperties(Constructor,staticProps);return Constructor;};}();var _get=function(){function get(object,property,receiver){if(object===null)object=Function.prototype;var desc=Object.getOwnPropertyDescriptor(object,property);if(desc===undefined){var parent=Object.getPrototypeOf(object);if(parent===null){return undefined;}else{return get(parent,property,receiver);}}else if("value"in desc){return desc.value;}else{var getter=desc.get;if(getter===undefined){return undefined;}return getter.call(receiver);}}return get;}();exports['default']=dynamicImport;function _classCallCheck(instance,Constructor){if(!(instance instanceof Constructor)){throw new TypeError("Cannot call a class as a function");}}function _possibleConstructorReturn(self,call){if(!self){throw new ReferenceError("this hasn't been initialised - super() hasn't been called");}return call&&(typeof call==="object"||typeof call==="function")?call:self;}function _inherits(subClass,superClass){if(typeof superClass!=="function"&&superClass!==null){throw new TypeError("Super expression must either be null or a function, not "+typeof superClass);}subClass.prototype=Object.create(superClass&&superClass.prototype,{constructor:{value:subClass,enumerable:false,writable:true,configurable:true}});if(superClass)Object.setPrototypeOf?Object.setPrototypeOf(subClass,superClass):subClass.__proto__=superClass;}/* eslint-disable no-underscore-dangle */var DynamicImportKey=exports.DynamicImportKey='Import';// NOTE: This allows `yield import()` to parse correctly.
  _acorn.tokTypes._import.startsExpr=true;function parseDynamicImport(){var node=this.startNode();this.next();if(this.type!==_acorn.tokTypes.parenL){this.unexpected();}return this.finishNode(node,DynamicImportKey);}function parenAfter(){return /^(\s|\/\/.*|\/\*[^]*?\*\/)*\(/.test(this.input.slice(this.pos));}function dynamicImport(Parser){return function(_Parser){_inherits(_class,_Parser);function _class(){_classCallCheck(this,_class);return _possibleConstructorReturn(this,(_class.__proto__||Object.getPrototypeOf(_class)).apply(this,arguments));}_createClass(_class,[{key:'parseStatement',value:function(){function parseStatement(context,topLevel,exports){if(this.type===_acorn.tokTypes._import&&parenAfter.call(this)){return this.parseExpressionStatement(this.startNode(),this.parseExpression());}return _get(_class.prototype.__proto__||Object.getPrototypeOf(_class.prototype),'parseStatement',this).call(this,context,topLevel,exports);}return parseStatement;}()},{key:'parseExprAtom',value:function(){function parseExprAtom(refDestructuringErrors){if(this.type===_acorn.tokTypes._import){return parseDynamicImport.call(this);}return _get(_class.prototype.__proto__||Object.getPrototypeOf(_class.prototype),'parseExprAtom',this).call(this,refDestructuringErrors);}return parseExprAtom;}()}]);return _class;}(Parser);}});var dynamicImport=unwrapExports(lib);var lib_1=lib.DynamicImportKey;// TODO: to remove when https://github.com/acornjs/acorn/pull/834 will be merged
  /**
  	 * Parse a js source to generate the AST
  	 * @param   {string} source - javascript source
  	 * @param   {Object} options - parser options
  	 * @returns {AST} AST tree
  	 */function generateAST(source,options){return main_2$1(source,Object.assign({parser:{parse(source,opts){return Parser.extend(dynamicImport).parse(source,Object.assign({},opts,{ecmaVersion:2019}));}}},options));}var builtin={"Array":false,"ArrayBuffer":false,Atomics:false,BigInt:false,BigInt64Array:false,BigUint64Array:false,"Boolean":false,constructor:false,"DataView":false,"Date":false,"decodeURI":false,"decodeURIComponent":false,"encodeURI":false,"encodeURIComponent":false,"Error":false,"escape":false,"eval":false,"EvalError":false,"Float32Array":false,"Float64Array":false,"Function":false,globalThis:false,hasOwnProperty:false,"Infinity":false,"Int16Array":false,"Int32Array":false,"Int8Array":false,"isFinite":false,"isNaN":false,isPrototypeOf:false,"JSON":false,"Map":false,"Math":false,"NaN":false,"Number":false,"Object":false,"parseFloat":false,"parseInt":false,"Promise":false,propertyIsEnumerable:false,"Proxy":false,"RangeError":false,"ReferenceError":false,"Reflect":false,"RegExp":false,"Set":false,SharedArrayBuffer:false,"String":false,"Symbol":false,"SyntaxError":false,toLocaleString:false,toString:false,"TypeError":false,"Uint16Array":false,"Uint32Array":false,"Uint8Array":false,"Uint8ClampedArray":false,"undefined":false,"unescape":false,"URIError":false,valueOf:false,"WeakMap":false,"WeakSet":false};var es5={"Array":false,"Boolean":false,constructor:false,"Date":false,"decodeURI":false,"decodeURIComponent":false,"encodeURI":false,"encodeURIComponent":false,"Error":false,"escape":false,"eval":false,"EvalError":false,"Function":false,hasOwnProperty:false,"Infinity":false,"isFinite":false,"isNaN":false,isPrototypeOf:false,"JSON":false,"Math":false,"NaN":false,"Number":false,"Object":false,"parseFloat":false,"parseInt":false,propertyIsEnumerable:false,"RangeError":false,"ReferenceError":false,"RegExp":false,"String":false,"SyntaxError":false,toLocaleString:false,toString:false,"TypeError":false,"undefined":false,"unescape":false,"URIError":false,valueOf:false};var es2015={"Array":false,"ArrayBuffer":false,"Boolean":false,constructor:false,"DataView":false,"Date":false,"decodeURI":false,"decodeURIComponent":false,"encodeURI":false,"encodeURIComponent":false,"Error":false,"escape":false,"eval":false,"EvalError":false,"Float32Array":false,"Float64Array":false,"Function":false,hasOwnProperty:false,"Infinity":false,"Int16Array":false,"Int32Array":false,"Int8Array":false,"isFinite":false,"isNaN":false,isPrototypeOf:false,"JSON":false,"Map":false,"Math":false,"NaN":false,"Number":false,"Object":false,"parseFloat":false,"parseInt":false,"Promise":false,propertyIsEnumerable:false,"Proxy":false,"RangeError":false,"ReferenceError":false,"Reflect":false,"RegExp":false,"Set":false,"String":false,"Symbol":false,"SyntaxError":false,toLocaleString:false,toString:false,"TypeError":false,"Uint16Array":false,"Uint32Array":false,"Uint8Array":false,"Uint8ClampedArray":false,"undefined":false,"unescape":false,"URIError":false,valueOf:false,"WeakMap":false,"WeakSet":false};var es2017={"Array":false,"ArrayBuffer":false,Atomics:false,"Boolean":false,constructor:false,"DataView":false,"Date":false,"decodeURI":false,"decodeURIComponent":false,"encodeURI":false,"encodeURIComponent":false,"Error":false,"escape":false,"eval":false,"EvalError":false,"Float32Array":false,"Float64Array":false,"Function":false,hasOwnProperty:false,"Infinity":false,"Int16Array":false,"Int32Array":false,"Int8Array":false,"isFinite":false,"isNaN":false,isPrototypeOf:false,"JSON":false,"Map":false,"Math":false,"NaN":false,"Number":false,"Object":false,"parseFloat":false,"parseInt":false,"Promise":false,propertyIsEnumerable:false,"Proxy":false,"RangeError":false,"ReferenceError":false,"Reflect":false,"RegExp":false,"Set":false,SharedArrayBuffer:false,"String":false,"Symbol":false,"SyntaxError":false,toLocaleString:false,toString:false,"TypeError":false,"Uint16Array":false,"Uint32Array":false,"Uint8Array":false,"Uint8ClampedArray":false,"undefined":false,"unescape":false,"URIError":false,valueOf:false,"WeakMap":false,"WeakSet":false};var browser={AbortController:false,AbortSignal:false,addEventListener:false,alert:false,AnalyserNode:false,Animation:false,AnimationEffectReadOnly:false,AnimationEffectTiming:false,AnimationEffectTimingReadOnly:false,AnimationEvent:false,AnimationPlaybackEvent:false,AnimationTimeline:false,applicationCache:false,ApplicationCache:false,ApplicationCacheErrorEvent:false,atob:false,Attr:false,Audio:false,AudioBuffer:false,AudioBufferSourceNode:false,AudioContext:false,AudioDestinationNode:false,AudioListener:false,AudioNode:false,AudioParam:false,AudioProcessingEvent:false,AudioScheduledSourceNode:false,"AudioWorkletGlobalScope ":false,AudioWorkletNode:false,AudioWorkletProcessor:false,BarProp:false,BaseAudioContext:false,BatteryManager:false,BeforeUnloadEvent:false,BiquadFilterNode:false,Blob:false,BlobEvent:false,blur:false,BroadcastChannel:false,btoa:false,BudgetService:false,ByteLengthQueuingStrategy:false,Cache:false,caches:false,CacheStorage:false,cancelAnimationFrame:false,cancelIdleCallback:false,CanvasCaptureMediaStreamTrack:false,CanvasGradient:false,CanvasPattern:false,CanvasRenderingContext2D:false,ChannelMergerNode:false,ChannelSplitterNode:false,CharacterData:false,clearInterval:false,clearTimeout:false,clientInformation:false,ClipboardEvent:false,close:false,closed:false,CloseEvent:false,Comment:false,CompositionEvent:false,confirm:false,console:false,ConstantSourceNode:false,ConvolverNode:false,CountQueuingStrategy:false,createImageBitmap:false,Credential:false,CredentialsContainer:false,crypto:false,Crypto:false,CryptoKey:false,CSS:false,CSSConditionRule:false,CSSFontFaceRule:false,CSSGroupingRule:false,CSSImportRule:false,CSSKeyframeRule:false,CSSKeyframesRule:false,CSSMediaRule:false,CSSNamespaceRule:false,CSSPageRule:false,CSSRule:false,CSSRuleList:false,CSSStyleDeclaration:false,CSSStyleRule:false,CSSStyleSheet:false,CSSSupportsRule:false,CustomElementRegistry:false,customElements:false,CustomEvent:false,DataTransfer:false,DataTransferItem:false,DataTransferItemList:false,defaultstatus:false,defaultStatus:false,DelayNode:false,DeviceMotionEvent:false,DeviceOrientationEvent:false,devicePixelRatio:false,dispatchEvent:false,document:false,Document:false,DocumentFragment:false,DocumentType:false,DOMError:false,DOMException:false,DOMImplementation:false,DOMMatrix:false,DOMMatrixReadOnly:false,DOMParser:false,DOMPoint:false,DOMPointReadOnly:false,DOMQuad:false,DOMRect:false,DOMRectReadOnly:false,DOMStringList:false,DOMStringMap:false,DOMTokenList:false,DragEvent:false,DynamicsCompressorNode:false,Element:false,ErrorEvent:false,event:false,Event:false,EventSource:false,EventTarget:false,external:false,fetch:false,File:false,FileList:false,FileReader:false,find:false,focus:false,FocusEvent:false,FontFace:false,FontFaceSetLoadEvent:false,FormData:false,frameElement:false,frames:false,GainNode:false,Gamepad:false,GamepadButton:false,GamepadEvent:false,getComputedStyle:false,getSelection:false,HashChangeEvent:false,Headers:false,history:false,History:false,HTMLAllCollection:false,HTMLAnchorElement:false,HTMLAreaElement:false,HTMLAudioElement:false,HTMLBaseElement:false,HTMLBodyElement:false,HTMLBRElement:false,HTMLButtonElement:false,HTMLCanvasElement:false,HTMLCollection:false,HTMLContentElement:false,HTMLDataElement:false,HTMLDataListElement:false,HTMLDetailsElement:false,HTMLDialogElement:false,HTMLDirectoryElement:false,HTMLDivElement:false,HTMLDListElement:false,HTMLDocument:false,HTMLElement:false,HTMLEmbedElement:false,HTMLFieldSetElement:false,HTMLFontElement:false,HTMLFormControlsCollection:false,HTMLFormElement:false,HTMLFrameElement:false,HTMLFrameSetElement:false,HTMLHeadElement:false,HTMLHeadingElement:false,HTMLHRElement:false,HTMLHtmlElement:false,HTMLIFrameElement:false,HTMLImageElement:false,HTMLInputElement:false,HTMLLabelElement:false,HTMLLegendElement:false,HTMLLIElement:false,HTMLLinkElement:false,HTMLMapElement:false,HTMLMarqueeElement:false,HTMLMediaElement:false,HTMLMenuElement:false,HTMLMetaElement:false,HTMLMeterElement:false,HTMLModElement:false,HTMLObjectElement:false,HTMLOListElement:false,HTMLOptGroupElement:false,HTMLOptionElement:false,HTMLOptionsCollection:false,HTMLOutputElement:false,HTMLParagraphElement:false,HTMLParamElement:false,HTMLPictureElement:false,HTMLPreElement:false,HTMLProgressElement:false,HTMLQuoteElement:false,HTMLScriptElement:false,HTMLSelectElement:false,HTMLShadowElement:false,HTMLSlotElement:false,HTMLSourceElement:false,HTMLSpanElement:false,HTMLStyleElement:false,HTMLTableCaptionElement:false,HTMLTableCellElement:false,HTMLTableColElement:false,HTMLTableElement:false,HTMLTableRowElement:false,HTMLTableSectionElement:false,HTMLTemplateElement:false,HTMLTextAreaElement:false,HTMLTimeElement:false,HTMLTitleElement:false,HTMLTrackElement:false,HTMLUListElement:false,HTMLUnknownElement:false,HTMLVideoElement:false,IDBCursor:false,IDBCursorWithValue:false,IDBDatabase:false,IDBFactory:false,IDBIndex:false,IDBKeyRange:false,IDBObjectStore:false,IDBOpenDBRequest:false,IDBRequest:false,IDBTransaction:false,IDBVersionChangeEvent:false,IdleDeadline:false,IIRFilterNode:false,Image:false,ImageBitmap:false,ImageBitmapRenderingContext:false,ImageCapture:false,ImageData:false,indexedDB:false,innerHeight:false,innerWidth:false,InputEvent:false,IntersectionObserver:false,IntersectionObserverEntry:false,"Intl":false,isSecureContext:false,KeyboardEvent:false,KeyframeEffect:false,KeyframeEffectReadOnly:false,length:false,localStorage:false,location:true,Location:false,locationbar:false,matchMedia:false,MediaDeviceInfo:false,MediaDevices:false,MediaElementAudioSourceNode:false,MediaEncryptedEvent:false,MediaError:false,MediaKeyMessageEvent:false,MediaKeySession:false,MediaKeyStatusMap:false,MediaKeySystemAccess:false,MediaList:false,MediaQueryList:false,MediaQueryListEvent:false,MediaRecorder:false,MediaSettingsRange:false,MediaSource:false,MediaStream:false,MediaStreamAudioDestinationNode:false,MediaStreamAudioSourceNode:false,MediaStreamEvent:false,MediaStreamTrack:false,MediaStreamTrackEvent:false,menubar:false,MessageChannel:false,MessageEvent:false,MessagePort:false,MIDIAccess:false,MIDIConnectionEvent:false,MIDIInput:false,MIDIInputMap:false,MIDIMessageEvent:false,MIDIOutput:false,MIDIOutputMap:false,MIDIPort:false,MimeType:false,MimeTypeArray:false,MouseEvent:false,moveBy:false,moveTo:false,MutationEvent:false,MutationObserver:false,MutationRecord:false,name:false,NamedNodeMap:false,NavigationPreloadManager:false,navigator:false,Navigator:false,NetworkInformation:false,Node:false,NodeFilter:false,NodeIterator:false,NodeList:false,Notification:false,OfflineAudioCompletionEvent:false,OfflineAudioContext:false,offscreenBuffering:false,OffscreenCanvas:true,onabort:true,onafterprint:true,onanimationend:true,onanimationiteration:true,onanimationstart:true,onappinstalled:true,onauxclick:true,onbeforeinstallprompt:true,onbeforeprint:true,onbeforeunload:true,onblur:true,oncancel:true,oncanplay:true,oncanplaythrough:true,onchange:true,onclick:true,onclose:true,oncontextmenu:true,oncuechange:true,ondblclick:true,ondevicemotion:true,ondeviceorientation:true,ondeviceorientationabsolute:true,ondrag:true,ondragend:true,ondragenter:true,ondragleave:true,ondragover:true,ondragstart:true,ondrop:true,ondurationchange:true,onemptied:true,onended:true,onerror:true,onfocus:true,ongotpointercapture:true,onhashchange:true,oninput:true,oninvalid:true,onkeydown:true,onkeypress:true,onkeyup:true,onlanguagechange:true,onload:true,onloadeddata:true,onloadedmetadata:true,onloadstart:true,onlostpointercapture:true,onmessage:true,onmessageerror:true,onmousedown:true,onmouseenter:true,onmouseleave:true,onmousemove:true,onmouseout:true,onmouseover:true,onmouseup:true,onmousewheel:true,onoffline:true,ononline:true,onpagehide:true,onpageshow:true,onpause:true,onplay:true,onplaying:true,onpointercancel:true,onpointerdown:true,onpointerenter:true,onpointerleave:true,onpointermove:true,onpointerout:true,onpointerover:true,onpointerup:true,onpopstate:true,onprogress:true,onratechange:true,onrejectionhandled:true,onreset:true,onresize:true,onscroll:true,onsearch:true,onseeked:true,onseeking:true,onselect:true,onstalled:true,onstorage:true,onsubmit:true,onsuspend:true,ontimeupdate:true,ontoggle:true,ontransitionend:true,onunhandledrejection:true,onunload:true,onvolumechange:true,onwaiting:true,onwheel:true,open:false,openDatabase:false,opener:false,Option:false,origin:false,OscillatorNode:false,outerHeight:false,outerWidth:false,PageTransitionEvent:false,pageXOffset:false,pageYOffset:false,PannerNode:false,parent:false,Path2D:false,PaymentAddress:false,PaymentRequest:false,PaymentRequestUpdateEvent:false,PaymentResponse:false,performance:false,Performance:false,PerformanceEntry:false,PerformanceLongTaskTiming:false,PerformanceMark:false,PerformanceMeasure:false,PerformanceNavigation:false,PerformanceNavigationTiming:false,PerformanceObserver:false,PerformanceObserverEntryList:false,PerformancePaintTiming:false,PerformanceResourceTiming:false,PerformanceTiming:false,PeriodicWave:false,Permissions:false,PermissionStatus:false,personalbar:false,PhotoCapabilities:false,Plugin:false,PluginArray:false,PointerEvent:false,PopStateEvent:false,postMessage:false,Presentation:false,PresentationAvailability:false,PresentationConnection:false,PresentationConnectionAvailableEvent:false,PresentationConnectionCloseEvent:false,PresentationConnectionList:false,PresentationReceiver:false,PresentationRequest:false,print:false,ProcessingInstruction:false,ProgressEvent:false,PromiseRejectionEvent:false,prompt:false,PushManager:false,PushSubscription:false,PushSubscriptionOptions:false,queueMicrotask:false,RadioNodeList:false,Range:false,ReadableStream:false,registerProcessor:false,RemotePlayback:false,removeEventListener:false,Request:false,requestAnimationFrame:false,requestIdleCallback:false,resizeBy:false,ResizeObserver:false,ResizeObserverEntry:false,resizeTo:false,Response:false,RTCCertificate:false,RTCDataChannel:false,RTCDataChannelEvent:false,RTCDtlsTransport:false,RTCIceCandidate:false,RTCIceGatherer:false,RTCIceTransport:false,RTCPeerConnection:false,RTCPeerConnectionIceEvent:false,RTCRtpContributingSource:false,RTCRtpReceiver:false,RTCRtpSender:false,RTCSctpTransport:false,RTCSessionDescription:false,RTCStatsReport:false,RTCTrackEvent:false,screen:false,Screen:false,screenLeft:false,ScreenOrientation:false,screenTop:false,screenX:false,screenY:false,ScriptProcessorNode:false,scroll:false,scrollbars:false,scrollBy:false,scrollTo:false,scrollX:false,scrollY:false,SecurityPolicyViolationEvent:false,Selection:false,self:false,ServiceWorker:false,ServiceWorkerContainer:false,ServiceWorkerRegistration:false,sessionStorage:false,setInterval:false,setTimeout:false,ShadowRoot:false,SharedWorker:false,SourceBuffer:false,SourceBufferList:false,speechSynthesis:false,SpeechSynthesisEvent:false,SpeechSynthesisUtterance:false,StaticRange:false,status:false,statusbar:false,StereoPannerNode:false,stop:false,Storage:false,StorageEvent:false,StorageManager:false,styleMedia:false,StyleSheet:false,StyleSheetList:false,SubtleCrypto:false,SVGAElement:false,SVGAngle:false,SVGAnimatedAngle:false,SVGAnimatedBoolean:false,SVGAnimatedEnumeration:false,SVGAnimatedInteger:false,SVGAnimatedLength:false,SVGAnimatedLengthList:false,SVGAnimatedNumber:false,SVGAnimatedNumberList:false,SVGAnimatedPreserveAspectRatio:false,SVGAnimatedRect:false,SVGAnimatedString:false,SVGAnimatedTransformList:false,SVGAnimateElement:false,SVGAnimateMotionElement:false,SVGAnimateTransformElement:false,SVGAnimationElement:false,SVGCircleElement:false,SVGClipPathElement:false,SVGComponentTransferFunctionElement:false,SVGDefsElement:false,SVGDescElement:false,SVGDiscardElement:false,SVGElement:false,SVGEllipseElement:false,SVGFEBlendElement:false,SVGFEColorMatrixElement:false,SVGFEComponentTransferElement:false,SVGFECompositeElement:false,SVGFEConvolveMatrixElement:false,SVGFEDiffuseLightingElement:false,SVGFEDisplacementMapElement:false,SVGFEDistantLightElement:false,SVGFEDropShadowElement:false,SVGFEFloodElement:false,SVGFEFuncAElement:false,SVGFEFuncBElement:false,SVGFEFuncGElement:false,SVGFEFuncRElement:false,SVGFEGaussianBlurElement:false,SVGFEImageElement:false,SVGFEMergeElement:false,SVGFEMergeNodeElement:false,SVGFEMorphologyElement:false,SVGFEOffsetElement:false,SVGFEPointLightElement:false,SVGFESpecularLightingElement:false,SVGFESpotLightElement:false,SVGFETileElement:false,SVGFETurbulenceElement:false,SVGFilterElement:false,SVGForeignObjectElement:false,SVGGElement:false,SVGGeometryElement:false,SVGGradientElement:false,SVGGraphicsElement:false,SVGImageElement:false,SVGLength:false,SVGLengthList:false,SVGLinearGradientElement:false,SVGLineElement:false,SVGMarkerElement:false,SVGMaskElement:false,SVGMatrix:false,SVGMetadataElement:false,SVGMPathElement:false,SVGNumber:false,SVGNumberList:false,SVGPathElement:false,SVGPatternElement:false,SVGPoint:false,SVGPointList:false,SVGPolygonElement:false,SVGPolylineElement:false,SVGPreserveAspectRatio:false,SVGRadialGradientElement:false,SVGRect:false,SVGRectElement:false,SVGScriptElement:false,SVGSetElement:false,SVGStopElement:false,SVGStringList:false,SVGStyleElement:false,SVGSVGElement:false,SVGSwitchElement:false,SVGSymbolElement:false,SVGTextContentElement:false,SVGTextElement:false,SVGTextPathElement:false,SVGTextPositioningElement:false,SVGTitleElement:false,SVGTransform:false,SVGTransformList:false,SVGTSpanElement:false,SVGUnitTypes:false,SVGUseElement:false,SVGViewElement:false,TaskAttributionTiming:false,Text:false,TextDecoder:false,TextEncoder:false,TextEvent:false,TextMetrics:false,TextTrack:false,TextTrackCue:false,TextTrackCueList:false,TextTrackList:false,TimeRanges:false,toolbar:false,top:false,Touch:false,TouchEvent:false,TouchList:false,TrackEvent:false,TransitionEvent:false,TreeWalker:false,UIEvent:false,URL:false,URLSearchParams:false,ValidityState:false,visualViewport:false,VisualViewport:false,VTTCue:false,WaveShaperNode:false,WebAssembly:false,WebGL2RenderingContext:false,WebGLActiveInfo:false,WebGLBuffer:false,WebGLContextEvent:false,WebGLFramebuffer:false,WebGLProgram:false,WebGLQuery:false,WebGLRenderbuffer:false,WebGLRenderingContext:false,WebGLSampler:false,WebGLShader:false,WebGLShaderPrecisionFormat:false,WebGLSync:false,WebGLTexture:false,WebGLTransformFeedback:false,WebGLUniformLocation:false,WebGLVertexArrayObject:false,WebSocket:false,WheelEvent:false,window:false,Window:false,Worker:false,WritableStream:false,XMLDocument:false,XMLHttpRequest:false,XMLHttpRequestEventTarget:false,XMLHttpRequestUpload:false,XMLSerializer:false,XPathEvaluator:false,XPathExpression:false,XPathResult:false,XSLTProcessor:false};var worker={addEventListener:false,applicationCache:false,atob:false,Blob:false,BroadcastChannel:false,btoa:false,Cache:false,caches:false,clearInterval:false,clearTimeout:false,close:true,console:false,fetch:false,FileReaderSync:false,FormData:false,Headers:false,IDBCursor:false,IDBCursorWithValue:false,IDBDatabase:false,IDBFactory:false,IDBIndex:false,IDBKeyRange:false,IDBObjectStore:false,IDBOpenDBRequest:false,IDBRequest:false,IDBTransaction:false,IDBVersionChangeEvent:false,ImageData:false,importScripts:true,indexedDB:false,location:false,MessageChannel:false,MessagePort:false,name:false,navigator:false,Notification:false,onclose:true,onconnect:true,onerror:true,onlanguagechange:true,onmessage:true,onoffline:true,ononline:true,onrejectionhandled:true,onunhandledrejection:true,performance:false,Performance:false,PerformanceEntry:false,PerformanceMark:false,PerformanceMeasure:false,PerformanceNavigation:false,PerformanceResourceTiming:false,PerformanceTiming:false,postMessage:true,"Promise":false,queueMicrotask:false,removeEventListener:false,Request:false,Response:false,self:true,ServiceWorkerRegistration:false,setInterval:false,setTimeout:false,TextDecoder:false,TextEncoder:false,URL:false,URLSearchParams:false,WebSocket:false,Worker:false,WorkerGlobalScope:false,XMLHttpRequest:false};var node={__dirname:false,__filename:false,Buffer:false,clearImmediate:false,clearInterval:false,clearTimeout:false,console:false,exports:true,global:false,"Intl":false,module:false,process:false,queueMicrotask:false,require:false,setImmediate:false,setInterval:false,setTimeout:false,TextDecoder:false,TextEncoder:false,URL:false,URLSearchParams:false};var commonjs={exports:true,global:false,module:false,require:false};var amd={define:false,require:false};var mocha={after:false,afterEach:false,before:false,beforeEach:false,context:false,describe:false,it:false,mocha:false,run:false,setup:false,specify:false,suite:false,suiteSetup:false,suiteTeardown:false,teardown:false,test:false,xcontext:false,xdescribe:false,xit:false,xspecify:false};var jasmine={afterAll:false,afterEach:false,beforeAll:false,beforeEach:false,describe:false,expect:false,fail:false,fdescribe:false,fit:false,it:false,jasmine:false,pending:false,runs:false,spyOn:false,spyOnProperty:false,waits:false,waitsFor:false,xdescribe:false,xit:false};var jest={afterAll:false,afterEach:false,beforeAll:false,beforeEach:false,describe:false,expect:false,fdescribe:false,fit:false,it:false,jest:false,pit:false,require:false,test:false,xdescribe:false,xit:false,xtest:false};var qunit={asyncTest:false,deepEqual:false,equal:false,expect:false,module:false,notDeepEqual:false,notEqual:false,notOk:false,notPropEqual:false,notStrictEqual:false,ok:false,propEqual:false,QUnit:false,raises:false,start:false,stop:false,strictEqual:false,test:false,throws:false};var phantomjs={console:true,exports:true,phantom:true,require:true,WebPage:true};var couch={emit:false,exports:false,getRow:false,log:false,module:false,provides:false,require:false,respond:false,send:false,start:false,sum:false};var rhino={defineClass:false,deserialize:false,gc:false,help:false,importClass:false,importPackage:false,java:false,load:false,loadClass:false,Packages:false,print:false,quit:false,readFile:false,readUrl:false,runCommand:false,seal:false,serialize:false,spawn:false,sync:false,toint32:false,version:false};var nashorn={__DIR__:false,__FILE__:false,__LINE__:false,com:false,edu:false,exit:false,java:false,Java:false,javafx:false,JavaImporter:false,javax:false,JSAdapter:false,load:false,loadWithNewGlobal:false,org:false,Packages:false,print:false,quit:false};var wsh={ActiveXObject:true,Enumerator:true,GetObject:true,ScriptEngine:true,ScriptEngineBuildVersion:true,ScriptEngineMajorVersion:true,ScriptEngineMinorVersion:true,VBArray:true,WScript:true,WSH:true,XDomainRequest:true};var jquery={$:false,jQuery:false};var yui={YAHOO:false,YAHOO_config:false,YUI:false,YUI_config:false};var shelljs={cat:false,cd:false,chmod:false,config:false,cp:false,dirs:false,echo:false,env:false,error:false,exec:false,exit:false,find:false,grep:false,ln:false,ls:false,mkdir:false,mv:false,popd:false,pushd:false,pwd:false,rm:false,sed:false,set:false,target:false,tempdir:false,test:false,touch:false,which:false};var prototypejs={$:false,$$:false,$A:false,$break:false,$continue:false,$F:false,$H:false,$R:false,$w:false,Abstract:false,Ajax:false,Autocompleter:false,Builder:false,Class:false,Control:false,Draggable:false,Draggables:false,Droppables:false,Effect:false,Element:false,Enumerable:false,Event:false,Field:false,Form:false,Hash:false,Insertion:false,ObjectRange:false,PeriodicalExecuter:false,Position:false,Prototype:false,Scriptaculous:false,Selector:false,Sortable:false,SortableObserver:false,Sound:false,Template:false,Toggle:false,Try:false};var meteor={_:false,$:false,Accounts:false,AccountsClient:false,AccountsCommon:false,AccountsServer:false,App:false,Assets:false,Blaze:false,check:false,Cordova:false,DDP:false,DDPRateLimiter:false,DDPServer:false,Deps:false,EJSON:false,Email:false,HTTP:false,Log:false,Match:false,Meteor:false,Mongo:false,MongoInternals:false,Npm:false,Package:false,Plugin:false,process:false,Random:false,ReactiveDict:false,ReactiveVar:false,Router:false,ServiceConfiguration:false,Session:false,share:false,Spacebars:false,Template:false,Tinytest:false,Tracker:false,UI:false,Utils:false,WebApp:false,WebAppInternals:false};var mongo={_isWindows:false,_rand:false,BulkWriteResult:false,cat:false,cd:false,connect:false,db:false,getHostName:false,getMemInfo:false,hostname:false,ISODate:false,listFiles:false,load:false,ls:false,md5sumFile:false,mkdir:false,Mongo:false,NumberInt:false,NumberLong:false,ObjectId:false,PlanCache:false,print:false,printjson:false,pwd:false,quit:false,removeFile:false,rs:false,sh:false,UUID:false,version:false,WriteResult:false};var applescript={$:false,Application:false,Automation:false,console:false,delay:false,Library:false,ObjC:false,ObjectSpecifier:false,Path:false,Progress:false,Ref:false};var serviceworker={addEventListener:false,applicationCache:false,atob:false,Blob:false,BroadcastChannel:false,btoa:false,Cache:false,caches:false,CacheStorage:false,clearInterval:false,clearTimeout:false,Client:false,clients:false,Clients:false,close:true,console:false,ExtendableEvent:false,ExtendableMessageEvent:false,fetch:false,FetchEvent:false,FileReaderSync:false,FormData:false,Headers:false,IDBCursor:false,IDBCursorWithValue:false,IDBDatabase:false,IDBFactory:false,IDBIndex:false,IDBKeyRange:false,IDBObjectStore:false,IDBOpenDBRequest:false,IDBRequest:false,IDBTransaction:false,IDBVersionChangeEvent:false,ImageData:false,importScripts:false,indexedDB:false,location:false,MessageChannel:false,MessagePort:false,name:false,navigator:false,Notification:false,onclose:true,onconnect:true,onerror:true,onfetch:true,oninstall:true,onlanguagechange:true,onmessage:true,onmessageerror:true,onnotificationclick:true,onnotificationclose:true,onoffline:true,ononline:true,onpush:true,onpushsubscriptionchange:true,onrejectionhandled:true,onsync:true,onunhandledrejection:true,performance:false,Performance:false,PerformanceEntry:false,PerformanceMark:false,PerformanceMeasure:false,PerformanceNavigation:false,PerformanceResourceTiming:false,PerformanceTiming:false,postMessage:true,"Promise":false,queueMicrotask:false,registration:false,removeEventListener:false,Request:false,Response:false,self:false,ServiceWorker:false,ServiceWorkerContainer:false,ServiceWorkerGlobalScope:false,ServiceWorkerMessageEvent:false,ServiceWorkerRegistration:false,setInterval:false,setTimeout:false,skipWaiting:false,TextDecoder:false,TextEncoder:false,URL:false,URLSearchParams:false,WebSocket:false,WindowClient:false,Worker:false,WorkerGlobalScope:false,XMLHttpRequest:false};var atomtest={advanceClock:false,fakeClearInterval:false,fakeClearTimeout:false,fakeSetInterval:false,fakeSetTimeout:false,resetTimeouts:false,waitsForPromise:false};var embertest={andThen:false,click:false,currentPath:false,currentRouteName:false,currentURL:false,fillIn:false,find:false,findAll:false,findWithAssert:false,keyEvent:false,pauseTest:false,resumeTest:false,triggerEvent:false,visit:false,wait:false};var protractor={$:false,$$:false,browser:false,by:false,By:false,DartObject:false,element:false,protractor:false};var webextensions={browser:false,chrome:false,opr:false};var greasemonkey={cloneInto:false,createObjectIn:false,exportFunction:false,GM:false,GM_addStyle:false,GM_deleteValue:false,GM_getResourceText:false,GM_getResourceURL:false,GM_getValue:false,GM_info:false,GM_listValues:false,GM_log:false,GM_openInTab:false,GM_registerMenuCommand:false,GM_setClipboard:false,GM_setValue:false,GM_xmlhttpRequest:false,unsafeWindow:false};var devtools={$:false,$_:false,$$:false,$0:false,$1:false,$2:false,$3:false,$4:false,$x:false,chrome:false,clear:false,copy:false,debug:false,dir:false,dirxml:false,getEventListeners:false,inspect:false,keys:false,monitor:false,monitorEvents:false,profile:false,profileEnd:false,queryObjects:false,table:false,undebug:false,unmonitor:false,unmonitorEvents:false,values:false};var globals={builtin:builtin,es5:es5,es2015:es2015,es2017:es2017,browser:browser,worker:worker,node:node,commonjs:commonjs,amd:amd,mocha:mocha,jasmine:jasmine,jest:jest,qunit:qunit,phantomjs:phantomjs,couch:couch,rhino:rhino,nashorn:nashorn,wsh:wsh,jquery:jquery,yui:yui,shelljs:shelljs,prototypejs:prototypejs,meteor:meteor,mongo:mongo,applescript:applescript,serviceworker:serviceworker,atomtest:atomtest,embertest:embertest,protractor:protractor,"shared-node-browser":{clearInterval:false,clearTimeout:false,console:false,setInterval:false,setTimeout:false,URL:false,URLSearchParams:false},webextensions:webextensions,greasemonkey:greasemonkey,devtools:devtools};var globals$1=/*#__PURE__*/Object.freeze({builtin:builtin,es5:es5,es2015:es2015,es2017:es2017,browser:browser,worker:worker,node:node,commonjs:commonjs,amd:amd,mocha:mocha,jasmine:jasmine,jest:jest,qunit:qunit,phantomjs:phantomjs,couch:couch,rhino:rhino,nashorn:nashorn,wsh:wsh,jquery:jquery,yui:yui,shelljs:shelljs,prototypejs:prototypejs,meteor:meteor,mongo:mongo,applescript:applescript,serviceworker:serviceworker,atomtest:atomtest,embertest:embertest,protractor:protractor,webextensions:webextensions,greasemonkey:greasemonkey,devtools:devtools,'default':globals});var require$$0=getCjsExportFromNamespace(globals$1);var globals$2=require$$0;const browserAPIs=Object.keys(globals$2.browser);const builtinAPIs=Object.keys(globals$2.builtin);const isIdentifier=namedTypes.Identifier.check.bind(namedTypes.Identifier);const isLiteral=namedTypes.Literal.check.bind(namedTypes.Literal);const isExpressionStatement=namedTypes.ExpressionStatement.check.bind(namedTypes.ExpressionStatement);const isObjectExpression=namedTypes.ObjectExpression.check.bind(namedTypes.ObjectExpression);const isThisExpression=namedTypes.ThisExpression.check.bind(namedTypes.ThisExpression);const isSequenceExpression=namedTypes.SequenceExpression.check.bind(namedTypes.SequenceExpression);const isBinaryExpression=namedTypes.BinaryExpression.check.bind(namedTypes.BinaryExpression);const isExportDefaultStatement=namedTypes.ExportDefaultDeclaration.check.bind(namedTypes.ExportDefaultDeclaration);const isBrowserAPI=(_ref)=>{let{name}=_ref;return browserAPIs.includes(name);};const isBuiltinAPI=(_ref2)=>{let{name}=_ref2;return builtinAPIs.includes(name);};const isRaw=node=>node&&node.raw;/**
  	 * Find the export default statement
  	 * @param   { Array } body - tree structure containing the program code
  	 * @returns { Object } node containing only the code of the export default statement
  	 */function findExportDefaultStatement(body){return body.find(isExportDefaultStatement);}/**
  	 * Find all the code in an ast program except for the export default statements
  	 * @param   { Array } body - tree structure containing the program code
  	 * @returns { Array } array containing all the program code except the export default expressions
  	 */function filterNonExportDefaultStatements(body){return body.filter(node=>!isExportDefaultStatement(node));}/**
  	 * Get the body of the AST structure
  	 * @param   { Object } ast - ast object generated by recast
  	 * @returns { Array } array containing the program code
  	 */function getProgramBody(ast){return ast.body||ast.program.body;}/**
  	 * Extend the AST adding the new tag method containing our tag sourcecode
  	 * @param   { Object } ast - current output ast
  	 * @param   { Object } exportDefaultNode - tag export default node
  	 * @returns { Object } the output ast having the "tag" key extended with the content of the export default
  	 */function extendTagProperty(ast,exportDefaultNode){types$1.visit(ast,{visitProperty(path){if(path.value.key.value===TAG_LOGIC_PROPERTY){path.value.value=exportDefaultNode.declaration;return false;}this.traverse(path);}});return ast;}/**
  	 * Generate the component javascript logic
  	 * @param   { Object } sourceNode - node generated by the riot compiler
  	 * @param   { string } source - original component source code
  	 * @param   { Object } meta - compilation meta information
  	 * @param   { AST } ast - current AST output
  	 * @returns { AST } the AST generated
  	 */function javascript(sourceNode,source,meta,ast){const preprocessorName=getPreprocessorTypeByAttribute(sourceNode);const javascriptNode=addLineOffset(sourceNode.text.text,source,sourceNode);const{options}=meta;const preprocessorOutput=preprocess('javascript',preprocessorName,meta,Object.assign({},sourceNode,{text:javascriptNode}));const inputSourceMap=sourcemapAsJSON(preprocessorOutput.map);const generatedAst=generateAST(preprocessorOutput.code,{sourceFileName:options.file,inputSourceMap:isEmptySourcemap(inputSourceMap)?null:inputSourceMap});const generatedAstBody=getProgramBody(generatedAst);const bodyWithoutExportDefault=filterNonExportDefaultStatements(generatedAstBody);const exportDefaultNode=findExportDefaultStatement(generatedAstBody);const outputBody=getProgramBody(ast);// add to the ast the "private" javascript content of our tag script node
  outputBody.unshift(...bodyWithoutExportDefault);// convert the export default adding its content to the "tag" property exported
  if(exportDefaultNode)extendTagProperty(ast,exportDefaultNode);return ast;}/**
  	 * Not all the types are handled in this module.
  	 *
  	 * @enum {number}
  	 * @readonly
  	 */const TAG=1;/* TAG */const ATTR=2;/* ATTR */const TEXT=3;/* TEXT */const CDATA=4;/* CDATA */const COMMENT=8;/* COMMENT */const DOCUMENT=9;/* DOCUMENT */const DOCTYPE=10;/* DOCTYPE */const DOCUMENT_FRAGMENT=11;/* DOCUMENT_FRAGMENT */var types$3=/*#__PURE__*/Object.freeze({TAG:TAG,ATTR:ATTR,TEXT:TEXT,CDATA:CDATA,COMMENT:COMMENT,DOCUMENT:DOCUMENT,DOCTYPE:DOCTYPE,DOCUMENT_FRAGMENT:DOCUMENT_FRAGMENT});const rootTagNotFound='Root tag not found.';const unclosedTemplateLiteral='Unclosed ES6 template literal.';const unexpectedEndOfFile='Unexpected end of file.';const unclosedComment='Unclosed comment.';const unclosedNamedBlock='Unclosed "%1" block.';const duplicatedNamedTag='Duplicate tag "<%1>".';const unexpectedCharInExpression='Unexpected character %1.';const unclosedExpression='Unclosed expression.';/**
  	 * Matches the start of valid tags names; used with the first 2 chars after the `'<'`.
  	 * @const
  	 * @private
  	 */const TAG_2C=/^(?:\/[a-zA-Z]|[a-zA-Z][^\s>/]?)/;/**
  	 * Matches valid tags names AFTER the validation with `TAG_2C`.
  	 * $1: tag name including any `'/'`, $2: non self-closing brace (`>`) w/o attributes.
  	 * @const
  	 * @private
  	 */const TAG_NAME=/(\/?[^\s>/]+)\s*(>)?/g;/**
  	 * Matches an attribute name-value pair (both can be empty).
  	 * $1: attribute name, $2: value including any quotes.
  	 * @const
  	 * @private
  	 */const ATTR_START=/(\S[^>/=\s]*)(?:\s*=\s*([^>/])?)?/g;/**
  	 * Matches the spread operator
  	 * it will be used for the spread attributes
  	 * @type {RegExp}
  	 */const SPREAD_OPERATOR=/\.\.\./;/**
  	 * Matches the closing tag of a `script` and `style` block.
  	 * Used by parseText fo find the end of the block.
  	 * @const
  	 * @private
  	 */const RE_SCRYLE={script:/<\/script\s*>/gi,style:/<\/style\s*>/gi,textarea:/<\/textarea\s*>/gi};// Do not touch text content inside this tags
  const RAW_TAGS=/^\/?(?:pre|textarea)$/;const JAVASCRIPT_OUTPUT_NAME='javascript';const CSS_OUTPUT_NAME='css';const TEMPLATE_OUTPUT_NAME='template';// Tag names
  const JAVASCRIPT_TAG='script';const STYLE_TAG='style';const TEXTAREA_TAG='textarea';// Boolean attributes
  const IS_RAW='isRaw';const IS_SELF_CLOSING='isSelfClosing';const IS_VOID='isVoid';const IS_BOOLEAN='isBoolean';const IS_CUSTOM='isCustom';const IS_SPREAD='isSpread';/**
  	 * Add an item into a collection, if the collection is not an array
  	 * we create one and add the item to it
  	 * @param   {Array} collection - target collection
  	 * @param   {*} item - item to add to the collection
  	 * @returns {Array} array containing the new item added to it
  	 */function addToCollection(collection,item){if(collection===void 0){collection=[];}collection.push(item);return collection;}/**
  	 * Run RegExp.exec starting from a specific position
  	 * @param   {RegExp} re - regex
  	 * @param   {number} pos - last index position
  	 * @param   {string} string - regex target
  	 * @returns {Array} regex result
  	 */function execFromPos(re,pos,string){re.lastIndex=pos;return re.exec(string);}/**
  	 * Escape special characters in a given string, in preparation to create a regex.
  	 *
  	 * @param   {string} str - Raw string
  	 * @returns {string} Escaped string.
  	 */var escapeStr=str=>str.replace(/(?=[-[\](){^*+?.$|\\])/g,'\\');function formatError$1(data,message,pos){if(!pos){pos=data.length;}// count unix/mac/win eols
  const line=(data.slice(0,pos).match(/\r\n?|\n/g)||'').length+1;let col=0;while(--pos>=0&&!/[\r\n]/.test(data[pos])){++col;}return `[${line},${col}]: ${message}`;}const $_ES6_BQ='`';/**
  	 * Searches the next backquote that signals the end of the ES6 Template Literal
  	 * or the "${" sequence that starts a JS expression, skipping any escaped
  	 * character.
  	 *
  	 * @param   {string}    code  - Whole code
  	 * @param   {number}    pos   - The start position of the template
  	 * @param   {string[]}  stack - To save nested ES6 TL count
  	 * @returns {number}    The end of the string (-1 if not found)
  	 */function skipES6TL(code,pos,stack){// we are in the char following the backquote (`),
  // find the next unescaped backquote or the sequence "${"
  const re=/[`$\\]/g;let c;while(re.lastIndex=pos,re.exec(code)){pos=re.lastIndex;c=code[pos-1];if(c==='`'){return pos;}if(c==='$'&&code[pos++]==='{'){stack.push($_ES6_BQ,'}');return pos;}// else this is an escaped char
  }throw formatError$1(code,unclosedTemplateLiteral,pos);}/**
  	 * Custom error handler can be implemented replacing this method.
  	 * The `state` object includes the buffer (`data`)
  	 * The error position (`loc`) contains line (base 1) and col (base 0).
  	 * @param {string} data - string containing the error
  	 * @param {string} msg - Error message
  	 * @param {number} pos - Position of the error
  	 * @returns {undefined} throw an exception error
  	 */function panic$1(data,msg,pos){const message=formatError$1(data,msg,pos);throw new Error(message);}// forked from https://github.com/aMarCruz/skip-regex
  // safe characters to precced a regex (including `=>`, `**`, and `...`)
  const beforeReChars='[{(,;:?=|&!^~>%*/';const beforeReSign=`${beforeReChars}+-`;// keyword that can preceed a regex (`in` is handled as special case)
  const beforeReWords=['case','default','do','else','in','instanceof','prefix','return','typeof','void','yield'];// Last chars of all the beforeReWords elements to speed up the process.
  const wordsEndChar=beforeReWords.reduce((s,w)=>s+w.slice(-1),'');// Matches literal regex from the start of the buffer.
  // The buffer to search must not include line-endings.
  const RE_LIT_REGEX=/^\/(?=[^*>/])[^[/\\]*(?:(?:\\.|\[(?:\\.|[^\]\\]*)*\])[^[\\/]*)*?\/[gimuy]*/;// Valid characters for JavaScript variable names and literal numbers.
  const RE_JS_VCHAR=/[$\w]/;// Match dot characters that could be part of tricky regex
  const RE_DOT_CHAR=/.*/g;/**
  	 * Searches the position of the previous non-blank character inside `code`,
  	 * starting with `pos - 1`.
  	 *
  	 * @param   {string} code - Buffer to search
  	 * @param   {number} pos  - Starting position
  	 * @returns {number} Position of the first non-blank character to the left.
  	 * @private
  	 */function _prev(code,pos){while(--pos>=0&&/\s/.test(code[pos]));return pos;}/**
  	 * Check if the character in the `start` position within `code` can be a regex
  	 * and returns the position following this regex or `start+1` if this is not
  	 * one.
  	 *
  	 * NOTE: Ensure `start` points to a slash (this is not checked).
  	 *
  	 * @function skipRegex
  	 * @param   {string} code  - Buffer to test in
  	 * @param   {number} start - Position the first slash inside `code`
  	 * @returns {number} Position of the char following the regex.
  	 *
  	 */ /* istanbul ignore next */function skipRegex(code,start){let pos=RE_DOT_CHAR.lastIndex=start++;// `exec()` will extract from the slash to the end of the line
  //   and the chained `match()` will match the possible regex.
  const match=(RE_DOT_CHAR.exec(code)||' ')[0].match(RE_LIT_REGEX);if(match){const next=pos+match[0].length;// result comes from `re.match`
  pos=_prev(code,pos);let c=code[pos];// start of buffer or safe prefix?
  if(pos<0||beforeReChars.includes(c)){return next;}// from here, `pos` is >= 0 and `c` is code[pos]
  if(c==='.'){// can be `...` or something silly like 5./2
  if(code[pos-1]==='.'){start=next;}}else{if(c==='+'||c==='-'){// tricky case
  if(code[--pos]!==c||// if have a single operator or
  (pos=_prev(code,pos))<0||// ...have `++` and no previous token
  beforeReSign.includes(c=code[pos])){return next;// ...this is a regex
  }}if(wordsEndChar.includes(c)){// looks like a keyword?
  const end=pos+1;// get the complete (previous) keyword
  while(--pos>=0&&RE_JS_VCHAR.test(code[pos]));// it is in the allowed keywords list?
  if(beforeReWords.includes(code.slice(pos+1,end))){start=next;}}}}return start;}/*
  	 * Mini-parser for expressions.
  	 * The main pourpose of this module is to find the end of an expression
  	 * and return its text without the enclosing brackets.
  	 * Does not works with comments, but supports ES6 template strings.
  	 */ /**
  	 * @exports exprExtr
  	 */const S_SQ_STR=/'[^'\n\r\\]*(?:\\(?:\r\n?|[\S\s])[^'\n\r\\]*)*'/.source;/**
  	 * Matches double quoted JS strings taking care about nested quotes
  	 * and EOLs (escaped EOLs are Ok).
  	 *
  	 * @const
  	 * @private
  	 */const S_STRING=`${S_SQ_STR}|${S_SQ_STR.replace(/'/g,'"')}`;/**
  	 * Regex cache
  	 *
  	 * @type {Object.<string, RegExp>}
  	 * @const
  	 * @private
  	 */const reBr={};/**
  	 * Makes an optimal regex that matches quoted strings, brackets, backquotes
  	 * and the closing brackets of an expression.
  	 *
  	 * @param   {string} b - Closing brackets
  	 * @returns {RegExp} - optimized regex
  	 */function _regex(b){let re=reBr[b];if(!re){let s=escapeStr(b);if(b.length>1){s=`${s}|[`;}else{s=/[{}[\]()]/.test(b)?'[':`[${s}`;}reBr[b]=re=new RegExp(`${S_STRING}|${s}\`/\\{}[\\]()]`,'g');}return re;}/**
  	 * Update the scopes stack removing or adding closures to it
  	 * @param   {Array} stack - array stacking the expression closures
  	 * @param   {string} char - current char to add or remove from the stack
  	 * @param   {string} idx  - matching index
  	 * @param   {string} code - expression code
  	 * @returns {Object} result
  	 * @returns {Object} result.char - either the char received or the closing braces
  	 * @returns {Object} result.index - either a new index to skip part of the source code,
  	 *                                  or 0 to keep from parsing from the old position
  	 */function updateStack(stack,char,idx,code){let index=0;switch(char){case'[':case'(':case'{':stack.push(char==='['?']':char==='('?')':'}');break;case')':case']':case'}':if(char!==stack.pop()){panic$1(code,unexpectedCharInExpression.replace('%1',char),index);}if(char==='}'&&stack[stack.length-1]===$_ES6_BQ){char=stack.pop();}index=idx+1;break;case'/':index=skipRegex(code,idx);}return {char,index};}/**
  	   * Parses the code string searching the end of the expression.
  	   * It skips braces, quoted strings, regexes, and ES6 template literals.
  	   *
  	   * @function exprExtr
  	   * @param   {string}  code  - Buffer to parse
  	   * @param   {number}  start - Position of the opening brace
  	   * @param   {[string,string]} bp - Brackets pair
  	   * @returns {Object} Expression's end (after the closing brace) or -1
  	   *                            if it is not an expr.
  	   */function exprExtr(code,start,bp){const[openingBraces,closingBraces]=bp;const offset=start+openingBraces.length;// skips the opening brace
  const stack=[];// expected closing braces ('`' for ES6 TL)
  const re=_regex(closingBraces);re.lastIndex=offset;// begining of the expression
  let end;let match;while(match=re.exec(code)){// eslint-disable-line
  const idx=match.index;const str=match[0];end=re.lastIndex;// end the iteration
  if(str===closingBraces&&!stack.length){return {text:code.slice(offset,idx),start,end};}const{char,index}=updateStack(stack,str[0],idx,code);// update the end value depending on the new index received
  end=index||end;// update the regex last index
  re.lastIndex=char===$_ES6_BQ?skipES6TL(code,end,stack):end;}if(stack.length){panic$1(code,unclosedExpression,end);}}/**
  	 * Outputs the last parsed node. Can be used with a builder too.
  	 *
  	 * @param   {ParserStore} store - Parsing store
  	 * @returns {undefined} void function
  	 * @private
  	 */function flush(store){const last=store.last;store.last=null;if(last&&store.root){store.builder.push(last);}}/**
  	 * Get the code chunks from start and end range
  	 * @param   {string}  source  - source code
  	 * @param   {number}  start   - Start position of the chunk we want to extract
  	 * @param   {number}  end     - Ending position of the chunk we need
  	 * @returns {string}  chunk of code extracted from the source code received
  	 * @private
  	 */function getChunk(source,start,end){return source.slice(start,end);}/**
  	 * states text in the last text node, or creates a new one if needed.
  	 *
  	 * @param {ParserState}   state   - Current parser state
  	 * @param {number}  start   - Start position of the tag
  	 * @param {number}  end     - Ending position (last char of the tag)
  	 * @param {Object}  extra   - extra properties to add to the text node
  	 * @param {RawExpr[]} extra.expressions  - Found expressions
  	 * @param {string}    extra.unescape     - Brackets to unescape
  	 * @returns {undefined} - void function
  	 * @private
  	 */function pushText(state,start,end,extra){if(extra===void 0){extra={};}const text=getChunk(state.data,start,end);const expressions=extra.expressions;const unescape=extra.unescape;let q=state.last;state.pos=end;if(q&&q.type===TEXT){q.text+=text;q.end=end;}else{flush(state);state.last=q={type:TEXT,text,start,end};}if(expressions&&expressions.length){q.expressions=(q.expressions||[]).concat(expressions);}if(unescape){q.unescape=unescape;}return TEXT;}/**
  	 * Find the end of the attribute value or text node
  	 * Extract expressions.
  	 * Detect if value have escaped brackets.
  	 *
  	 * @param   {ParserState} state  - Parser state
  	 * @param   {HasExpr} node       - Node if attr, info if text
  	 * @param   {string} endingChars - Ends the value or text
  	 * @param   {number} start       - Starting position
  	 * @returns {number} Ending position
  	 * @private
  	 */function expr(state,node,endingChars,start){const re=b0re(state,endingChars);re.lastIndex=start;// reset re position
  const{unescape,expressions,end}=parseExpressions(state,re);if(node){if(unescape){node.unescape=unescape;}if(expressions.length){node.expressions=expressions;}}else{pushText(state,start,end,{expressions,unescape});}return end;}/**
  	 * Parse a text chunk finding all the expressions in it
  	 * @param   {ParserState} state  - Parser state
  	 * @param   {RegExp} re - regex to match the expressions contents
  	 * @returns {Object} result containing the expression found, the string to unescape and the end position
  	 */function parseExpressions(state,re){const{data,options}=state;const{brackets}=options;const expressions=[];let unescape,pos,match;// Anything captured in $1 (closing quote or character) ends the loop...
  while((match=re.exec(data))&&!match[1]){// ...else, we have an opening bracket and maybe an expression.
  pos=match.index;if(data[pos-1]==='\\'){unescape=match[0];// it is an escaped opening brace
  }else{const tmpExpr=exprExtr(data,pos,brackets);if(tmpExpr){expressions.push(tmpExpr);re.lastIndex=tmpExpr.end;}}}// Even for text, the parser needs match a closing char
  if(!match){panic$1(data,unexpectedEndOfFile,pos);}return {unescape,expressions,end:match.index};}/**
  	 * Creates a regex for the given string and the left bracket.
  	 * The string is captured in $1.
  	 *
  	 * @param   {ParserState} state  - Parser state
  	 * @param   {string} str - String to search
  	 * @returns {RegExp} Resulting regex.
  	 * @private
  	 */function b0re(state,str){const{brackets}=state.options;const re=state.regexCache[str];if(re)return re;const b0=escapeStr(brackets[0]);// cache the regex extending the regexCache object
  Object.assign(state.regexCache,{[str]:new RegExp(`(${str})|${b0}`,'g')});return state.regexCache[str];}/**
  	 * SVG void elements that cannot be auto-closed and shouldn't contain child nodes.
  	 * @const {Array}
  	 */const VOID_SVG_TAGS_LIST=['circle','ellipse','line','path','polygon','polyline','rect','stop','use'];/**
  	 * List of all the available svg tags
  	 * @const {Array}
  	 * @see {@link https://github.com/wooorm/svg-tag-names}
  	 */const SVG_TAGS_LIST=['a','altGlyph','altGlyphDef','altGlyphItem','animate','animateColor','animateMotion','animateTransform','animation','audio','canvas','clipPath','color-profile','cursor','defs','desc','discard','feBlend','feColorMatrix','feComponentTransfer','feComposite','feConvolveMatrix','feDiffuseLighting','feDisplacementMap','feDistantLight','feDropShadow','feFlood','feFuncA','feFuncB','feFuncG','feFuncR','feGaussianBlur','feImage','feMerge','feMergeNode','feMorphology','feOffset','fePointLight','feSpecularLighting','feSpotLight','feTile','feTurbulence','filter','font','font-face','font-face-format','font-face-name','font-face-src','font-face-uri','foreignObject','g','glyph','glyphRef','handler','hatch','hatchpath','hkern','iframe','image','linearGradient','listener','marker','mask','mesh','meshgradient','meshpatch','meshrow','metadata','missing-glyph','mpath','pattern','prefetch','radialGradient','script','set','solidColor','solidcolor','style','svg','switch','symbol','tbreak','text','textArea','textPath','title','tref','tspan','unknown','video','view','vkern'].concat(VOID_SVG_TAGS_LIST).sort();/**
  	 * HTML void elements that cannot be auto-closed and shouldn't contain child nodes.
  	 * @type {Array}
  	 * @see   {@link http://www.w3.org/TR/html-markup/syntax.html#syntax-elements}
  	 * @see   {@link http://www.w3.org/TR/html5/syntax.html#void-elements}
  	 */const VOID_HTML_TAGS_LIST=['area','base','br','col','embed','hr','img','input','keygen','link','menuitem','meta','param','source','track','wbr'];/**
  	 * List of all the html tags
  	 * @const {Array}
  	 * @see {@link https://github.com/sindresorhus/html-tags}
  	 */const HTML_TAGS_LIST=['a','abbr','address','article','aside','audio','b','bdi','bdo','blockquote','body','button','canvas','caption','cite','code','colgroup','data','datalist','dd','del','details','dfn','dialog','div','dl','dt','em','fieldset','figcaption','figure','footer','form','h1','h2','h3','h4','h5','h6','head','header','hgroup','html','i','iframe','ins','kbd','label','legend','li','main','map','mark','math','menu','meter','nav','noscript','object','ol','optgroup','option','output','p','picture','pre','progress','q','rb','rp','rt','rtc','ruby','s','samp','script','section','select','slot','small','span','strong','style','sub','summary','sup','svg','table','tbody','td','template','textarea','tfoot','th','thead','time','title','tr','u','ul','var','video'].concat(VOID_HTML_TAGS_LIST).sort();/**
  	 * Matches boolean HTML attributes in the riot tag definition.
  	 * With a long list like this, a regex is faster than `[].indexOf` in most browsers.
  	 * @const {RegExp}
  	 * @see [attributes.md](https://github.com/riot/compiler/blob/dev/doc/attributes.md)
  	 */const BOOLEAN_ATTRIBUTES_LIST=['disabled','visible','checked','readonly','required','allowfullscreen','autofocus','autoplay','compact','controls','default','formnovalidate','hidden','ismap','itemscope','loop','multiple','muted','noresize','noshade','novalidate','nowrap','open','reversed','seamless','selected','sortable','truespeed','typemustmatch'];/**
  	 * Join a list of items with the pipe symbol (usefull for regex list concatenation)
  	 * @private
  	 * @param   {Array} list - list of strings
  	 * @returns {String} the list received joined with pipes
  	 */function joinWithPipe(list){return list.join('|');}/**
  	 * Convert list of strings to regex in order to test against it ignoring the cases
  	 * @private
  	 * @param   {...Array} lists - array of strings
  	 * @returns {RegExp} regex that will match all the strings in the array received ignoring the cases
  	 */function listsToRegex(){for(var _len4=arguments.length,lists=new Array(_len4),_key4=0;_key4<_len4;_key4++){lists[_key4]=arguments[_key4];}return new RegExp(`^/?(?:${joinWithPipe(lists.map(joinWithPipe))})$`,'i');}/**
  	 * Regex matching all the html tags ignoring the cases
  	 * @const {RegExp}
  	 */const HTML_TAGS_RE=listsToRegex(HTML_TAGS_LIST);/**
  	 * Regex matching all the svg tags ignoring the cases
  	 * @const {RegExp}
  	 */const SVG_TAGS_RE=listsToRegex(SVG_TAGS_LIST);/**
  	 * Regex matching all the void html tags ignoring the cases
  	 * @const {RegExp}
  	 */const VOID_HTML_TAGS_RE=listsToRegex(VOID_HTML_TAGS_LIST);/**
  	 * Regex matching all the void svg tags ignoring the cases
  	 * @const {RegExp}
  	 */const VOID_SVG_TAGS_RE=listsToRegex(VOID_SVG_TAGS_LIST);/**
  	 * Regex matching all the boolean attributes
  	 * @const {RegExp}
  	 */const BOOLEAN_ATTRIBUTES_RE=listsToRegex(BOOLEAN_ATTRIBUTES_LIST);/**
  	 * True if it's a self closing tag
  	 * @param   {String}  tag - test tag
  	 * @returns {Boolean}
  	 * @example
  	 * isVoid('meta') // true
  	 * isVoid('circle') // true
  	 * isVoid('IMG') // true
  	 * isVoid('div') // false
  	 * isVoid('mask') // false
  	 */function isVoid(tag){return [VOID_HTML_TAGS_RE,VOID_SVG_TAGS_RE].some(r=>r.test(tag));}/**
  	 * True if it's not SVG nor a HTML known tag
  	 * @param   {String}  tag - test tag
  	 * @returns {Boolean}
  	 * @example
  	 * isCustom('my-component') // true
  	 * isCustom('div') // false
  	 */function isCustom(tag){return [HTML_TAGS_RE,SVG_TAGS_RE].every(l=>!l.test(tag));}/**
  	 * True if it's a boolean attribute
  	 * @param   {String} attribute - test attribute
  	 * @returns {Boolean}
  	 * @example
  	 * isBoolAttribute('selected') // true
  	 * isBoolAttribute('class') // false
  	 */function isBoolAttribute(attribute){return BOOLEAN_ATTRIBUTES_RE.test(attribute);}/**
  	 * Memoization function
  	 * @param   {Function} fn - function to memoize
  	 * @returns {*} return of the function to memoize
  	 */function memoize(fn){const cache=new WeakMap();return function(){if(cache.has(arguments.length<=0?undefined:arguments[0]))return cache.get(arguments.length<=0?undefined:arguments[0]);const ret=fn(...arguments);cache.set(arguments.length<=0?undefined:arguments[0],ret);return ret;};}const expressionsContentRe=memoize(brackets=>RegExp(`(${brackets[0]}[^${brackets[1]}]*?${brackets[1]})`,'g'));const isSpreadAttribute=name=>SPREAD_OPERATOR.test(name);const isAttributeExpression=(name,brackets)=>name[0]===brackets[0];const getAttributeEnd=(state,attr)=>expr(state,attr,'[>/\\s]',attr.start);/**
  	 * The more complex parsing is for attributes as it can contain quoted or
  	 * unquoted values or expressions.
  	 *
  	 * @param   {ParserStore} state  - Parser state
  	 * @returns {number} New parser mode.
  	 * @private
  	 */function attr(state){const{data,last,pos,root}=state;const tag=last;// the last (current) tag in the output
  const _CH=/\S/g;// matches the first non-space char
  const ch=execFromPos(_CH,pos,data);switch(true){case!ch:state.pos=data.length;// reaching the end of the buffer with
  // NodeTypes.ATTR will generate error
  break;case ch[0]==='>':// closing char found. If this is a self-closing tag with the name of the
  // Root tag, we need decrement the counter as we are changing mode.
  state.pos=tag.end=_CH.lastIndex;if(tag[IS_SELF_CLOSING]){state.scryle=null;// allow selfClosing script/style tags
  if(root&&root.name===tag.name){state.count--;// "pop" root tag
  }}return TEXT;case ch[0]==='/':state.pos=_CH.lastIndex;// maybe. delegate the validation
  tag[IS_SELF_CLOSING]=true;// the next loop
  break;default:delete tag[IS_SELF_CLOSING];// ensure unmark as selfclosing tag
  setAttribute(state,ch.index,tag);}return ATTR;}/**
  	 * Parses an attribute and its expressions.
  	 *
  	 * @param   {ParserStore}  state  - Parser state
  	 * @param   {number} pos    - Starting position of the attribute
  	 * @param   {Object} tag    - Current parent tag
  	 * @returns {undefined} void function
  	 * @private
  	 */function setAttribute(state,pos,tag){const{data}=state;const expressionContent=expressionsContentRe(state.options.brackets);const re=ATTR_START;// (\S[^>/=\s]*)(?:\s*=\s*([^>/])?)? g
  const start=re.lastIndex=expressionContent.lastIndex=pos;// first non-whitespace
  const attrMatches=re.exec(data);const isExpressionName=isAttributeExpression(attrMatches[1],state.options.brackets);const match=isExpressionName?[null,expressionContent.exec(data)[1],null]:attrMatches;if(match){const end=re.lastIndex;const attr=parseAttribute(state,match,start,end,isExpressionName);//assert(q && q.type === Mode.TAG, 'no previous tag for the attr!')
  // Pushes the attribute and shifts the `end` position of the tag (`last`).
  state.pos=tag.end=attr.end;tag.attributes=addToCollection(tag.attributes,attr);}}function parseNomalAttribute(state,attr,quote){const{data}=state;let{end}=attr;if(isBoolAttribute(attr.name)){attr[IS_BOOLEAN]=true;}// parse the whole value (if any) and get any expressions on it
  if(quote){// Usually, the value's first char (`quote`) is a quote and the lastIndex
  // (`end`) is the start of the value.
  let valueStart=end;// If it not, this is an unquoted value and we need adjust the start.
  if(quote!=='"'&&quote!=='\''){quote='';// first char of value is not a quote
  valueStart--;// adjust the starting position
  }end=expr(state,attr,quote||'[>/\\s]',valueStart);// adjust the bounds of the value and save its content
  return Object.assign(attr,{value:getChunk(data,valueStart,end),valueStart,end:quote?++end:end});}return attr;}/**
  	 * Parse expression names <a {href}>
  	 * @param   {ParserStore}  state  - Parser state
  	 * @param   {Object} attr - attribute object parsed
  	 * @returns {Object} normalized attribute object
  	 */function parseSpreadAttribute(state,attr){const end=getAttributeEnd(state,attr);return {[IS_SPREAD]:true,start:attr.start,expressions:attr.expressions.map(expr=>Object.assign(expr,{text:expr.text.replace(SPREAD_OPERATOR,'').trim()})),end:end};}/**
  	 * Parse expression names <a {href}>
  	 * @param   {ParserStore}  state  - Parser state
  	 * @param   {Object} attr - attribute object parsed
  	 * @returns {Object} normalized attribute object
  	 */function parseExpressionNameAttribute(state,attr){const end=getAttributeEnd(state,attr);return {start:attr.start,name:attr.expressions[0].text.trim(),expressions:attr.expressions,end:end};}/**
  	 * Parse the attribute values normalising the quotes
  	 * @param   {ParserStore}  state  - Parser state
  	 * @param   {Array} match - results of the attributes regex
  	 * @param   {number} start - attribute start position
  	 * @param   {number} end - attribute end position
  	 * @param   {boolean} isExpressionName - true if the attribute name is an expression
  	 * @returns {Object} attribute object
  	 */function parseAttribute(state,match,start,end,isExpressionName){const attr={name:match[1],value:'',start,end};const quote=match[2];// first letter of value or nothing
  switch(true){case isSpreadAttribute(attr.name):return parseSpreadAttribute(state,attr);case isExpressionName===true:return parseExpressionNameAttribute(state,attr);default:return parseNomalAttribute(state,attr,quote);}}/**
  	 * Parses comments in long or short form
  	 * (any DOCTYPE & CDATA blocks are parsed as comments).
  	 *
  	 * @param   {ParserState} state  - Parser state
  	 * @param   {string} data       - Buffer to parse
  	 * @param   {number} start      - Position of the '<!' sequence
  	 * @returns {number} node type id
  	 * @private
  	 */function comment(state,data,start){const pos=start+2;// skip '<!'
  const str=data.substr(pos,2)==='--'?'-->':'>';const end=data.indexOf(str,pos);if(end<0){panic$1(data,unclosedComment,start);}pushComment$1(state,start,end+str.length);return TEXT;}/**
  	 * Parse a comment.
  	 *
  	 * @param   {ParserState}  state - Current parser state
  	 * @param   {number}  start - Start position of the tag
  	 * @param   {number}  end   - Ending position (last char of the tag)
  	 * @returns {undefined} void function
  	 * @private
  	 */function pushComment$1(state,start,end){flush(state);state.pos=end;if(state.options.comments===true){state.last={type:COMMENT,start,end};}}/**
  	 * Pushes a new *tag* and set `last` to this, so any attributes
  	 * will be included on this and shifts the `end`.
  	 *
  	 * @param   {ParserState} state  - Current parser state
  	 * @param   {string}  name      - Name of the node including any slash
  	 * @param   {number}  start     - Start position of the tag
  	 * @param   {number}  end       - Ending position (last char of the tag + 1)
  	 * @returns {undefined} - void function
  	 * @private
  	 */function pushTag(state,name,start,end){const root=state.root;const last={type:TAG,name,start,end};if(isCustom(name)){last[IS_CUSTOM]=true;}if(isVoid(name)){last[IS_VOID]=true;}state.pos=end;if(root){if(name===root.name){state.count++;}else if(name===root.close){state.count--;}flush(state);}else{// start with root (keep ref to output)
  state.root={name:last.name,close:`/${name}`};state.count=1;}state.last=last;}/**
  	 * Parse the tag following a '<' character, or delegate to other parser
  	 * if an invalid tag name is found.
  	 *
  	 * @param   {ParserState} state  - Parser state
  	 * @returns {number} New parser mode
  	 * @private
  	 */function tag(state){const{pos,data}=state;// pos of the char following '<'
  const start=pos-1;// pos of '<'
  const str=data.substr(pos,2);// first two chars following '<'
  switch(true){case str[0]==='!':return comment(state,data,start);case TAG_2C.test(str):return parseTag(state,start);default:return pushText(state,start,pos);// pushes the '<' as text
  }}function parseTag(state,start){const{data,pos}=state;const re=TAG_NAME;// (\/?[^\s>/]+)\s*(>)? g
  const match=execFromPos(re,pos,data);const end=re.lastIndex;const name=match[1].toLowerCase();// $1: tag name including any '/'
  // script/style block is parsed as another tag to extract attributes
  if(name in RE_SCRYLE){state.scryle=name;// used by parseText
  }pushTag(state,name,start,end);// only '>' can ends the tag here, the '/' is handled in parseAttribute
  if(!match[2]){return ATTR;}return TEXT;}/**
  	 * Parses regular text and script/style blocks ...scryle for short :-)
  	 * (the content of script and style is text as well)
  	 *
  	 * @param   {ParserState} state - Parser state
  	 * @returns {number} New parser mode.
  	 * @private
  	 */function text(state){const{pos,data,scryle}=state;switch(true){case typeof scryle==='string':{const name=scryle;const re=RE_SCRYLE[name];const match=execFromPos(re,pos,data);if(!match){panic$1(data,unclosedNamedBlock.replace('%1',name),pos-1);}const start=match.index;const end=re.lastIndex;state.scryle=null;// reset the script/style flag now
  // write the tag content, if any
  if(start>pos){parseSpecialTagsContent(state,name,match);}// now the closing tag, either </script> or </style>
  pushTag(state,`/${name}`,start,end);break;}case data[pos]==='<':state.pos++;return TAG;default:expr(state,null,'<',pos);}return TEXT;}/**
  	 * Parse the text content depending on the name
  	 * @param   {ParserState} state - Parser state
  	 * @param   {string} name  - one of the tags matched by the RE_SCRYLE regex
  	 * @param   {Array}  match - result of the regex matching the content of the parsed tag
  	 * @returns {undefined} void function
  	 */function parseSpecialTagsContent(state,name,match){const{pos}=state;const start=match.index;if(name===TEXTAREA_TAG){expr(state,null,match[0],pos);}else{pushText(state,pos,start);}}/*---------------------------------------------------------------------
  	 * Tree builder for the riot tag parser.
  	 *
  	 * The output has a root property and separate arrays for `html`, `css`,
  	 * and `js` tags.
  	 *
  	 * The root tag is included as first element in the `html` array.
  	 * Script tags marked with "defer" are included in `html` instead `js`.
  	 *
  	 * - Mark SVG tags
  	 * - Mark raw tags
  	 * - Mark void tags
  	 * - Split prefixes from expressions
  	 * - Unescape escaped brackets and escape EOLs and backslashes
  	 * - Compact whitespace (option `compact`) for non-raw tags
  	 * - Create an array `parts` for text nodes and attributes
  	 *
  	 * Throws on unclosed tags or closing tags without start tag.
  	 * Selfclosing and void tags has no nodes[] property.
  	 */ /**
  	 * Escape the carriage return and the line feed from a string
  	 * @param   {string} string - input string
  	 * @returns {string} output string escaped
  	 */function escapeReturn(string){return string.replace(/\r/g,'\\r').replace(/\n/g,'\\n');}/**
  	 * Escape double slashes in a string
  	 * @param   {string} string - input string
  	 * @returns {string} output string escaped
  	 */function escapeSlashes(string){return string.replace(/\\/g,'\\\\');}/**
  	 * Replace the multiple spaces with only one
  	 * @param   {string} string - input string
  	 * @returns {string} string without trailing spaces
  	 */function cleanSpaces(string){return string.replace(/\s+/g,' ');}const TREE_BUILDER_STRUCT=Object.seal({get(){const store=this.store;// The real root tag is in store.root.nodes[0]
  return {[TEMPLATE_OUTPUT_NAME]:store.root.nodes[0],[CSS_OUTPUT_NAME]:store[STYLE_TAG],[JAVASCRIPT_OUTPUT_NAME]:store[JAVASCRIPT_TAG]};},/**
  	  * Process the current tag or text.
  	  * @param {Object} node - Raw pseudo-node from the parser
  	  * @returns {undefined} void function
  	  */push(node){const store=this.store;switch(node.type){case TEXT:this.pushText(store,node);break;case TAG:{const name=node.name;const closingTagChar='/';const[firstChar]=name;if(firstChar===closingTagChar&&!node.isVoid){this.closeTag(store,node,name);}else if(firstChar!==closingTagChar){this.openTag(store,node);}break;}}},closeTag(store,node){const last=store.scryle||store.last;last.end=node.end;if(store.scryle){store.scryle=null;}else{store.last=store.stack.pop();}},openTag(store,node){const name=node.name;const attrs=node.attributes;if([JAVASCRIPT_TAG,STYLE_TAG].includes(name)){// Only accept one of each
  if(store[name]){panic$1(this.store.data,duplicatedNamedTag.replace('%1',name),node.start);}store[name]=node;store.scryle=store[name];}else{// store.last holds the last tag pushed in the stack and this are
  // non-void, non-empty tags, so we are sure the `lastTag` here
  // have a `nodes` property.
  const lastTag=store.last;const newNode=node;lastTag.nodes.push(newNode);if(lastTag[IS_RAW]||RAW_TAGS.test(name)){node[IS_RAW]=true;}if(!node[IS_SELF_CLOSING]&&!node[IS_VOID]){store.stack.push(lastTag);newNode.nodes=[];store.last=newNode;}}if(attrs){this.attrs(attrs);}},attrs(attributes){attributes.forEach(attr=>{if(attr.value){this.split(attr,attr.value,attr.valueStart,true);}});},pushText(store,node){const text=node.text;const empty=!/\S/.test(text);const scryle=store.scryle;if(!scryle){// store.last always have a nodes property
  const parent=store.last;const pack=this.compact&&!parent[IS_RAW];if(pack&&empty){return;}this.split(node,text,node.start,pack);parent.nodes.push(node);}else if(!empty){scryle.text=node;}},split(node,source,start,pack){const expressions=node.expressions;const parts=[];if(expressions){let pos=0;expressions.forEach(expr=>{const text=source.slice(pos,expr.start-start);const code=expr.text;parts.push(this.sanitise(node,text,pack),escapeReturn(escapeSlashes(code).trim()));pos=expr.end-start;});if(pos<node.end){parts.push(this.sanitise(node,source.slice(pos),pack));}}else{parts[0]=this.sanitise(node,source,pack);}node.parts=parts.filter(p=>p);// remove the empty strings
  },// unescape escaped brackets and split prefixes of expressions
  sanitise(node,text,pack){let rep=node.unescape;if(rep){let idx=0;rep=`\\${rep}`;while((idx=text.indexOf(rep,idx))!==-1){text=text.substr(0,idx)+text.substr(idx+1);idx++;}}text=escapeSlashes(text);return pack?cleanSpaces(text):escapeReturn(text);}});function createTreeBuilder(data,options){const root={type:TAG,name:'',start:0,end:0,nodes:[]};return Object.assign(Object.create(TREE_BUILDER_STRUCT),{compact:options.compact!==false,store:{last:root,stack:[],scryle:null,root,style:null,script:null,data}});}/**
  	 * Factory for the Parser class, exposing only the `parse` method.
  	 * The export adds the Parser class as property.
  	 *
  	 * @param   {Object}   options - User Options
  	 * @param   {Function} customBuilder - Tree builder factory
  	 * @returns {Function} Public Parser implementation.
  	 */function parser$1(options,customBuilder){const state=curry(createParserState)(options,customBuilder||createTreeBuilder);return {parse:data=>parse$1(state(data))};}/**
  	 * Create a new state object
  	 * @param   {Object} userOptions - parser options
  	 * @param   {Function} builder - Tree builder factory
  	 * @param   {string} data - data to parse
  	 * @returns {ParserState} it represents the current parser state
  	 */function createParserState(userOptions,builder,data){const options=Object.assign({brackets:['{','}']},userOptions);return {options,regexCache:{},pos:0,count:-1,root:null,last:null,scryle:null,builder:builder(data,options),data};}/**
  	 * It creates a raw output of pseudo-nodes with one of three different types,
  	 * all of them having a start/end position:
  	 *
  	 * - TAG     -- Opening or closing tags
  	 * - TEXT    -- Raw text
  	 * - COMMENT -- Comments
  	 *
  	 * @param   {ParserState}  state - Current parser state
  	 * @returns {ParserResult} Result, contains data and output properties.
  	 */function parse$1(state){const{data}=state;walk(state);flush(state);if(state.count){panic$1(data,state.count>0?unexpectedEndOfFile:rootTagNotFound,state.pos);}return {data,output:state.builder.get()};}/**
  	 * Parser walking recursive function
  	 * @param {ParserState}  state - Current parser state
  	 * @param {string} type - current parsing context
  	 * @returns {undefined} void function
  	 */function walk(state,type){const{data}=state;// extend the state adding the tree builder instance and the initial data
  const length=data.length;// The "count" property is set to 1 when the first tag is found.
  // This becomes the root and precedent text or comments are discarded.
  // So, at the end of the parsing count must be zero.
  if(state.pos<length&&state.count){walk(state,eat(state,type));}}/**
  	 * Function to help iterating on the current parser state
  	 * @param {ParserState}  state - Current parser state
  	 * @param   {string} type - current parsing context
  	 * @returns {string} parsing context
  	 */function eat(state,type){switch(type){case TAG:return tag(state);case ATTR:return attr(state);default:return text(state);}}/**
  	 * The nodeTypes definition
  	 */const nodeTypes=types$3;// import {IS_BOOLEAN,IS_CUSTOM,IS_RAW,IS_SPREAD,IS_VOID} from '@riotjs/parser/src/constants'
  const BINDING_TYPES='bindingTypes';const EACH_BINDING_TYPE='EACH';const IF_BINDING_TYPE='IF';const TAG_BINDING_TYPE='TAG';const SLOT_BINDING_TYPE='SLOT';const EXPRESSION_TYPES='expressionTypes';const ATTRIBUTE_EXPRESSION_TYPE='ATTRIBUTE';const VALUE_EXPRESSION_TYPE='VALUE';const TEXT_EXPRESSION_TYPE='TEXT';const EVENT_EXPRESSION_TYPE='EVENT';const TEMPLATE_FN='template';const SCOPE='scope';const GET_COMPONENT_FN='getComponent';// keys needed to create the DOM bindings
  const BINDING_SELECTOR_KEY='selector';const BINDING_GET_COMPONENT_KEY='getComponent';const BINDING_TEMPLATE_KEY='template';const BINDING_TYPE_KEY='type';const BINDING_REDUNDANT_ATTRIBUTE_KEY='redundantAttribute';const BINDING_CONDITION_KEY='condition';const BINDING_ITEM_NAME_KEY='itemName';const BINDING_GET_KEY_KEY='getKey';const BINDING_INDEX_NAME_KEY='indexName';const BINDING_EVALUATE_KEY='evaluate';const BINDING_NAME_KEY='name';const BINDING_SLOTS_KEY='slots';const BINDING_EXPRESSIONS_KEY='expressions';const BINDING_CHILD_NODE_INDEX_KEY='childNodeIndex';// slots keys
  const BINDING_BINDINGS_KEY='bindings';const BINDING_ID_KEY='id';const BINDING_HTML_KEY='html';const BINDING_ATTRIBUTES_KEY='attributes';// DOM directives
  const IF_DIRECTIVE='if';const EACH_DIRECTIVE='each';const KEY_ATTRIBUTE='key';const SLOT_ATTRIBUTE='slot';const NAME_ATTRIBUTE='name';const IS_DIRECTIVE='is';// Misc
  const DEFAULT_SLOT_NAME='default';const TEXT_NODE_EXPRESSION_PLACEHOLDER='<!---->';const BINDING_SELECTOR_PREFIX='expr';const SLOT_TAG_NODE_NAME='slot';const IS_VOID_NODE='isVoid';const IS_CUSTOM_NODE='isCustom';const IS_BOOLEAN_ATTRIBUTE='isBoolean';const IS_SPREAD_ATTRIBUTE='isSpread';/**
  	 * Unescape the user escaped chars
  	 * @param   {string} string - input string
  	 * @param   {string} char - probably a '{' or anything the user want's to escape
  	 * @returns {string} cleaned up string
  	 */function unescapeChar(string,char){return string.replace(RegExp(`\\\\${char}`,'gm'),char);}const scope$1=builders.identifier(SCOPE);const getName$1=node=>node&&node.name?node.name:node;/**
  	 * Find the attribute node
  	 * @param   { string } name -  name of the attribute we want to find
  	 * @param   { riotParser.nodeTypes.TAG } node - a tag node
  	 * @returns { riotParser.nodeTypes.ATTR } attribute node
  	 */function findAttribute(name,node){return node.attributes&&node.attributes.find(attr=>getName$1(attr)===name);}const findIfAttribute=curry(findAttribute)(IF_DIRECTIVE);const findEachAttribute=curry(findAttribute)(EACH_DIRECTIVE);const findKeyAttribute=curry(findAttribute)(KEY_ATTRIBUTE);const findIsAttribute=curry(findAttribute)(IS_DIRECTIVE);const hasIfAttribute=compose(Boolean,findIfAttribute);const hasEachAttribute=compose(Boolean,findEachAttribute);const hasKeyAttribute=compose(Boolean,findKeyAttribute);const hasIsAttribute=compose(Boolean,findIsAttribute);/**
  	 * Check if a node name is part of the browser or builtin javascript api or it belongs to the current scope
  	 * @param   { types.NodePath } path - containing the current node visited
  	 * @returns {boolean} true if it's a global api variable
  	 */function isGlobal(_ref3){let{scope,node}=_ref3;return Boolean(isRaw(node)||isBuiltinAPI(node)||isBrowserAPI(node)||isNodeInScope(scope,node));}/**
  	 * Checks if the identifier of a given node exists in a scope
  	 * @param {Scope} scope - scope where to search for the identifier
  	 * @param {types.Node} node - node to search for the identifier
  	 * @returns {boolean} true if the node identifier is defined in the given scope
  	 */function isNodeInScope(scope,node){const traverse=function traverse(isInScope){if(isInScope===void 0){isInScope=false;}types$1.visit(node,{visitIdentifier(path){if(scope.lookup(getName$1(path.node))){isInScope=true;}this.abort();}});return isInScope;};return traverse();}/**
  	 * Replace the path scope with a member Expression
  	 * @param   { types.NodePath } path - containing the current node visited
  	 * @param   { types.Node } property - node we want to prefix with the scope identifier
  	 * @returns {undefined} this is a void function
  	 */function replacePathScope(path,property){path.replace(builders.memberExpression(scope$1,property,false));}/**
  	 * Change the nodes scope adding the `scope` prefix
  	 * @param   { types.NodePath } path - containing the current node visited
  	 * @returns { boolean } return false if we want to stop the tree traversal
  	 * @context { types.visit }
  	 */function updateNodeScope(path){if(!isGlobal(path)){replacePathScope(path,path.node);return false;}this.traverse(path);}/**
  	 * Change the scope of the member expressions
  	 * @param   { types.NodePath } path - containing the current node visited
  	 * @returns { boolean } return always false because we want to check only the first node object
  	 */function visitMemberExpression(path){if(!isGlobal(path)&&!isGlobal({node:path.node.object,scope:path.scope})){if(isBinaryExpression(path.node.object)){this.traverse(path.get('object'));}else if(path.value.computed){this.traverse(path);}else{replacePathScope(path,isThisExpression(path.node.object)?path.node.property:path.node);}}return false;}/**
  	 * Objects properties should be handled a bit differently from the Identifier
  	 * @param   { types.NodePath } path - containing the current node visited
  	 * @returns { boolean } return false if we want to stop the tree traversal
  	 */function visitProperty(path){const value=path.node.value;if(isIdentifier(value)){updateNodeScope(path.get('value'));}else{this.traverse(path.get('value'));}return false;}/**
  	 * The this expressions should be replaced with the scope
  	 * @param   { types.NodePath } path - containing the current node visited
  	 * @returns { boolean|undefined } return false if we want to stop the tree traversal
  	 */function visitThisExpression(path){path.replace(scope$1);this.traverse(path);}/**
  	 * Update the scope of the global nodes
  	 * @param   { Object } ast - ast program
  	 * @returns { Object } the ast program with all the global nodes updated
  	 */function updateNodesScope(ast){const ignorePath=()=>false;types$1.visit(ast,{visitIdentifier:updateNodeScope,visitMemberExpression,visitProperty,visitThisExpression,visitClassExpression:ignorePath});return ast;}/**
  	 * Convert any expression to an AST tree
  	 * @param   { Object } expression - expression parsed by the riot parser
  	 * @param   { string } sourceFile - original tag file
  	 * @param   { string } sourceCode - original tag source code
  	 * @returns { Object } the ast generated
  	 */function createASTFromExpression(expression,sourceFile,sourceCode){const code=sourceFile?addLineOffset(expression.text,sourceCode,expression):expression.text;return generateAST(`(${code})`,{sourceFileName:sourceFile});}/**
  	 * Create the bindings template property
  	 * @param   {Array} args - arguments to pass to the template function
  	 * @returns {ASTNode} a binding template key
  	 */function createTemplateProperty(args){return simplePropertyNode(BINDING_TEMPLATE_KEY,args?callTemplateFunction(...args):nullNode());}/**
  	 * Try to get the expression of an attribute node
  	 * @param   { RiotParser.Node.Attribute } attribute - riot parser attribute node
  	 * @returns { RiotParser.Node.Expression } attribute expression value
  	 */function getAttributeExpression(attribute){return attribute.expressions?attribute.expressions[0]:Object.assign({},attribute,{text:attribute.value});}/**
  	 * Wrap the ast generated in a function call providing the scope argument
  	 * @param   {Object} ast - function body
  	 * @returns {FunctionExpresion} function having the scope argument injected
  	 */function wrapASTInFunctionWithScope(ast){return builders.functionExpression(null,[scope$1],builders.blockStatement([builders.returnStatement(ast)]));}/**
  	 * Convert any parser option to a valid template one
  	 * @param   { RiotParser.Node.Expression } expression - expression parsed by the riot parser
  	 * @param   { string } sourceFile - original tag file
  	 * @param   { string } sourceCode - original tag source code
  	 * @returns { Object } a FunctionExpression object
  	 *
  	 * @example
  	 *  toScopedFunction('foo + bar') // scope.foo + scope.bar
  	 *
  	 * @example
  	 *  toScopedFunction('foo.baz + bar') // scope.foo.baz + scope.bar
  	 */function toScopedFunction(expression,sourceFile,sourceCode){return compose(wrapASTInFunctionWithScope,transformExpression)(expression,sourceFile,sourceCode);}/**
  	 * Transform an expression node updating its global scope
  	 * @param   {RiotParser.Node.Expr} expression - riot parser expression node
  	 * @param   {string} sourceFile - source file
  	 * @param   {string} sourceCode - source code
  	 * @returns {ASTExpression} ast expression generated from the riot parser expression node
  	 */function transformExpression(expression,sourceFile,sourceCode){return compose(getExpressionAST,updateNodesScope,createASTFromExpression)(expression,sourceFile,sourceCode);}/**
  	 * Get the parsed AST expression of riot expression node
  	 * @param   {AST.Program} sourceAST - raw node parsed
  	 * @returns {AST.Expression} program expression output
  	 */function getExpressionAST(sourceAST){const astBody=sourceAST.program.body;return astBody[0]?astBody[0].expression:astBody;}/**
  	 * Create the template call function
  	 * @param   {Array|string|Node.Literal} template - template string
  	 * @param   {Array<AST.Nodes>} bindings - template bindings provided as AST nodes
  	 * @returns {Node.CallExpression} template call expression
  	 */function callTemplateFunction(template,bindings){return builders.callExpression(builders.identifier(TEMPLATE_FN),[template?builders.literal(template):nullNode(),bindings?builders.arrayExpression(bindings):nullNode()]);}/**
  	 * Convert any DOM attribute into a valid DOM selector useful for the querySelector API
  	 * @param   { string } attributeName - name of the attribute to query
  	 * @returns { string } the attribute transformed to a query selector
  	 */const attributeNameToDOMQuerySelector=attributeName=>`[${attributeName}]`;/**
  	 * Create the properties to query a DOM node
  	 * @param   { string } attributeName - attribute name needed to identify a DOM node
  	 * @returns { Array<AST.Node> } array containing the selector properties needed for the binding
  	 */function createSelectorProperties(attributeName){return attributeName?[simplePropertyNode(BINDING_REDUNDANT_ATTRIBUTE_KEY,builders.literal(attributeName)),simplePropertyNode(BINDING_SELECTOR_KEY,compose(builders.literal,attributeNameToDOMQuerySelector)(attributeName))]:[];}/**
  	 * Clean binding or custom attributes
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {Array<RiotParser.Node.Attr>} only the attributes that are not bindings or directives
  	 */function cleanAttributes(node){return getNodeAttributes(node).filter(attribute=>![IF_DIRECTIVE,EACH_DIRECTIVE,KEY_ATTRIBUTE,SLOT_ATTRIBUTE,IS_DIRECTIVE].includes(attribute.name));}/**
  	 * Clone the node filtering out the selector attribute from the attributes list
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @param   {string} selectorAttribute - name of the selector attribute to filter out
  	 * @returns {RiotParser.Node} the node with the attribute cleaned up
  	 */function cloneNodeWithoutSelectorAttribute(node,selectorAttribute){return Object.assign({},node,{attributes:getAttributesWithoutSelector(getNodeAttributes(node),selectorAttribute)});}/**
  	 * Get the node attributes without the selector one
  	 * @param   {Array<RiotParser.Attr>} attributes - attributes list
  	 * @param   {string} selectorAttribute - name of the selector attribute to filter out
  	 * @returns {Array<RiotParser.Attr>} filtered attributes
  	 */function getAttributesWithoutSelector(attributes,selectorAttribute){if(selectorAttribute)return attributes.filter(attribute=>attribute.name!==selectorAttribute);return attributes;}/**
  	 * Create a root node proxing only its nodes and attributes
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {RiotParser.Node} root node
  	 */function createRootNode(node){return {nodes:getChildrenNodes(node),isRoot:true,// root nodes shuold't have directives
  attributes:cleanAttributes(node)};}/**
  	 * Get all the child nodes of a RiotParser.Node
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {Array<RiotParser.Node>} all the child nodes found
  	 */function getChildrenNodes(node){return node&&node.nodes?node.nodes:[];}/**
  	 * Get all the attributes of a riot parser node
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {Array<RiotParser.Node.Attribute>} all the attributes find
  	 */function getNodeAttributes(node){return node.attributes?node.attributes:[];}/**
  	 * Get the name of a custom node transforming it into an expression node
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {RiotParser.Node.Attr} the node name as expression attribute
  	 */function getCustomNodeNameAsExpression(node){const isAttribute=findIsAttribute(node);const toRawString=val=>`'${val}'`;if(isAttribute){return isAttribute.expressions?isAttribute.expressions[0]:Object.assign({},isAttribute,{text:toRawString(isAttribute.value)});}return Object.assign({},node,{text:toRawString(getName$1(node))});}/**
  	 * Find all the node attributes that are not expressions
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {Array} list of all the static attributes
  	 */function findStaticAttributes(node){return getNodeAttributes(node).filter(attribute=>!hasExpressions(attribute));}/**
  	 * Find all the node attributes that have expressions
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {Array} list of all the dynamic attributes
  	 */function findDynamicAttributes(node){return getNodeAttributes(node).filter(hasExpressions);}/**
  	 * True if the node has the isCustom attribute set
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true if either it's a riot component or a custom element
  	 */function isCustomNode(node){return !!(node[IS_CUSTOM_NODE]||hasIsAttribute(node));}/**
  	 * True the node is <slot>
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true if it's a slot node
  	 */function isSlotNode(node){return node.name===SLOT_TAG_NODE_NAME;}/**
  	 * True if the node has the isVoid attribute set
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true if the node is self closing
  	 */function isVoidNode(node){return !!node[IS_VOID_NODE];}/**
  	 * True if the riot parser did find a tag node
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true only for the tag nodes
  	 */function isTagNode(node){return node.type===nodeTypes.TAG;}/**
  	 * True if the riot parser did find a text node
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true only for the text nodes
  	 */function isTextNode(node){return node.type===nodeTypes.TEXT;}/**
  	 * True if the node parsed is the root one
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true only for the root nodes
  	 */function isRootNode(node){return node.isRoot;}/**
  	 * True if the attribute parsed is of type spread one
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true if the attribute node is of type spread
  	 */function isSpreadAttribute$1(node){return node[IS_SPREAD_ATTRIBUTE];}/**
  	 * True if the node is an attribute and its name is "value"
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true only for value attribute nodes
  	 */function isValueAttribute(node){return node.name==='value';}/**
  	 * True if the node is an attribute and a DOM handler
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true only for dom listener attribute nodes
  	 */const isEventAttribute=(()=>{const EVENT_ATTR_RE=/^on/;return node=>EVENT_ATTR_RE.test(node.name);})();/**
  	 * True if the node has expressions or expression attributes
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} ditto
  	 */function hasExpressions(node){return !!(node.expressions||// has expression attributes
  getNodeAttributes(node).some(attribute=>hasExpressions(attribute))||// has child text nodes with expressions
  node.nodes&&node.nodes.some(node=>isTextNode(node)&&hasExpressions(node)));}/**
  	 * Convert all the node static attributes to strings
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {string} all the node static concatenated as string
  	 */function staticAttributesToString(node){return findStaticAttributes(node).map(attribute=>attribute[IS_BOOLEAN_ATTRIBUTE]||!attribute.value?attribute.name:`${attribute.name}="${unescapeNode(attribute,'value').value}"`).join(' ');}/**
  	 * Make sure that node escaped chars will be unescaped
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @param   {string} key - key property to unescape
  	 * @returns {RiotParser.Node} node with the text property unescaped
  	 */function unescapeNode(node,key){if(node.unescape){return Object.assign({},node,{[key]:unescapeChar(node[key],node.unescape)});}return node;}/**
  	 * Convert a riot parser opening node into a string
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {string} the node as string
  	 */function nodeToString(node){const attributes=staticAttributesToString(node);switch(true){case isTagNode(node):return `<${node.name}${attributes?` ${attributes}`:''}${isVoidNode(node)?'/':''}>`;case isTextNode(node):return hasExpressions(node)?TEXT_NODE_EXPRESSION_PLACEHOLDER:unescapeNode(node,'text').text;default:return '';}}/**
  	 * Close an html node
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {string} the closing tag of the html tag node passed to this function
  	 */function closeTag(node){return node.name?`</${node.name}>`:'';}/**
  	 * True if the node has not expression set nor bindings directives
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true only if it's a static node that doesn't need bindings or expressions
  	 */function isStaticNode(node){return [hasExpressions,findEachAttribute,findIfAttribute,isCustomNode,isSlotNode].every(test=>!test(node));}/**
  	 * True if the node is a directive having its own template
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @returns {boolean} true only for the IF EACH and TAG bindings
  	 */function hasItsOwnTemplate(node){return [findEachAttribute,findIfAttribute,isCustomNode].some(test=>test(node));}/**
  	 * Create a strings array with the `join` call to transform it into a string
  	 * @param   {Array} stringsArray - array containing all the strings to concatenate
  	 * @returns {AST.CallExpression} array with a `join` call
  	 */function createArrayString(stringsArray){return builders.callExpression(builders.memberExpression(builders.arrayExpression(stringsArray),builders.identifier('join'),false),[builders.literal('')]);}/**
  	 * Create a selector that will be used to find the node via dom-bindings
  	 * @param   {number} id - temporary variable that will be increased anytime this function will be called
  	 * @returns {string} selector attribute needed to bind a riot expression
  	 */const createBindingSelector=function createSelector(id){if(id===void 0){id=0;}return ()=>`${BINDING_SELECTOR_PREFIX}${id++}`;}();/**
  	 * Simple clone deep function, do not use it for classes or recursive objects!
  	 * @param   {*} source - possibily an object to clone
  	 * @returns {*} the object we wanted to clone
  	 */function cloneDeep(source){return JSON.parse(JSON.stringify(source));}const getEachItemName=expression=>isSequenceExpression(expression.left)?expression.left.expressions[0]:expression.left;const getEachIndexName=expression=>isSequenceExpression(expression.left)?expression.left.expressions[1]:null;const getEachValue=expression=>expression.right;const nameToliteral=compose(builders.literal,getName$1);const generateEachItemNameKey=expression=>simplePropertyNode(BINDING_ITEM_NAME_KEY,compose(nameToliteral,getEachItemName)(expression));const generateEachIndexNameKey=expression=>simplePropertyNode(BINDING_INDEX_NAME_KEY,compose(nameToliteral,getEachIndexName)(expression));const generateEachEvaluateKey=(expression,eachExpression,sourceFile,sourceCode)=>simplePropertyNode(BINDING_EVALUATE_KEY,compose(e=>toScopedFunction(e,sourceFile,sourceCode),e=>Object.assign({},eachExpression,{text:generateJavascript(e).code}),getEachValue)(expression));/**
  	 * Get the each expression properties to create properly the template binding
  	 * @param   { DomBinding.Expression } eachExpression - original each expression data
  	 * @param   { string } sourceFile - original tag file
  	 * @param   { string } sourceCode - original tag source code
  	 * @returns { Array } AST nodes that are needed to build an each binding
  	 */function generateEachExpressionProperties(eachExpression,sourceFile,sourceCode){const ast=createASTFromExpression(eachExpression,sourceFile,sourceCode);const body=ast.program.body;const firstNode=body[0];if(!isExpressionStatement(firstNode)){panic(`The each directives supported should be of type "ExpressionStatement",you have provided a "${firstNode.type}"`);}const{expression}=firstNode;return [generateEachItemNameKey(expression),generateEachIndexNameKey(expression),generateEachEvaluateKey(expression,eachExpression,sourceFile,sourceCode)];}/**
  	 * Transform a RiotParser.Node.Tag into an each binding
  	 * @param   { RiotParser.Node.Tag } sourceNode - tag containing the each attribute
  	 * @param   { string } selectorAttribute - attribute needed to select the target node
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @returns { AST.Node } an each binding node
  	 */function createEachBinding(sourceNode,selectorAttribute,sourceFile,sourceCode){const[ifAttribute,eachAttribute,keyAttribute]=[findIfAttribute,findEachAttribute,findKeyAttribute].map(f=>f(sourceNode));const attributeOrNull=attribute=>attribute?toScopedFunction(getAttributeExpression(attribute),sourceFile,sourceCode):nullNode();return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(BINDING_TYPES),builders.identifier(EACH_BINDING_TYPE),false)),simplePropertyNode(BINDING_GET_KEY_KEY,attributeOrNull(keyAttribute)),simplePropertyNode(BINDING_CONDITION_KEY,attributeOrNull(ifAttribute)),createTemplateProperty(createNestedBindings(sourceNode,sourceFile,sourceCode,selectorAttribute)),...createSelectorProperties(selectorAttribute),...compose(generateEachExpressionProperties,getAttributeExpression)(eachAttribute)]);}/**
  	 * Transform a RiotParser.Node.Tag into an if binding
  	 * @param   { RiotParser.Node.Tag } sourceNode - tag containing the if attribute
  	 * @param   { string } selectorAttribute - attribute needed to select the target node
  	 * @param   { stiring } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @returns { AST.Node } an if binding node
  	 */function createIfBinding(sourceNode,selectorAttribute,sourceFile,sourceCode){const ifAttribute=findIfAttribute(sourceNode);return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(BINDING_TYPES),builders.identifier(IF_BINDING_TYPE),false)),simplePropertyNode(BINDING_EVALUATE_KEY,toScopedFunction(ifAttribute.expressions[0],sourceFile,sourceCode)),...createSelectorProperties(selectorAttribute),createTemplateProperty(createNestedBindings(sourceNode,sourceFile,sourceCode,selectorAttribute))]);}/**
  	 * Simple expression bindings might contain multiple expressions like for example: "class="{foo} red {bar}""
  	 * This helper aims to merge them in a template literal if it's necessary
  	 * @param   {RiotParser.Attr} node - riot parser node
  	 * @param   {string} sourceFile - original tag file
  	 * @param   {string} sourceCode - original tag source code
  	 * @returns { Object } a template literal expression object
  	 */function mergeAttributeExpressions(node,sourceFile,sourceCode){if(!node.parts||node.parts.length===1)return transformExpression(node.expressions[0],sourceFile,sourceCode);const stringsArray=[...node.parts.reduce((acc,str)=>{const expression=node.expressions.find(e=>e.text.trim()===str);return [...acc,expression?transformExpression(expression,sourceFile,sourceCode):builders.literal(str)];},[])].filter(expr=>!isLiteral(expr)||expr.value);return createArrayString(stringsArray);}/**
  	 * Create a simple attribute expression
  	 * @param   {RiotParser.Node.Attr} sourceNode - the custom tag
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @returns {AST.Node} object containing the expression binding keys
  	 */function createAttributeExpression(sourceNode,sourceFile,sourceCode){return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(EXPRESSION_TYPES),builders.identifier(ATTRIBUTE_EXPRESSION_TYPE),false)),simplePropertyNode(BINDING_NAME_KEY,isSpreadAttribute$1(sourceNode)?nullNode():builders.literal(sourceNode.name)),simplePropertyNode(BINDING_EVALUATE_KEY,hasExpressions(sourceNode)?// dynamic attribute
  wrapASTInFunctionWithScope(mergeAttributeExpressions(sourceNode,sourceFile,sourceCode)):// static attribute
  builders.functionExpression(null,[],builders.blockStatement([builders.returnStatement(builders.literal(sourceNode.value||true))])))]);}/**
  	 * Create a simple event expression
  	 * @param   {RiotParser.Node.Attr} sourceNode - attribute containing the event handlers
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @returns {AST.Node} object containing the expression binding keys
  	 */function createEventExpression(sourceNode,sourceFile,sourceCode){return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(EXPRESSION_TYPES),builders.identifier(EVENT_EXPRESSION_TYPE),false)),simplePropertyNode(BINDING_NAME_KEY,builders.literal(sourceNode.name)),simplePropertyNode(BINDING_EVALUATE_KEY,toScopedFunction(sourceNode.expressions[0],sourceFile,sourceCode))]);}/**
  	 * Generate the pure immutable string chunks from a RiotParser.Node.Text
  	 * @param   {RiotParser.Node.Text} node - riot parser text node
  	 * @param   {string} sourceCode sourceCode - source code
  	 * @returns {Array} array containing the immutable string chunks
  	 */function generateLiteralStringChunksFromNode(node,sourceCode){return node.expressions.reduce((chunks,expression,index)=>{const start=index?node.expressions[index-1].end:node.start;chunks.push(sourceCode.substring(start,expression.start));// add the tail to the string
  if(index===node.expressions.length-1)chunks.push(sourceCode.substring(expression.end,node.end));return chunks;},[]).map(str=>node.unescape?unescapeChar(str,node.unescape):str);}/**
  	 * Simple bindings might contain multiple expressions like for example: "{foo} and {bar}"
  	 * This helper aims to merge them in a template literal if it's necessary
  	 * @param   {RiotParser.Node} node - riot parser node
  	 * @param   {string} sourceFile - original tag file
  	 * @param   {string} sourceCode - original tag source code
  	 * @returns { Object } a template literal expression object
  	 */function mergeNodeExpressions(node,sourceFile,sourceCode){if(node.parts.length===1)return transformExpression(node.expressions[0],sourceFile,sourceCode);const pureStringChunks=generateLiteralStringChunksFromNode(node,sourceCode);const stringsArray=pureStringChunks.reduce((acc,str,index)=>{const expr=node.expressions[index];return [...acc,builders.literal(str),expr?transformExpression(expr,sourceFile,sourceCode):nullNode()];},[])// filter the empty literal expressions
  .filter(expr=>!isLiteral(expr)||expr.value);return createArrayString(stringsArray);}/**
  	 * Create a text expression
  	 * @param   {RiotParser.Node.Text} sourceNode - text node to parse
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @param   {number} childNodeIndex - position of the child text node in its parent children nodes
  	 * @returns {AST.Node} object containing the expression binding keys
  	 */function createTextExpression(sourceNode,sourceFile,sourceCode,childNodeIndex){return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(EXPRESSION_TYPES),builders.identifier(TEXT_EXPRESSION_TYPE),false)),simplePropertyNode(BINDING_CHILD_NODE_INDEX_KEY,builders.literal(childNodeIndex)),simplePropertyNode(BINDING_EVALUATE_KEY,wrapASTInFunctionWithScope(mergeNodeExpressions(sourceNode,sourceFile,sourceCode)))]);}function createValueExpression(sourceNode,sourceFile,sourceCode){return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(EXPRESSION_TYPES),builders.identifier(VALUE_EXPRESSION_TYPE),false)),simplePropertyNode(BINDING_EVALUATE_KEY,toScopedFunction(sourceNode.expressions[0],sourceFile,sourceCode))]);}function createExpression(sourceNode,sourceFile,sourceCode,childNodeIndex){switch(true){case isTextNode(sourceNode):return createTextExpression(sourceNode,sourceFile,sourceCode,childNodeIndex);case isValueAttribute(sourceNode):return createValueExpression(sourceNode,sourceFile,sourceCode);case isEventAttribute(sourceNode):return createEventExpression(sourceNode,sourceFile,sourceCode);default:return createAttributeExpression(sourceNode,sourceFile,sourceCode);}}/**
  	 * Create the attribute expressions
  	 * @param   {RiotParser.Node} sourceNode - any kind of node parsed via riot parser
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @returns {Array} array containing all the attribute expressions
  	 */function createAttributeExpressions(sourceNode,sourceFile,sourceCode){return findDynamicAttributes(sourceNode).map(attribute=>createExpression(attribute,sourceFile,sourceCode));}/**
  	 * Create the text node expressions
  	 * @param   {RiotParser.Node} sourceNode - any kind of node parsed via riot parser
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @returns {Array} array containing all the text node expressions
  	 */function createTextNodeExpressions(sourceNode,sourceFile,sourceCode){const childrenNodes=getChildrenNodes(sourceNode);return childrenNodes.filter(isTextNode).filter(hasExpressions).map(node=>createExpression(node,sourceFile,sourceCode,childrenNodes.indexOf(node)));}/**
  	 * Add a simple binding to a riot parser node
  	 * @param   { RiotParser.Node.Tag } sourceNode - tag containing the if attribute
  	 * @param   { string } selectorAttribute - attribute needed to select the target node
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @returns { AST.Node } an each binding node
  	 */function createSimpleBinding(sourceNode,selectorAttribute,sourceFile,sourceCode){return builders.objectExpression([...createSelectorProperties(selectorAttribute),simplePropertyNode(BINDING_EXPRESSIONS_KEY,builders.arrayExpression([...createTextNodeExpressions(sourceNode,sourceFile,sourceCode),...createAttributeExpressions(sourceNode,sourceFile,sourceCode)]))]);}/**
  	 * Transform a RiotParser.Node.Tag of type slot into a slot binding
  	 * @param   { RiotParser.Node.Tag } sourceNode - slot node
  	 * @param   { string } selectorAttribute - attribute needed to select the target node
  	 * @returns { AST.Node } a slot binding node
  	 */function createSlotBinding(sourceNode,selectorAttribute){const slotNameAttribute=findAttribute(NAME_ATTRIBUTE,sourceNode);const slotName=slotNameAttribute?slotNameAttribute.value:DEFAULT_SLOT_NAME;return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(BINDING_TYPES),builders.identifier(SLOT_BINDING_TYPE),false)),simplePropertyNode(BINDING_NAME_KEY,builders.literal(slotName)),...createSelectorProperties(selectorAttribute)]);}/**
  	 * Find the slots in the current component and group them under the same id
  	 * @param   {RiotParser.Node.Tag} sourceNode - the custom tag
  	 * @returns {Object} object containing all the slots grouped by name
  	 */function groupSlots(sourceNode){return getChildrenNodes(sourceNode).reduce((acc,node)=>{const slotAttribute=findSlotAttribute(node);if(slotAttribute){acc[slotAttribute.value]=node;}else{acc.default=createRootNode({nodes:[...getChildrenNodes(acc.default),node]});}return acc;},{default:null});}/**
  	 * Create the slot entity to pass to the riot-dom bindings
  	 * @param   {string} id - slot id
  	 * @param   {RiotParser.Node.Tag} sourceNode - slot root node
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @returns {AST.Node} ast node containing the slot object properties
  	 */function buildSlot(id,sourceNode,sourceFile,sourceCode){const cloneNode=Object.assign({},sourceNode,{// avoid to render the slot attribute
  attributes:getNodeAttributes(sourceNode).filter(attribute=>attribute.name!==SLOT_ATTRIBUTE)});const[html,bindings]=build(cloneNode,sourceFile,sourceCode);return builders.objectExpression([simplePropertyNode(BINDING_ID_KEY,builders.literal(id)),simplePropertyNode(BINDING_HTML_KEY,builders.literal(html)),simplePropertyNode(BINDING_BINDINGS_KEY,builders.arrayExpression(bindings))]);}/**
  	 * Create the AST array containing the slots
  	 * @param   { RiotParser.Node.Tag } sourceNode - the custom tag
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @returns {AST.ArrayExpression} array containing the attributes to bind
  	 */function createSlotsArray(sourceNode,sourceFile,sourceCode){return builders.arrayExpression([...compose(slots=>slots.map((_ref4)=>{let[key,value]=_ref4;return buildSlot(key,value,sourceFile,sourceCode);}),slots=>slots.filter((_ref5)=>{let[,value]=_ref5;return value;}),Object.entries,groupSlots)(sourceNode)]);}/**
  	 * Create the AST array containing the attributes to bind to this node
  	 * @param   { RiotParser.Node.Tag } sourceNode - the custom tag
  	 * @param   { string } selectorAttribute - attribute needed to select the target node
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @returns {AST.ArrayExpression} array containing the slot objects
  	 */function createBindingAttributes(sourceNode,selectorAttribute,sourceFile,sourceCode){return builders.arrayExpression([...compose(attributes=>attributes.map(attribute=>createExpression(attribute,sourceFile,sourceCode)),attributes=>getAttributesWithoutSelector(attributes,selectorAttribute),// eslint-disable-line
  cleanAttributes)(sourceNode)]);}/**
  	 * Find the slot attribute if it exists
  	 * @param   {RiotParser.Node.Tag} sourceNode - the custom tag
  	 * @returns {RiotParser.Node.Attr|undefined} the slot attribute found
  	 */function findSlotAttribute(sourceNode){return getNodeAttributes(sourceNode).find(attribute=>attribute.name===SLOT_ATTRIBUTE);}/**
  	 * Transform a RiotParser.Node.Tag into a tag binding
  	 * @param   { RiotParser.Node.Tag } sourceNode - the custom tag
  	 * @param   { string } selectorAttribute - attribute needed to select the target node
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @returns { AST.Node } tag binding node
  	 */function createTagBinding(sourceNode,selectorAttribute,sourceFile,sourceCode){return builders.objectExpression([simplePropertyNode(BINDING_TYPE_KEY,builders.memberExpression(builders.identifier(BINDING_TYPES),builders.identifier(TAG_BINDING_TYPE),false)),simplePropertyNode(BINDING_GET_COMPONENT_KEY,builders.identifier(GET_COMPONENT_FN)),simplePropertyNode(BINDING_EVALUATE_KEY,toScopedFunction(getCustomNodeNameAsExpression(sourceNode),sourceFile,sourceCode)),simplePropertyNode(BINDING_SLOTS_KEY,createSlotsArray(sourceNode,sourceFile,sourceCode)),simplePropertyNode(BINDING_ATTRIBUTES_KEY,createBindingAttributes(sourceNode,selectorAttribute,sourceFile,sourceCode)),...createSelectorProperties(selectorAttribute)]);}const BuildingState=Object.freeze({html:[],bindings:[],parent:null});/**
  	 * Nodes having bindings should be cloned and new selector properties should be added to them
  	 * @param   {RiotParser.Node} sourceNode - any kind of node parsed via riot parser
  	 * @param   {string} bindingsSelector - temporary string to identify the current node
  	 * @returns {RiotParser.Node} the original node parsed having the new binding selector attribute
  	 */function createBindingsTag(sourceNode,bindingsSelector){if(!bindingsSelector)return sourceNode;return Object.assign({},sourceNode,{// inject the selector bindings into the node attributes
  attributes:[{name:bindingsSelector},...getNodeAttributes(sourceNode)]});}/**
  	 * Create a generic dynamic node (text or tag) and generate its bindings
  	 * @param   {RiotParser.Node} sourceNode - any kind of node parsed via riot parser
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @param   {BuildingState} state - state representing the current building tree state during the recursion
  	 * @returns {Array} array containing the html output and bindings for the current node
  	 */function createDynamicNode(sourceNode,sourceFile,sourceCode,state){switch(true){case isTextNode(sourceNode):// text nodes will not have any bindings
  return [nodeToString(sourceNode),[]];default:return createTagWithBindings(sourceNode,sourceFile,sourceCode);}}/**
  	 * Create only a dynamic tag node with generating a custom selector and its bindings
  	 * @param   {RiotParser.Node} sourceNode - any kind of node parsed via riot parser
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @param   {BuildingState} state - state representing the current building tree state during the recursion
  	 * @returns {Array} array containing the html output and bindings for the current node
  	 */function createTagWithBindings(sourceNode,sourceFile,sourceCode){const bindingsSelector=isRootNode(sourceNode)?null:createBindingSelector();const cloneNode=createBindingsTag(sourceNode,bindingsSelector);const tagOpeningHTML=nodeToString(cloneNode);switch(true){// EACH bindings have prio 1
  case hasEachAttribute(cloneNode):return [tagOpeningHTML,[createEachBinding(cloneNode,bindingsSelector,sourceFile,sourceCode)]];// IF bindings have prio 2
  case hasIfAttribute(cloneNode):return [tagOpeningHTML,[createIfBinding(cloneNode,bindingsSelector,sourceFile,sourceCode)]];// TAG bindings have prio 3
  case isCustomNode(cloneNode):return [tagOpeningHTML,[createTagBinding(cloneNode,bindingsSelector,sourceFile,sourceCode)]];// slot tag
  case isSlotNode(cloneNode):return [tagOpeningHTML,[createSlotBinding(cloneNode,bindingsSelector)]];// this node has expressions bound to it
  default:return [tagOpeningHTML,[createSimpleBinding(cloneNode,bindingsSelector,sourceFile,sourceCode)]];}}/**
  	 * Parse a node trying to extract its template and bindings
  	 * @param   {RiotParser.Node} sourceNode - any kind of node parsed via riot parser
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @param   {BuildingState} state - state representing the current building tree state during the recursion
  	 * @returns {Array} array containing the html output and bindings for the current node
  	 */function parseNode(sourceNode,sourceFile,sourceCode,state){// static nodes have no bindings
  if(isStaticNode(sourceNode))return [nodeToString(sourceNode),[]];return createDynamicNode(sourceNode,sourceFile,sourceCode);}/**
  	 * Create the tag binding
  	 * @param   { RiotParser.Node.Tag } sourceNode - tag containing the each attribute
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @param   { string } selector - binding selector
  	 * @returns { Array } array with only the tag binding AST
  	 */function createNestedBindings(sourceNode,sourceFile,sourceCode,selector){const mightBeARiotComponent=isCustomNode(sourceNode);return mightBeARiotComponent?[null,[createTagBinding(cloneNodeWithoutSelectorAttribute(sourceNode,selector),null,sourceFile,sourceCode)]]:build(createRootNode(sourceNode),sourceFile,sourceCode);}/**
  	 * Build the template and the bindings
  	 * @param   {RiotParser.Node} sourceNode - any kind of node parsed via riot parser
  	 * @param   {string} sourceFile - source file path
  	 * @param   {string} sourceCode - original source
  	 * @param   {BuildingState} state - state representing the current building tree state during the recursion
  	 * @returns {Array} array containing the html output and the dom bindings
  	 */function build(sourceNode,sourceFile,sourceCode,state){if(!sourceNode)panic('Something went wrong with your tag DOM parsing, your tag template can\'t be created');const[nodeHTML,nodeBindings]=parseNode(sourceNode,sourceFile,sourceCode);const childrenNodes=getChildrenNodes(sourceNode);const currentState=Object.assign({},cloneDeep(BuildingState),{},state);// mutate the original arrays
  currentState.html.push(...nodeHTML);currentState.bindings.push(...nodeBindings);// do recursion if
  // this tag has children and it has no special directives bound to it
  if(childrenNodes.length&&!hasItsOwnTemplate(sourceNode)){childrenNodes.forEach(node=>build(node,sourceFile,sourceCode,Object.assign({parent:sourceNode},currentState)));}// close the tag if it's not a void one
  if(isTagNode(sourceNode)&&!isVoidNode(sourceNode)){currentState.html.push(closeTag(sourceNode));}return [currentState.html.join(''),currentState.bindings];}const templateFunctionArguments=[TEMPLATE_FN,EXPRESSION_TYPES,BINDING_TYPES,GET_COMPONENT_FN].map(builders.identifier);/**
  	 * Create the content of the template function
  	 * @param   { RiotParser.Node } sourceNode - node generated by the riot compiler
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @returns {AST.BlockStatement} the content of the template function
  	 */function createTemplateFunctionContent(sourceNode,sourceFile,sourceCode){return builders.blockStatement([builders.returnStatement(callTemplateFunction(...build(createRootNode(sourceNode),sourceFile,sourceCode)))]);}/**
  	 * Extend the AST adding the new template property containing our template call to render the component
  	 * @param   { Object } ast - current output ast
  	 * @param   { string } sourceFile - source file path
  	 * @param   { string } sourceCode - original source
  	 * @param   { RiotParser.Node } sourceNode - node generated by the riot compiler
  	 * @returns { Object } the output ast having the "template" key
  	 */function extendTemplateProperty(ast,sourceFile,sourceCode,sourceNode){types$1.visit(ast,{visitProperty(path){if(path.value.key.value===TAG_TEMPLATE_PROPERTY){path.value.value=builders.functionExpression(null,templateFunctionArguments,createTemplateFunctionContent(sourceNode,sourceFile,sourceCode));return false;}this.traverse(path);}});return ast;}/**
  	 * Generate the component template logic
  	 * @param   { RiotParser.Node } sourceNode - node generated by the riot compiler
  	 * @param   { string } source - original component source code
  	 * @param   { Object } meta - compilation meta information
  	 * @param   { AST } ast - current AST output
  	 * @returns { AST } the AST generated
  	 */function template(sourceNode,source,meta,ast){const{options}=meta;return extendTemplateProperty(ast,options.file,source,sourceNode);}const DEFAULT_OPTIONS={template:'default',file:'[unknown-source-file]',scopedCss:true};/**
  	 * Create the initial AST
  	 * @param {string} tagName - the name of the component we have compiled
  	 * @returns { AST } the initial AST
  	 *
  	 * @example
  	 * // the output represents the following string in AST
  	 */function createInitialInput(_ref6){let{tagName}=_ref6;/*
  	  generates
  	  export default {
  	     ${TAG_CSS_PROPERTY}: null,
  	     ${TAG_LOGIC_PROPERTY}: null,
  	     ${TAG_TEMPLATE_PROPERTY}: null
  	  }
  	  */return builders.program([builders.exportDefaultDeclaration(builders.objectExpression([simplePropertyNode(TAG_CSS_PROPERTY,nullNode()),simplePropertyNode(TAG_LOGIC_PROPERTY,nullNode()),simplePropertyNode(TAG_TEMPLATE_PROPERTY,nullNode()),simplePropertyNode(TAG_NAME_PROPERTY,builders.literal(tagName))]))]);}/**
  	 * Make sure the input sourcemap is valid otherwise we ignore it
  	 * @param   {SourceMapGenerator} map - preprocessor source map
  	 * @returns {Object} sourcemap as json or nothing
  	 */function normaliseInputSourceMap(map){const inputSourceMap=sourcemapAsJSON(map);return isEmptySourcemap(inputSourceMap)?null:inputSourceMap;}/**
  	 * Override the sourcemap content making sure it will always contain the tag source code
  	 * @param   {Object} map - sourcemap as json
  	 * @param   {string} source - component source code
  	 * @returns {Object} original source map with the "sourcesContent" property overriden
  	 */function overrideSourcemapContent(map,source){return Object.assign({},map,{sourcesContent:[source]});}/**
  	 * Create the compilation meta object
  	 * @param { string } source - source code of the tag we will need to compile
  	 * @param { string } options - compiling options
  	 * @returns {Object} meta object
  	 */function createMeta(source,options){return {tagName:null,fragments:null,options:Object.assign({},DEFAULT_OPTIONS,{},options),source};}/**
  	 * Generate the output code source together with the sourcemap
  	 * @param { string } source - source code of the tag we will need to compile
  	 * @param { string } opts - compiling options
  	 * @returns { Output } object containing output code and source map
  	 */function compile(source,opts){if(opts===void 0){opts={};}const meta=createMeta(source,opts);const{options}=meta;const{code,map}=execute$1('template',options.template,meta,source);const{template:template$1,css:css$1,javascript:javascript$1}=parser$1(options).parse(code).output;// extend the meta object with the result of the parsing
  Object.assign(meta,{tagName:template$1.name,fragments:{template:template$1,css:css$1,javascript:javascript$1}});return compose(result=>Object.assign({},result,{meta}),result=>execute(result,meta),result=>Object.assign({},result,{map:overrideSourcemapContent(result.map,source)}),ast=>meta.ast=ast&&generateJavascript(ast,{sourceMapName:`${options.file}.map`,inputSourceMap:normaliseInputSourceMap(map)}),hookGenerator(template,template$1,code,meta),hookGenerator(javascript,javascript$1,code,meta),hookGenerator(css,css$1,code,meta))(createInitialInput(meta));}/**
  	 * Prepare the riot parser node transformers
  	 * @param   { Function } transformer - transformer function
  	 * @param   { Object } sourceNode - riot parser node
  	 * @param   { string } source - component source code
  	 * @param   { Object } meta - compilation meta information
  	 * @returns { Promise<Output> } object containing output code and source map
  	 */function hookGenerator(transformer,sourceNode,source,meta){if(// filter missing nodes
  !sourceNode||// filter nodes without children
  sourceNode.nodes&&!sourceNode.nodes.length||// filter empty javascript and css nodes
  !sourceNode.nodes&&!sourceNode.text){return result=>result;}return curry(transformer)(sourceNode,source,meta);}// This function can be used to register new preprocessors
  // a preprocessor can target either only the css or javascript nodes
  // or the complete tag source file ('template')
  const registerPreprocessor=register$1;// This function can allow you to register postprocessors that will parse the output code
  // here we can run prettifiers, eslint fixes...
  const registerPostprocessor=register;exports.compile=compile;exports.createInitialInput=createInitialInput;exports.registerPostprocessor=registerPostprocessor;exports.registerPreprocessor=registerPreprocessor;Object.defineProperty(exports,'__esModule',{value:true});});});var compiler$1 = unwrapExports(compiler);

  const GLOBAL_REGISTRY = '__riot_registry__';
  window[GLOBAL_REGISTRY] = {}; // evaluates a compiled tag within the global context

  function evaluate(js, url) {
    const node = document.createElement('script');
    const root = document.documentElement; // make the source available in the "(no domain)" tab
    // of Chrome DevTools, with a .js extension

    if (url) node.text = `${js}\n//# sourceURL=${url}.js`;
    root.appendChild(node);
    root.removeChild(node);
  } // cheap module transpilation


  function transpile(code) {
    return `(function (global){${code}})(this)`.replace('export default', 'return');
  }

  function inject(code, tagName, url) {
    evaluate(`window.${GLOBAL_REGISTRY}['${tagName}'] = ${transpile(code)}`, url);
    register(tagName, window[GLOBAL_REGISTRY][tagName]);
  }

  function compileFromString(string, options) {
    return compiler$1.compile(string, options);
  }

  async function compileFromUrl(url) {
    const response = await fetch(url);
    const code = await response.text();
    return compiler$1.compile(code, {
      file: url
    });
  }

  async function compile() {
    const scripts = $('script[type="riot"]');
    const urls = scripts.map(s => get(s, 'src') || get(s, 'data-src'));
    const tags = await Promise.all(urls.map(compileFromUrl));
    tags.forEach((_ref, i) => {
      let {
        code,
        meta
      } = _ref;
      const url = urls[i];
      const {
        tagName
      } = meta;
      inject(code, tagName, url);
    });
  }

  var riot_compiler = Object.assign({}, riot, {
    compile,
    inject,
    compileFromUrl,
    compileFromString
  });

  return riot_compiler;

}));
