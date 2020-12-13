package com.fn.bi.report.login.controller;

import com.fn.bi.backend.common.dto.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@Validated
public class AdminDashbordController {

//    @Autowired
//    private LitemallUserService userService;
//    @Autowired
//    private LitemallGoodsService goodsService;
//    @Autowired
//    private LitemallGoodsProductService productService;
//    @Autowired
//    private LitemallOrderService orderService;

    @GetMapping("")
    public Object info() {
//        int userTotal = userService.count();
//        int goodsTotal = goodsService.count();
//        int productTotal = productService.count();
//        int orderTotal = orderService.count();
        Map<String, Integer> data = new HashMap<>();
        data.put("userTotal", 123);
        data.put("goodsTotal", 32);
        data.put("productTotal", 42);
        data.put("orderTotal", 4342);

        return Result.ok(data);
    }

}
