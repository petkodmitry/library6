function orderToReadingRoom(id) {
    $.ajax({
        url: 'orderToReadingRoom&bookId='+id,
        context: document.body
    }).done(function() {
        location.reload();
    });
}
