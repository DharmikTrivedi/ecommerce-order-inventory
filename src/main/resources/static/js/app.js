// ShopKart OIMS - client side scripts (jQuery)
$(function () {

    var ctx = $('body').data('ctx') || '';

    function toast(msg, isError) {
        var t = $('#toast');
        t.text(msg).toggleClass('err', !!isError).stop(true, true).fadeIn(150);
        clearTimeout(t.data('timer'));
        t.data('timer', setTimeout(function () { t.fadeOut(300); }, 2800));
    }

    function money(n) {
        return Number(n).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function errorText(xhr) {
        return (xhr.responseJSON && xhr.responseJSON.message) ? xhr.responseJSON.message : 'Request failed, try again';
    }

    // ---------- general UI ----------

    // flash messages fade out after a few seconds
    setTimeout(function () { $('.flash-success, .flash-info').slideUp(300); }, 5000);

    // reports dropdown in the nav
    $('.dropdown-toggle').on('click', function (e) {
        e.preventDefault();
        $(this).parent().toggleClass('open');
    });
    $(document).on('click', function (e) {
        if (!$(e.target).closest('.dropdown').length) $('.dropdown').removeClass('open');
    });

    // any form/button with data-confirm asks before submitting (delete, cancel order etc.)
    $(document).on('submit', 'form[data-confirm]', function () {
        return confirm($(this).data('confirm'));
    });

    // ---------- client side validation ----------
    // rules come from attributes: required, type=email, data-pattern, min, max, data-minlen

    function checkField($f) {
        var val = $.trim($f.val() || '');
        var label = $f.data('label') || $f.attr('name');
        var msg = '';

        if ($f.prop('required') && val === '') {
            msg = label + ' is required';
        } else if (val !== '') {
            if ($f.attr('type') === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(val)) {
                msg = 'Enter a valid email address';
            } else if ($f.data('pattern') && !new RegExp('^(?:' + $f.data('pattern') + ')$').test(val)) {
                msg = $f.data('pattern-msg') || (label + ' format is wrong');
            } else if ($f.attr('type') === 'number') {
                var n = Number(val);
                if (isNaN(n)) msg = label + ' must be a number';
                else if ($f.attr('min') !== undefined && n < Number($f.attr('min'))) msg = label + ' must be at least ' + $f.attr('min');
                else if ($f.attr('max') !== undefined && n > Number($f.attr('max'))) msg = label + ' cannot be more than ' + $f.attr('max');
            } else if ($f.data('minlen') && val.length < Number($f.data('minlen'))) {
                msg = label + ' must be at least ' + $f.data('minlen') + ' characters';
            }
        }
        if (!msg && $f.data('server-taken')) {
            msg = $f.data('server-taken');
        }

        var $err = $f.siblings('.err-msg');
        if (msg) {
            $f.addClass('invalid');
            if (!$err.length) $err = $('<span class="err-msg"></span>').insertAfter($f);
            $err.text(msg);
        } else {
            $f.removeClass('invalid');
            $err.remove();
        }
        return !msg;
    }

    $('form.js-validate').each(function () {
        var $form = $(this);
        $form.on('blur change', 'input, select, textarea', function () { checkField($(this)); });
        $form.on('submit', function (e) {
            var ok = true;
            $form.find('input, select, textarea').not('[type=hidden]').each(function () {
                if (!checkField($(this))) ok = false;
            });
            if (!ok) {
                e.preventDefault();
                $form.find('.invalid').first().focus();
            }
        });
    });

    // customer form: check email with the server when the field loses focus (AJAX)
    $('#email[data-check-url]').on('blur', function () {
        var $f = $(this), val = $.trim($f.val());
        if (!val) return;
        $.get($f.data('check-url'), { email: val, id: $f.data('id') || '' }, function (res) {
            $f.data('server-taken', res.available ? '' : 'This email is already registered');
            checkField($f);
        });
    });

    // character counter for textareas with maxlength
    $('textarea[maxlength]').each(function () {
        var $t = $(this), max = $t.attr('maxlength');
        var $c = $('<span class="hint"></span>').insertAfter($t);
        var update = function () { $c.text($t.val().length + ' / ' + max + ' characters'); };
        $t.on('input', update);
        update();
    });

    // ---------- shop page ----------

    // add to cart without reloading the page
    $('.btn-add-cart').on('click', function () {
        var $btn = $(this), $card = $btn.closest('.product');
        var qty = parseInt($card.find('.qty-input').val(), 10) || 1;
        $btn.prop('disabled', true);
        $.post(ctx + '/api/cart/add', { productId: $btn.data('id'), qty: qty })
            .done(function (res) {
                $('#cartCount').text(res.cartCount);
                toast(res.message);
            })
            .fail(function (xhr) { toast(errorText(xhr), true); })
            .always(function () { $btn.prop('disabled', false); });
    });

    // check stock when the quantity is changed on a product card
    $('.product .qty-input').on('change', function () {
        var $in = $(this), $card = $in.closest('.product');
        $.getJSON(ctx + '/api/products/' + $card.data('id') + '/stock', function (p) {
            var $s = $card.find('.stock');
            if (p.stock === 0) {
                $s.html('<span class="out-stock">Out of stock</span>');
            } else if (parseInt($in.val(), 10) > p.stock) {
                $s.html('<span class="out-stock">Only ' + p.stock + ' left in stock</span>');
                $in.val(p.stock);
            } else {
                $s.html('<span class="in-stock">' + p.stock + ' in stock</span>');
            }
        });
    });

    // wishlist heart
    $('.wish').on('click', function () {
        var $h = $(this);
        $.post(ctx + '/api/wishlist/' + $h.data('id'))
            .done(function (res) {
                $h.toggleClass('on', res.inWishlist);
                toast(res.inWishlist ? 'Added to wishlist' : 'Removed from wishlist');
            })
            .fail(function (xhr) { toast(errorText(xhr), true); });
    });

    // live search suggestions
    var searchTimer;
    $('#liveSearch').on('input', function () {
        var q = $.trim($(this).val()), $box = $('#suggestions');
        clearTimeout(searchTimer);
        if (q.length < 2) { $box.hide(); return; }
        searchTimer = setTimeout(function () {
            $.getJSON(ctx + '/api/products/search', { q: q }, function (list) {
                $box.empty();
                if (!list.length) {
                    $box.append('<a href="#">No matching products</a>');
                }
                $.each(list, function (i, p) {
                    var a = $('<a>').attr('href', ctx + '/shop?q=' + encodeURIComponent(p.name));
                    a.append($('<span>').text(p.name + ' (' + p.sku + ')'));
                    a.append($('<span class="muted">').text('Rs. ' + money(p.price) + ' | ' + p.stock + ' left'));
                    $box.append(a);
                });
                $box.show();
            });
        }, 250);
    });
    $(document).on('click', function (e) {
        if (!$(e.target).closest('.search-box').length) $('#suggestions').hide();
    });

    // ---------- cart page ----------

    // +/- buttons and typed quantity update the line and total through AJAX
    function updateCartLine($row, qty) {
        var max = parseInt($row.data('max'), 10);
        if (qty < 1 || qty > max) {
            toast('Quantity must be between 1 and ' + max, true);
            return;
        }
        $.post(ctx + '/api/cart/' + $row.data('id') + '/qty', { qty: qty })
            .done(function (res) {
                $row.find('.qty-input').val(qty);
                $row.find('.line-subtotal').text(money(res.subtotal));
                $('#cartTotal').text(money(res.total));
                $('#cartCount').text(res.cartCount);
            })
            .fail(function (xhr) {
                toast(errorText(xhr), true);
                $row.find('.qty-input').val($row.find('.qty-input').data('last'));
            });
    }

    $('.cart-row .qty-input').each(function () { $(this).data('last', $(this).val()); });

    $('.cart-row .qty-minus, .cart-row .qty-plus').on('click', function () {
        var $row = $(this).closest('.cart-row'), $in = $row.find('.qty-input');
        var qty = parseInt($in.val(), 10) + ($(this).hasClass('qty-plus') ? 1 : -1);
        updateCartLine($row, qty);
    });
    $('.cart-row .qty-input').on('change', function () {
        updateCartLine($(this).closest('.cart-row'), parseInt($(this).val(), 10));
    });

    // ---------- checkout ----------
    $('#useOtherAddress').on('change', function () {
        var $addr = $('#shippingAddress');
        if (this.checked) {
            $addr.prop('readonly', false).val('').focus();
        } else {
            $addr.prop('readonly', true).val($addr.data('default'));
            checkField($addr);
        }
    });

    // ---------- return form ----------
    $('#returnQty').on('input change', function () {
        var price = Number($(this).data('price')), q = parseInt($(this).val(), 10) || 0;
        $('#refundPreview').text(money(price * q));
    }).trigger('change');
});
