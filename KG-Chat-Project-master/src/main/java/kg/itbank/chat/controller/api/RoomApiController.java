package kg.itbank.chat.controller.api;

import kg.itbank.chat.config.PrincipalDetail;
import kg.itbank.chat.dto.ResponseDto;
import kg.itbank.chat.exception.GlobalExceptionHandler;
import kg.itbank.chat.model.Room;
import kg.itbank.chat.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
public class RoomApiController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping
    public ResponseDto<?> createRoom(@AuthenticationPrincipal PrincipalDetail principal,
                                     @RequestBody Room room) {
        long result = roomService.create(room, principal.getId());
        if(result == -1) return new GlobalExceptionHandler().handleArgumentException(new Exception("debate ongoing"));
        return new ResponseDto<>(HttpStatus.OK.value(), result);
    }

    @PostMapping("/enter/{id}")
    public ResponseDto<?> enterRoom(@AuthenticationPrincipal PrincipalDetail principal, @PathVariable(value = "id") long id){
        return new ResponseDto<>(HttpStatus.OK.value(), roomService.becomeDebater(id, principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseDto<?> getRoomData(@PathVariable long id) {
        return new ResponseDto<>(HttpStatus.OK.value(), roomService.getRoom(id));
    }

    @PutMapping("/{id}")
    public ResponseDto<?> becomeDebater(@AuthenticationPrincipal PrincipalDetail principal,
                                        @PathVariable long roomId) {
        roomService.becomeDebater(principal.getId(), roomId);
        return new ResponseDto<>(HttpStatus.OK.value(), "success");
    }

    @PutMapping("/{roomId}/start")
    public ResponseDto<?> startDebate(@AuthenticationPrincipal PrincipalDetail principal,
                                      @PathVariable long roomId) {

        // ?????? ?????? ??? ?????? 30??? 5?????? ?????? ???????????? ???????????? ????????? ???????????? ?????? ??????
        // ????????? ????????? ????????? ??????????????? ?????? ???????????????
        // ???????????? ???????????? ????????????.
//        Timer timer = new Timer(true);
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                ChatMsgDto msg = ChatMsgDto.builder()
//                        .chatId(roomId)
//                        .sender(principal.getUser().getName())
//                        .senderId(principal.getId())
//                        .messageType("end")
//                        .message("")
//                        .build();
//                if(roomService.roomExists(roomId)){
//                    simpMessagingTemplate.convertAndSend("/topic/info/"+ roomId,msg);
//                    roomService.close(roomId, principal.getId());
//                }
//            }
//        };
//
//        timer.schedule(timerTask, 1805000);

        roomService.startDebate(roomId, principal.getId());
        return new ResponseDto<>(HttpStatus.OK.value(), "success");
    }

    @PutMapping("/{roomId}/end")
    public ResponseDto<?> endDebate(@AuthenticationPrincipal PrincipalDetail principal,
                                    @PathVariable long roomId) {
        roomService.resetDebate(roomId, principal.getId());
        return new ResponseDto<>(HttpStatus.OK.value(), "success");
    }

    @PutMapping("/{roomId}/close")
    public ResponseDto<?> close(@AuthenticationPrincipal PrincipalDetail principal,
                                    @PathVariable long roomId) {
        roomService.close(roomId, principal.getId());
        return new ResponseDto<>(HttpStatus.OK.value(), "success");
    }

//    @PutMapping("/{roomId}/leave")
//    public ResponseDto<?> leave(@AuthenticationPrincipal PrincipalDetail principal,
//                                    @PathVariable long roomId) {
//        return new ResponseDto<>(HttpStatus.OK.value(), roomService.leave(roomId, principal.getId()));
//    }

    @GetMapping("/{roomId}/stats")
    public  ResponseDto<?> stats(@PathVariable long roomId){
        return new ResponseDto<>(HttpStatus.OK.value(), roomService.defaultInfo(roomId));
    }

}
