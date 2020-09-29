package cn.xzxy.lewy.zk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel("城市详情入参")
public class CountryDetailReq {

    @ApiModelProperty(value = "城市ID")
    @NotBlank(message = "城市ID不能为空")
    @Size(max = 40,message = "城市ID最大长度为5")
    private int countryId;
}
