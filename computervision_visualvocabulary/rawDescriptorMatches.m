load('twoFrameData.mat'); 
imageindices = selectRegion(im1, positions1); 
results = []; 
X = size(imageindices); 
for i = 1:X
    eucdist = dist2(descriptors1(imageindices(i),:),descriptors2(:,:)); % find euc dist between img descriptors 
    [~,x] = find(eucdist < 0.25); % set threshold to find descriptor matches 
    results = [results x]; % store into array
end
figure
imshow(im2); 
displaySIFTPatches(positions2(results,:),scales2(results),orients2(results),im2); 