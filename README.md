# MMTASS1
Tên ứng dụng: Ứng dụng Chat MiChat
a. Ứng dụng cho phép hai người sử dụng ở hai máy khác nhau có thể chat với nhau
b. Một người có thể chat với nhiều người khác nhau tại cùng một thời điểm
c. Ứng dụng chat được xây dựng theo mô hình lai giữa Client-server và P2P: hệ
thống có một server trung tâm dùng cho việc đăng ký người sử dụng và quản lý
danh mục người sử dụng đang online, quá trình chat được thực hiện trực tiếp giữa
các client.
d. Ứng dụng cho phép truyền tải file(hình ảnh, âm thanh,sticker) trong quá trình chat giữa hai người
e. Ứng dụng phải sử dụng Chat Proprocol đã được nhóm định nghĩa trước đó.

+ Chức năng mở rộng: * Chatvoice(2 người)
                      * deploy lên mobile -> quét mã QR
                      * CHat ẩn danh(tạo một phòng, người dùng vào được bắt cặp ngẫu nhiên để chat với nhau hoàn toàn ẩn danh)

FRONT(java-elclipse): design giao diện (Ý)

BACK: server + database(firebase) (Huy)
 server: đăng ký tài khoản, lưu dữ liệu ng dùng, hiển thị list người dùng online. Khi người dùng A click vào một người dùng B
 thì sẽ lấy về từ server địa chỉ IP của người B và tạo kết nối P2P tới người B
 
CORE(giao thức chat) (Hiếu + Kiệt)
http://cs.berry.edu/~nhamid/p2p/
