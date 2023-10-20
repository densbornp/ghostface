import { Component, OnInit } from '@angular/core';
import { ConversionService } from '../services/conversion.service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { CookieModalComponent } from '../cookie-modal/cookie-modal.component';
import { BehaviorSubject } from 'rxjs';
import { Constants } from '../constants';

@Component({
    selector: 'app-conversion',
    templateUrl: './conversion.component.html',
    styleUrls: ['./conversion.component.scss']
})
export class ConversionComponent implements OnInit {

    imagePath: BehaviorSubject<string> = new BehaviorSubject("assets/img/no-image-original.png");
    tmpImagePath: BehaviorSubject<string> = new BehaviorSubject("assets/img/no-image-tmp.png");
    choosenFile: string;
    infoText: string;
    INFO_FINISHED: "Finished!";
    INFO_DOWNLOAD: "Download successful!";
    INFO_UPLOAD: "Upload successful!";
    INFO_CONVERSION: "Conversion successful!";
    INFO_CONVERSION_FAILED: "Conversion error occured!";
    downloadBtnDisabled = true;
    convertSectionDisabled = true;
    convertBtnDisabled = false;
    private config = {
        animated: true,
        ignoreBackdropClick: true,
        'class': 'modal-dialog-centered'
    };
    conversionType = "none";
    preTrainedModel = "default";
    minNeighbours = 3;
    imageScaleFactor = 1.05;

    constructor(private conversionService: ConversionService, private modalService: BsModalService) { }

    ngOnInit(): void {
        this.choosenFile = Constants.CHOOSE_FILE;
        this.infoText = null;
        this.getUploadedImage();
        this.getTmpImage();
    }

    public uploadImage(event: any) {
        let uploadedFile = event.srcElement.files[0];
        this.choosenFile = uploadedFile?.name;
        let formData = new FormData();
        formData.append("imageFile", uploadedFile);
        this.conversionService.uploadFile(formData).subscribe(() => {
            this.downloadBtnDisabled = false;
            this.convertSectionDisabled = false;
            this.infoText = this.INFO_UPLOAD;
            this.getUploadedImage();
            this.getTmpImage();
        }, (error: any) => {
            if (error.error === Constants.COOKIE_MISSING) {
                this.modalService.show(CookieModalComponent, this.config);
            } else {
                alert("Upload failed: " + error.error);
            }
            this.infoText = null;
        });
    }

    public downloadImage() {
        this.conversionService.downloadFile().subscribe((data: any) => {
            if (data != null) {
                this.infoText = this.INFO_DOWNLOAD;
                const file = new Blob([data], { type: data.type });
                let a = document.createElement("a");
                const url = URL.createObjectURL(file);
                a.href = url;
                a.download = "convertedImage." + data.type.slice(6);
                document.body.appendChild(a);
                a.click();
                setTimeout(function () {
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                }, 0);
            }
        }, (error: any) => {
            alert("Download ended with an error: " + error.error);
            this.infoText = null;
        });
    }

    private getUploadedImage() {
        this.conversionService.getImage().subscribe((data: any) => {
            if (data != null) {
                // Add the current time to break the image cache
                this.imagePath.next("/image?" + new Date().getTime());
            } else {
                this.imagePath.next("assets/img/no-image-original.png");
            }
        });
    }

    private getTmpImage() {
        this.conversionService.getTmpImage().subscribe((data: any) => {
            if (data != null) {
                // Add the current time to break the image cache
                this.tmpImagePath.next("/tmpImage?" + new Date().getTime());
                this.downloadBtnDisabled = false;
                this.convertSectionDisabled = false;
            } else {
                this.tmpImagePath.next("assets/img/no-image-tmp.png");
            }
        });
    }

    public updateConversionType(value: string) {
        this.conversionType = value;
    }

    public updatePreTrainedModel(value: string) {
        this.preTrainedModel = value;
    }

    public updateSensitivityText(value: number) {
        this.minNeighbours = value;
    }

    public updateScaleText(value: number) {
        this.imageScaleFactor = value;
    }

    public convertImage() {
        this.convertBtnDisabled = true;
        this.conversionService.convertImage(this.conversionType, this.preTrainedModel, this.minNeighbours, this.imageScaleFactor).subscribe(data => {
            if (data != null) {
                this.infoText = this.INFO_CONVERSION;
                this.getTmpImage();
            } else {
                this.infoText = this.INFO_CONVERSION_FAILED;
            }
            this.convertBtnDisabled = false;
        }, error => {this.convertBtnDisabled = false;});
    }
}
