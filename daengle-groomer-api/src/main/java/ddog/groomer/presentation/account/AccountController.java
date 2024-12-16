package ddog.groomer.presentation.account;

import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.groomer.application.AccountService;

import ddog.groomer.application.mapper.BeautyShopMapper;
import ddog.groomer.presentation.account.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@RestController
@RequestMapping("/api/groomer")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/join")
    public CommonResponseEntity<SignUpResp> signUp(@RequestBody SignUpReq request, HttpServletResponse response) {
        return success(accountService.signUp(request, response));
    }

    @GetMapping("/info")
    public CommonResponseEntity<ProfileInfo> getGroomerInfo(PayloadDto payloadDto) {
        return success(accountService.getGroomerInfo(payloadDto.getAccountId()));
    }

    @GetMapping("/modify-page")
    public CommonResponseEntity<ProfileInfo.UpdatePage> getUpdateInfo(PayloadDto payloadDto) {
        return success(accountService.getUpdatePage(payloadDto.getAccountId()));
    }

    @PatchMapping("/info")
    public CommonResponseEntity<AccountResp> updateInfo(@RequestBody UpdateInfoReq request, PayloadDto payloadDto) {
        return success(accountService.updateInfo(request, payloadDto.getAccountId()));
    }

    @GetMapping("/shop/{shopId}/info")
    public CommonResponseEntity<ShopInfo.UpdatePage> getShopInfo(@PathVariable Long shopId) {
        return success(accountService.getShopInfo(shopId));
    }

    @PatchMapping("/shop/info")
    public CommonResponseEntity<ShopInfo.UpdateResp> updateShopInfo(@RequestBody UpdateShopReq request) {
        return success(accountService.updateShopInfo(request));
    }
}
