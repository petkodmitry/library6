function orderToHome(id) {
    $.ajax({
        url: 'orderToHome&bookId=' + id,
        context: document.body
    }).done(function() {
        location.reload();
    });
}
