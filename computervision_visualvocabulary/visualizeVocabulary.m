addpath('./provided_code/');
directoryframes = 'frames/';
directorysift = 'sift/';
allsift = dir([directorysift '/*.mat']);
descriptorstaken = 100;
siftsample = []; 
j = 1;
for i = 1:6000; % iterate through all images and retrieve descriptors/features  
    sifts = [directorysift '/' allsift(i).name];
    load(sifts,'descriptors');
    if (~(size(descriptors) == 0))
        increment = (descriptorstaken - 1) + j;
        a = datasample(descriptors,descriptorstaken); % takes num descriptors from each image
        siftsample(j:increment,:) = a; % store into array 
        j = increment + 1; 
    end
end
k = 1500; % take kmeans to find best words 
[m,results,~] = kmeansML(k,single(siftsample'));
clustercenters = results';
save('kMeans.mat','clustercenters'); % store into file
randomcenters = randi(k,1,2); % to retrieve 2 words for matrix
for i = 1:2 
    center12 = clustercenters(randomcenters(i),:);
    ed = []; % store distances into this array 
    arr = []; % store indices into this array
    for f = 1:6000
        sift1 = [directorysift '/' allsift(f).name]; % loop through all frames and compare
        load(sift1, 'imname', 'descriptors'); 
        frame = rgb2gray(imread([directoryframes,imname]));
        if (~(size(descriptors) == 0)) % find eucildean distance between each descriptor / word
            x = dist2(descriptors,center12);
            [~,k] = min(x); % find minimum euc distances to store in array
            ed = [ed, k];
            arr = [arr, f];
        end
    end
    [a,b] = sort(ed); % find best frames to display
    figure
    for c = 1:25 % iterate through frames select top 25 
        d = a(c);
        i = arr(b(c)); % find specific descriptor
        sift1 = [directorysift '/' allsift(i).name];
        load(sift1, 'imname', 'positions', 'scales', 'orients'); 
        frame = rgb2gray(imread([directoryframes,imname])); 
        allpatches = getPatchFromSIFTParameters(positions(d,:), scales(d,:), orients(d,:), frame);
        subplot(5,5,c);
        imshow(allpatches); % display 25 patches in separate subplots
    end
end

