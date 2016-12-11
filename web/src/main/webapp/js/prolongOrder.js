function prolongOrder(id) {
    $.ajax({
        url: 'prolongOrder&orderId=' + id,
        context: document.body
    }).done(function() {
        location.reload();
    });
}
